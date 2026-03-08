package digdaserver.domain.notification.presentation.dto.res

import digdaserver.domain.notification.domain.entity.Notification
import digdaserver.domain.notification.domain.entity.NotificationType
import java.time.LocalDateTime

data class NotificationRes(
    val id: Long,
    val type: NotificationType,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime,
    val readAt: LocalDateTime?,
    val referenceId: Long?,
    val referenceType: String?
) {
    companion object {
        fun of(notification: Notification): NotificationRes {
            return NotificationRes(
                id = notification.id,
                type = notification.type,
                title = notification.title,
                message = notification.message,
                isRead = notification.isRead,
                createdAt = notification.createdAt,
                readAt = notification.readAt,
                referenceId = notification.referenceId,
                referenceType = notification.referenceType
            )
        }
    }
}
