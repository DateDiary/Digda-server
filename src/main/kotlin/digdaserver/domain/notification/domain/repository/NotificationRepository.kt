package digdaserver.domain.notification.domain.repository

import digdaserver.domain.notification.domain.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findAllByRecipientIdOrderByCreatedAtDesc(recipientId: String): List<Notification>
    fun findAllByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(recipientId: String): List<Notification>
}
