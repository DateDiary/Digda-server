package digdaserver.domain.notification.presentation.controller

import digdaserver.domain.notification.application.service.NotificationService
import digdaserver.domain.notification.presentation.dto.res.NotificationRes
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/notification")
@Tag(name = "Notification", description = "알림 전용 API")
class NotificationController(
    private val notificationService: NotificationService
) {

    @Operation(summary = "내 알림 목록 조회 API", description = "나의 알림 목록을 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "알림 목록 조회 성공")
        ]
    )
    @GetMapping
    fun getMyNotifications(
        @AuthenticationPrincipal userId: String,
        @RequestParam(required = false, defaultValue = "false") unreadOnly: Boolean
    ): ResponseEntity<List<NotificationRes>> {
        return ResponseEntity.ok(notificationService.getMyNotifications(userId, unreadOnly))
    }

    @Operation(summary = "알림 읽음 처리 API", description = "알림을 읽음 처리합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
            ApiResponse(responseCode = "406", description = "알림 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "권한 없음")
        ]
    )
    @PatchMapping("/{notificationId}/read")
    fun markAsRead(
        @AuthenticationPrincipal userId: String,
        @PathVariable notificationId: Long
    ): ResponseEntity<NotificationRes> {
        return ResponseEntity.ok(notificationService.markAsRead(userId, notificationId))
    }

    @Operation(summary = "알림 삭제 API", description = "알림을 삭제합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "알림 삭제 성공"),
            ApiResponse(responseCode = "406", description = "알림 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "권한 없음")
        ]
    )
    @DeleteMapping("/{notificationId}")
    fun deleteNotification(
        @AuthenticationPrincipal userId: String,
        @PathVariable notificationId: Long
    ): ResponseEntity<Unit> {
        notificationService.deleteNotification(userId, notificationId)
        return ResponseEntity.noContent().build()
    }
}
