package digdaserver.domain.oauth2.application.service.impl.oauth

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import digdaserver.domain.oauth2.application.service.OAuth2Service
import digdaserver.domain.oauth2.domain.entity.SocialProvider
import digdaserver.domain.oauth2.infra.AppleJwtUtils
import digdaserver.domain.oauth2.presentation.dto.req.SocialTokenRequest
import digdaserver.domain.oauth2.presentation.dto.res.oatuh.KakaoTokenResponse
import digdaserver.domain.oauth2.presentation.dto.res.oatuh.KakaoUserResponse
import digdaserver.global.infra.exception.error.ErrorCode
import digdaserver.global.infra.exception.error.DigdaServerException
import digdaserver.global.infra.feignclient.ios.AppleOAuth2FeignClient
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

@Service
@Transactional
class AppleOAuth2ServiceImpl(
    private val appleOAuth2FeignClient: AppleOAuth2FeignClient,
    private val appleJwtUtils: AppleJwtUtils,
    private val objectMapper: ObjectMapper,
    @Value("\${oauth2.apple.client-id}")
    private val clientId: String,
    @Value("\${oauth2.apple.redirect-uri}")
    private val redirectUri: String
) : OAuth2Service {

    private val log = LoggerFactory.getLogger(AppleOAuth2ServiceImpl::class.java)

    override fun getLoginUrl(): String {
        val url = StringBuilder()
        url.append("https://appleid.apple.com/auth/authorize")
        url.append("?client_id=").append(URLEncoder.encode(clientId, StandardCharsets.UTF_8))
        url.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8))
        url.append("&response_type=code")
        url.append("&scope=").append(URLEncoder.encode("name email", StandardCharsets.UTF_8))
        url.append("&response_mode=form_post")

        return url.toString()
    }

    override fun getTokens(code: String): KakaoTokenResponse {
        val clientSecret = appleJwtUtils.generateClientSecret()

        val appleResponse = appleOAuth2FeignClient.getAccessToken(
            "authorization_code",
            clientId,
            clientSecret,
            code,
            redirectUri
        )

        return KakaoTokenResponse(
            appleResponse.accessToken,
            appleResponse.refreshToken,
            appleResponse.idToken,
            appleResponse.expiresIn
        )
    }

    override fun refreshTokens(refreshToken: String): KakaoTokenResponse {
        val clientSecret = appleJwtUtils.generateClientSecret()

        val appleResponse = appleOAuth2FeignClient.refreshToken(
            "refresh_token",
            clientId,
            clientSecret,
            refreshToken
        )

        return KakaoTokenResponse(
            appleResponse.accessToken,
            appleResponse.refreshToken,
            appleResponse.idToken,
            appleResponse.expiresIn
        )
    }

    override fun getUserInfo(accessToken: String): KakaoUserResponse {
        throw DigdaServerException(ErrorCode.ID_ERROR_TOKEN)
    }

    override fun getUserInfoFromIdToken(idToken: String): KakaoUserResponse {
        try {
            log.info("Apple ID Token 파싱 시작")
            return parseIdToken(idToken)
        } catch (e: Exception) {
            log.error("ID Token 파싱 실패", e)
            throw DigdaServerException(ErrorCode.ID_ERROR_TOKEN)
        }
    }

    override fun validateToken(accessToken: String): Boolean {
        log.warn("Apple Access Token 검증은 구현되지 않았습니다. ID Token 검증을 사용하세요.")
        return accessToken.isNotBlank()
    }

    override fun convertToTokenResponse(tokenRequest: SocialTokenRequest): KakaoTokenResponse {
        return KakaoTokenResponse(
            tokenRequest.accessToken,
            tokenRequest.refreshToken,
            tokenRequest.idToken,
            tokenRequest.expiresIn
        )
    }

    override fun getProvider(): SocialProvider = SocialProvider.APPLE

    private fun parseIdToken(idToken: String): KakaoUserResponse {
        try {
            if (idToken.isBlank()) {
                throw DigdaServerException(ErrorCode.ID_ERROR_TOKEN)
            }

            val parts = idToken.split(".")
            if (parts.size != 3) {
                log.error("JWT 토큰 형식이 잘못됨. 파트 개수: {}", parts.size)
                throw DigdaServerException(ErrorCode.JWT_ERROR_TOKEN)
            }

            var payloadPart = parts[1]
            while (payloadPart.length % 4 != 0) {
                payloadPart += "="
            }

            val decodedBytes = try {
                Base64.getUrlDecoder().decode(payloadPart)
            } catch (e: Exception) {
                log.error("Base64 디코딩 실패", e)
                throw DigdaServerException(ErrorCode.ID_ERROR_TOKEN)
            }

            val payload = String(decodedBytes, StandardCharsets.UTF_8)
            val claims: JsonNode = objectMapper.readTree(payload)

            val sub = claims["sub"]?.asText()
                ?: throw DigdaServerException(ErrorCode.ID_ERROR_TOKEN)

            val email = claims["email"]?.asText()

            var name: String? = claims["name"]?.asText()
            if (name == null && email != null && email.contains("@")) {
                name = email.substringBefore("@")
            }

            return KakaoUserResponse(
                sub,
                KakaoUserResponse.KakaoAccount(
                    KakaoUserResponse.Profile(
                        name ?: "Apple User",
                        null
                    ),
                    email
                )
            )
        } catch (e: Exception) {
            log.error("Apple ID Token 파싱 실패", e)
            throw DigdaServerException(ErrorCode.ID_ERROR_TOKEN)
        }
    }
}
