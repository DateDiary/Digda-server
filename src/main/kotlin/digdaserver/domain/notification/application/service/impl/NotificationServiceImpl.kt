package digdaserver.domain.notification.application.service.impl

import digdaserver.domain.notification.application.service.NotificationService
import digdaserver.domain.notification.domain.repository.NotificationRepository
import digdaserver.domain.notification.presentation.dto.res.NotificationRes
import digdaserver.global.infra.exception.error.DigdaServerException
import digdaserver.global.infra.exception.error.ErrorCode
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository
) : NotificationService {

    override fun getMyNotifications(userId: String, unreadOnly: Boolean): List<NotificationRes> {
        val notifications = if (unreadOnly) {
            notificationRepository.findAllByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId)
        } else {
            notificationRepository.findAllByRecipientIdOrderByCreatedAtDesc(userId)
        }

        return notifications.map { NotificationRes.of(it) }
    }

    override fun markAsRead(userId: String, notificationId: Long): NotificationRes {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (notification.recipientId != userId) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        notification.markAsRead()

        return NotificationRes.of(notification)
    }

    override fun deleteNotification(userId: String, notificationId: Long) {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (notification.recipientId != userId) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        notificationRepository.delete(notification)
    }
}
