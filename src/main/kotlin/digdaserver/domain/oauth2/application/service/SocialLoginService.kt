package digdaserver.domain.oauth2.application.service

import digdaserver.domain.oauth2.domain.entity.SocialProvider
import digdaserver.domain.oauth2.presentation.dto.req.SocialTokenRequest
import digdaserver.domain.oauth2.presentation.dto.res.LoginToken

interface SocialLoginService {

    // 앱에서 AccessToken / IDToken을 직접 전달받는 방식
    fun loginWithToken(provider: SocialProvider, tokenRequest: SocialTokenRequest): LoginToken

    // 기존 Authorization Code Flow 로그인
    fun login(provider: SocialProvider, code: String): LoginToken

    // 로그인 URL 조회 (테스트)
    fun getLoginUrl(provider: SocialProvider): String
}
