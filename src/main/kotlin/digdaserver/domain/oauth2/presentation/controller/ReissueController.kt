package digdaserver.domain.oauth2.presentation.controller

import digdaserver.domain.oauth2.application.service.ReissueService
import digdaserver.domain.oauth2.presentation.dto.res.LoginToken
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "Token", description = "토큰 재발급 API")
class ReissueController(
    private val reissueService: ReissueService
) {

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새로운 Access/Refresh Token을 발급합니다.")
    @PostMapping("/app/reissue")
    fun reissueApp(@RequestBody request: ReissueRequest): ResponseEntity<LoginToken> {
        val loginToken = reissueService.reissue(request.refreshToken)
        return ResponseEntity.ok(loginToken)
    }

    data class ReissueRequest(
        val refreshToken: String
    )
}
