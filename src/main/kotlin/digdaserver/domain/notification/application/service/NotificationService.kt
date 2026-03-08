package digdaserver.domain.notification.application.service

import digdaserver.domain.notification.presentation.dto.res.NotificationRes

interface NotificationService {
    fun getMyNotifications(userId: String, unreadOnly: Boolean): List<NotificationRes>
    fun markAsRead(userId: String, notificationId: Long): NotificationRes
    fun deleteNotification(userId: String, notificationId: Long)
}
