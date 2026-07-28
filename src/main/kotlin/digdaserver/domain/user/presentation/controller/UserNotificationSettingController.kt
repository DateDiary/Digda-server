package digdaserver.domain.user.presentation.controller

import digdaserver.domain.user.application.service.UserNotificationSettingService
import digdaserver.domain.user.presentation.dto.req.UpdateNotificationSettingRequest
import digdaserver.domain.user.presentation.dto.res.NotificationSettingResponse
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
@RequestMapping("/users/me/notification-settings")
@Tag(name = "User", description = "사용자 API")
class UserNotificationSettingController(
    private val userNotificationSettingService: UserNotificationSettingService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "알림 설정 조회", description = "로그인한 사용자의 알림 설정을 조회합니다.")
    @GetMapping
    fun getNotificationSetting(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<NotificationSettingResponse> {
        log.info("api=GET /users/me/notification-settings, userId={}", currentUserId())
        val response = userNotificationSettingService.getNotificationSetting(UUID.fromString(userId))
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "알림 설정 수정", description = "알림 설정을 수정합니다. 변경할 필드만 전송합니다.")
    @PutMapping
    fun updateNotificationSetting(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: UpdateNotificationSettingRequest
    ): ResponseEntity<NotificationSettingResponse> {
        log.info(
            "api=PUT /users/me/notification-settings, userId={}, push={}, schedule={}, " +
                "diary={}, comment={}, mochi={}",
            currentUserId(),
            request.pushEnabled,
            request.scheduleNotification,
            request.diaryNotification,
            request.commentNotification,
            request.mochiNotification
        )
        val response = userNotificationSettingService.updateNotificationSetting(UUID.fromString(userId), request)
        log.info("api=PUT /users/me/notification-settings 완료, userId={}", currentUserId())
        return ResponseEntity.ok(response)
    }
}
