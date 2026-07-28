package digdaserver.domain.notification.presentation.controller

import digdaserver.domain.notification.application.service.NotificationService
import digdaserver.domain.notification.presentation.dto.req.UpdateNotificationReadRequest
import digdaserver.domain.notification.presentation.dto.res.NotificationListResponse
import digdaserver.global.infra.logging.LogUserContext.currentUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "Notification", description = "알림 API")
class NotificationController(
    private val notificationService: NotificationService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "알림 목록 조회", description = "내 알림 목록을 최신순으로 조회합니다.")
    @GetMapping("/notifications")
    fun getNotifications(
        @AuthenticationPrincipal userId: String,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): ResponseEntity<NotificationListResponse> {
        log.info(
            "api=GET /notifications, userId={}, limit={}, offset={}",
            currentUserId(),
            limit,
            offset
        )
        val response = notificationService.getNotifications(UUID.fromString(userId), limit, offset)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    @PatchMapping("/notifications/{notificationId}")
    fun markAsRead(
        @AuthenticationPrincipal userId: String,
        @PathVariable notificationId: Long,
        @RequestBody request: UpdateNotificationReadRequest
    ): ResponseEntity<Void> {
        log.info(
            "api=PATCH /notifications/{}, userId={}, isRead={}",
            notificationId,
            currentUserId(),
            request.isRead
        )
        notificationService.markAsRead(UUID.fromString(userId), notificationId, request.isRead)
        log.info(
            "api=PATCH /notifications/{} 완료, userId={}, isRead={}",
            notificationId,
            currentUserId(),
            request.isRead
        )
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "전체 알림 읽음 처리", description = "모든 알림을 읽음 처리합니다.")
    @PostMapping("/notifications/read-all")
    fun markAllAsRead(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<Void> {
        log.info("api=POST /notifications/read-all, userId={}", currentUserId())
        notificationService.markAllAsRead(UUID.fromString(userId))
        log.info("api=POST /notifications/read-all 완료, userId={}", currentUserId())
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    @DeleteMapping("/notifications/{notificationId}")
    fun deleteNotification(
        @AuthenticationPrincipal userId: String,
        @PathVariable notificationId: Long
    ): ResponseEntity<Void> {
        log.info("api=DELETE /notifications/{}, userId={}", notificationId, currentUserId())
        notificationService.deleteNotification(UUID.fromString(userId), notificationId)
        log.info("api=DELETE /notifications/{} 완료, userId={}", notificationId, currentUserId())
        return ResponseEntity.noContent().build()
    }
}
