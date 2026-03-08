package digdaserver.domain.member.presentation.controller

import digdaserver.domain.member.application.service.MemberService
import digdaserver.domain.member.presentation.dto.member.req.UpdateNameReq
import digdaserver.domain.member.presentation.dto.member.res.MyPageRes
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/member")
@Tag(name = "Member", description = "사용자 전용 API")
class MemberController(
    private val memberService: MemberService
) {

    @Operation(summary = "마이페이지 조회", description = "내 프로필 정보를 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "404", description = "유저 존재하지 않음")
        ]
    )
    @GetMapping
    fun infoMyPage(@AuthenticationPrincipal userId: String): ResponseEntity<MyPageRes> {
        return ResponseEntity.ok(memberService.infoMyPage(userId))
    }

    @Operation(summary = "이름 변경", description = "사용자 이름을 변경합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "이름 변경 성공"),
            ApiResponse(responseCode = "404", description = "유저 존재하지 않음")
        ]
    )
    @PatchMapping("/name")
    fun updateName(
        @AuthenticationPrincipal userId: String,
        @RequestBody @Valid req: UpdateNameReq
    ): ResponseEntity<MyPageRes> {
        return ResponseEntity.ok(memberService.updateName(userId, req.name))
    }

    @Operation(summary = "프로필 이미지 변경", description = "프로필 이미지를 변경합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "이미지 변경 성공"),
            ApiResponse(responseCode = "404", description = "유저 존재하지 않음")
        ]
    )
    @PatchMapping("/profile-image", consumes = ["multipart/form-data"])
    fun updateProfileImage(
        @AuthenticationPrincipal userId: String,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<MyPageRes> {
        return ResponseEntity.ok(memberService.updateProfileImage(userId, file))
    }

    @Operation(summary = "회원 탈퇴", description = "계정을 삭제합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
            ApiResponse(responseCode = "404", description = "유저 존재하지 않음")
        ]
    )
    @DeleteMapping
    fun deleteAccount(@AuthenticationPrincipal userId: String): ResponseEntity<Void> {
        memberService.deleteAccount(userId)
        return ResponseEntity.noContent().build()
    }
}
