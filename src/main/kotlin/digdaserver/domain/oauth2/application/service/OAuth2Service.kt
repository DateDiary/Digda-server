package digdaserver.domain.oauth2.application.service

import digdaserver.domain.oauth2.domain.entity.SocialProvider
import digdaserver.domain.oauth2.presentation.dto.req.SocialTokenRequest
import digdaserver.domain.oauth2.presentation.dto.res.oatuh.KakaoTokenResponse
import digdaserver.domain.oauth2.presentation.dto.res.oatuh.KakaoUserResponse

interface OAuth2Service {

    // 기존 Authorization Code Flow 방식
    fun getLoginUrl(): String
    fun getTokens(code: String): KakaoTokenResponse
    fun refreshTokens(refreshToken: String): KakaoTokenResponse

    // Access Token → 사용자 정보 조회
    fun getUserInfo(accessToken: String): KakaoUserResponse

    // Apple: ID Token 기반 사용자 정보 조회
    fun getUserInfoFromIdToken(idToken: String): KakaoUserResponse

    // 토큰 검증
    fun validateToken(accessToken: String): Boolean

    // SocialTokenRequest → KakaoTokenResponse 변환
    fun convertToTokenResponse(tokenRequest: SocialTokenRequest): KakaoTokenResponse

    fun getProvider(): SocialProvider
}
