package digdaserver.domain.oauth2.presentation.controller

import digdaserver.domain.oauth2.application.service.LogoutService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "Auth", description = "인증 관련 API")
class LogoutController(
    private val logoutService: LogoutService
) {

    @Operation(summary = "[APP] 로그아웃", description = "사용자를 로그아웃하고 토큰을 무효화합니다.")
    @PostMapping("/app/oauth2/logout")
    fun logout(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: LogoutRequest
    ): ResponseEntity<Void> {
        logoutService.logout(userId, request.refreshToken)
        return ResponseEntity.ok().build()
    }

    data class LogoutRequest(
        val refreshToken: String
    )
}
