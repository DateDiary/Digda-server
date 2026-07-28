package digdaserver.domain.user.presentation.controller

import digdaserver.domain.user.application.service.UserProfileService
import digdaserver.domain.user.presentation.dto.req.UpdateProfileRequest
import digdaserver.domain.user.presentation.dto.res.MyProfileResponse
import digdaserver.global.infra.logging.LogUserContext.currentUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "사용자 API")
class UserProfileController(
    private val userProfileService: UserProfileService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @GetMapping("/me")
    fun getMyProfile(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<MyProfileResponse> {
        log.info("api=GET /users/me, userId={}", currentUserId())
        val response = userProfileService.getMyProfile(UUID.fromString(userId))
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "프로필 수정", description = "닉네임, 상태 메시지, 프로필 이미지를 수정합니다.")
    @PutMapping("/me")
    fun updateProfile(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<MyProfileResponse> {
        // 닉네임 값은 남기지 않는다(사용자 작성 텍스트). 무엇을 바꿨는지만 기록.
        log.info(
            "api=PUT /users/me, userId={}, nameChanged={}, profileImageChanged={}",
            currentUserId(),
            request.name != null,
            request.profileImageId != null
        )
        val response = userProfileService.updateProfile(UUID.fromString(userId), request)
        log.info("api=PUT /users/me 완료, userId={}", currentUserId())
        return ResponseEntity.ok(response)
    }
}
