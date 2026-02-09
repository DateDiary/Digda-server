package digdaserver.domain.oauth2.application.service.impl.oauth

import digdaserver.domain.member.domain.entity.Member
import digdaserver.domain.member.domain.entity.Role
import digdaserver.domain.member.domain.repository.MemberRepository
import digdaserver.domain.oauth2.application.service.CreateAccessTokenAndRefreshTokenService
import digdaserver.domain.oauth2.application.service.OAuth2Service
import digdaserver.domain.oauth2.application.service.SocialLoginService
import digdaserver.domain.oauth2.domain.entity.SocialProvider
import digdaserver.domain.oauth2.presentation.dto.req.SocialTokenRequest
import digdaserver.domain.oauth2.presentation.dto.res.LoginToken
import digdaserver.domain.oauth2.presentation.dto.res.oatuh.KakaoTokenResponse
import digdaserver.domain.oauth2.presentation.dto.res.oatuh.KakaoUserResponse
import digdaserver.domain.point.domain.entity.Point
import digdaserver.domain.point.domain.repository.PointRepository
import digdaserver.global.infra.exception.error.ErrorCode
import digdaserver.global.infra.exception.error.DigdaServerException
import digdaserver.global.jwt.domain.entity.SocialToken
import digdaserver.global.jwt.domain.repository.SocialTokenRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Transactional
class SocialLoginServiceImpl(
    oauth2Services: List<OAuth2Service>,
    private val tokenService: CreateAccessTokenAndRefreshTokenService,
    private val userRepository: MemberRepository,
    private val socialTokenRepository: SocialTokenRepository,
    private val pointRepository: PointRepository,

    @Value("\${oauth2.apple.profile}")
    private val appleProfile: String
) : SocialLoginService {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val oauth2ServicesMap: Map<SocialProvider, OAuth2Service> =
        oauth2Services.associateBy { it.getProvider() }

    override fun loginWithToken(provider: SocialProvider, tokenRequest: SocialTokenRequest): LoginToken {
        val oauth2Service = getOAuth2Service(provider)

        log.info("소셜 로그인 시작: provider={}", provider)

        val isValidToken = if (provider == SocialProvider.APPLE) {
            tokenRequest.idToken?.let { oauth2Service.validateToken(it) }
        } else {
            oauth2Service.validateToken(tokenRequest.accessToken)
        }

        if (!isValidToken!!) {
            throw DigdaServerException(ErrorCode.INVALID_PARAMETER, "유효하지 않은 토큰입니다")
        }

        val userResponse: KakaoUserResponse =
            if (provider == SocialProvider.APPLE && tokenRequest.idToken != null) {
                oauth2Service.getUserInfoFromIdToken(tokenRequest.idToken)
            } else {
                oauth2Service.getUserInfo(tokenRequest.accessToken)
            }

        log.info("사용자 정보 획득 완료: userId={}, provider={}", userResponse.id, provider)

        val tokenResponse = oauth2Service.convertToTokenResponse(tokenRequest)

        val user = createSocialUser(provider, tokenResponse, userResponse)

        return tokenService.createAccessTokenAndRefreshToken(
            user.id,
            user.role,
            user.email
        )
    }

    override fun login(provider: SocialProvider, code: String): LoginToken {
        val oauth2Service = getOAuth2Service(provider)

        val tokenResponse = oauth2Service.getTokens(code)

        log.info("===== 소셜에서 받은 토큰 정보 =====")
        log.info("Access Token: {}", tokenResponse.accessToken)
        log.info("Refresh Token: {}", tokenResponse.refreshToken)
        log.info("Expires In: {}", tokenResponse.expiresIn)
        log.info("================================")

        // accessToken 없으면 바로 예외
        val accessToken = tokenResponse.accessToken
            ?: throw DigdaServerException(ErrorCode.INVALID_PARAMETER, "access token 없음")

        val userResponse = oauth2Service.getUserInfo(accessToken)

        val user = createSocialUser(provider, tokenResponse, userResponse)

        return tokenService.createAccessTokenAndRefreshToken(
            user.id,
            user.role,
            user.email
        )
    }

    override fun getLoginUrl(provider: SocialProvider): String {
        val oauth2Service = getOAuth2Service(provider)
        val url = oauth2Service.getLoginUrl()
        log.info("생성된 로그인 URL: {}", url)
        return url
    }

    private fun getOAuth2Service(provider: SocialProvider): OAuth2Service {
        val service = oauth2ServicesMap[provider]
            ?: throw DigdaServerException(ErrorCode.INVALID_PARAMETER)

        log.info("OAuth2Service 조회 성공: {}", service::class.simpleName)
        return service
    }

    private fun createSocialUser(
        provider: SocialProvider,
        tokenResponse: KakaoTokenResponse,
        userResponse: KakaoUserResponse
    ): Member {
        if (userResponse.id == null || userResponse.id.isBlank()) {
            throw IllegalArgumentException("소셜 사용자 ID가 유효하지 않습니다.")
        }

        var user = userRepository.findById(userResponse.id).orElse(null)

        if (user == null) {
            val existingEmail = userResponse.getEmail()?.let { userRepository.findByEmail(it) }
            if (existingEmail != null) {
                if (existingEmail.isPresent) {
                    throw DigdaServerException(ErrorCode.DUPLICATE_EMAIL)
                }
            }

            val profileImage = if (provider == SocialProvider.APPLE) appleProfile else userResponse.getProfile()

            user = userResponse.getEmail()?.let {
                Member(
                    id = userResponse.id,
                    email = it,
                    name = userResponse.getName()!!,
                    profile = profileImage,
                    socialProvider = provider,
                    role = Role.USER
                )
            }

            userRepository.save(user)

            pointRepository.save(Point(member = user))
        } else {
            if (user.email != userResponse.getEmail()) {
                val duplicate = userResponse.getEmail()?.let { userRepository.findByEmail(it) }
                if (duplicate != null) {
                    if (duplicate.isPresent && duplicate.get().id != user.id) {
                        throw DigdaServerException(ErrorCode.DUPLICATE_EMAIL)
                    }
                }
            }

            userResponse.getEmail()?.let { user.updateEmail(it) }
            user.updateIsActivateNameFlag()
        }

        saveSocialToken(user.id, provider, tokenResponse)

        return user
    }

    private fun saveSocialToken(
        userId: String,
        provider: SocialProvider,
        tokenResponse: KakaoTokenResponse
    ) {
        val expiresAt = LocalDateTime.now().plusSeconds(
            tokenResponse.expiresIn ?: 3600L
        )

        val socialToken = tokenResponse.accessToken?.let {
            tokenResponse.refreshToken?.let { it1 ->
                SocialToken.of(
                    userId = userId,
                    provider = provider,
                    accessToken = it,
                    refreshToken = it1,
                    expiresIn = expiresAt
                )
            }
        }

        socialTokenRepository.deleteByUserIdAndProvider(userId, provider)
        if (socialToken == null) throw DigdaServerException(ErrorCode.JWT_ERROR_TOKEN)
        socialTokenRepository.save(socialToken)

        log.info("소셜 토큰 저장 완료: userId={}, provider={}", userId, provider)
    }
}
