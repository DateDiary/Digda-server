package digdaserver.domain.user.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "user_notification_setting")
class UserNotificationSetting(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_notification_setting_id")
    val id: Long = 0L,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    val user: User,

    @Column(name = "push_enabled", nullable = false)
    var pushEnabled: Boolean = true,

    @Column(name = "schedule_notification", nullable = false)
    var scheduleNotification: Boolean = true,

    @Column(name = "diary_notification", nullable = false)
    var diaryNotification: Boolean = true,

    @Column(name = "comment_notification", nullable = false)
    var commentNotification: Boolean = true,

    // 모찌(캐릭터) 알림 — 게임을 제외한 캐릭터 관련(레벨업/디코/퀴즈) 푸시.
    @Column(name = "mochi_notification", nullable = false)
    var mochiNotification: Boolean = true,

    // 마케팅 수신은 알림 설정 화면에서 제거됐지만, prod 컬럼이 NOT NULL 이라 신규 insert
    // 안전을 위해 엔티티 필드는 dormant 로 유지한다(기본 false). API/UI 에서는 노출하지 않는다.
    @Column(name = "marketing_consent", nullable = false)
    var marketingConsent: Boolean = false
) {

    fun update(
        pushEnabled: Boolean?,
        scheduleNotification: Boolean?,
        diaryNotification: Boolean?,
        commentNotification: Boolean?,
        mochiNotification: Boolean?
    ) {
        pushEnabled?.let { this.pushEnabled = it }
        scheduleNotification?.let { this.scheduleNotification = it }
        diaryNotification?.let { this.diaryNotification = it }
        commentNotification?.let { this.commentNotification = it }
        mochiNotification?.let { this.mochiNotification = it }
    }
}
