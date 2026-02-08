package digdaserver.domain.member.presentation.controller

import digdaserver.domain.member.application.service.MemberService
import digdaserver.domain.member.presentation.dto.member.res.MyPageRes
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/member")
@Tag(name = "Member", description = "사용자 전용 API")
class MemberController(
    private val memberService: MemberService
) {

    @Operation(summary = "마이페이지 API", description = "마이페이지를 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "마이페이지 조회 성공"),
            ApiResponse(responseCode = "404", description = "유저 존재하지 않음")
        ]
    )
    @GetMapping
    fun infoMyPage(@AuthenticationPrincipal userId: String): ResponseEntity<MyPageRes> {
        return ResponseEntity.ok(memberService.infoMyPage(userId))
    }
}
