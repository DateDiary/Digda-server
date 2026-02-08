package digdaserver.domain.oauth2.presentation.controller

import digdaserver.domain.oauth2.application.service.SocialLoginService
import digdaserver.domain.oauth2.domain.entity.SocialProvider
import digdaserver.domain.oauth2.presentation.dto.req.SocialTokenRequest
import digdaserver.domain.oauth2.presentation.dto.res.LoginToken
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "Social Login", description = "소셜 로그인 API")
class SocialLoginController(
    private val socialLoginService: SocialLoginService
) {

    private val log = LoggerFactory.getLogger(SocialLoginController::class.java)

    @Operation(summary = "[APP] 소셜 토큰으로 로그인", description = "앱에서 받은 소셜 토큰으로 로그인을 처리합니다.")
    @PostMapping("/oauth2/login/{provider}")
    fun socialLoginWithToken(
        @PathVariable provider: SocialProvider,
        @RequestBody request: SocialTokenRequest
    ): ResponseEntity<LoginToken> {
        log.info("소셜 토큰 로그인: provider={}", provider)

        val loginToken = socialLoginService.loginWithToken(provider, request)

        log.info("✅✅ACToken={}", loginToken.accessToken)
        log.info("✅✅RFToken={}", loginToken.refreshToken)

        return ResponseEntity.ok(loginToken)
    }

    @Operation(summary = "[TEST] 소셜 로그인 URL 조회", description = "각 플랫폼별 소셜 로그인 URL을 반환합니다. (테스트용)")
    @GetMapping("/test/oauth2/login/{provider}")
    fun getLoginUrl(@PathVariable provider: SocialProvider): ResponseEntity<String> {
        log.info("소셜 로그인 URL 요청: {}", provider)
        val loginUrl = socialLoginService.getLoginUrl(provider)
        return ResponseEntity.ok(loginUrl)
    }

    // 테스트용 콜백 API
    @Operation(summary = "[TEST] 소셜 로그인 콜백", description = "테스트용 콜백 API")
    @GetMapping("/callback/{provider}")
    fun socialLogin(
        @PathVariable provider: SocialProvider,
        @RequestParam("code") code: String
    ): ResponseEntity<LoginToken> {
        log.info("소셜 로그인: provider={}, code={}", provider, code)
        val loginToken = socialLoginService.login(provider, code)
        return ResponseEntity.ok(loginToken)
    }

    @Operation(summary = "[TEST] 소셜 로그인 콜백", description = "테스트용 애플 콜백 API")
    @PostMapping("/callback/apple")
    fun socialAppleLogin(
        @RequestParam("code") code: String
    ): ResponseEntity<LoginToken> {
        val loginToken = socialLoginService.login(SocialProvider.APPLE, code)
        return ResponseEntity.ok(loginToken)
    }
}
