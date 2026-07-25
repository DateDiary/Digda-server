package digdaserver.domain.feedback.domain.entity

import digdaserver.domain.user.domain.entity.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * 사용자가 제출한 피드백 응답 1건. 문항이 나중에 바뀌어도 어드민이 그대로 읽을 수 있도록
 * 제출 시점의 문항 제목/유형을 스냅샷으로 함께 JSON 직렬화해 [answers] 에 저장한다.
 *
 * answers JSON 예:
 * [{"questionId":1,"title":"얼마나 자주 쓰세요?","type":"SINGLE_CHOICE","answer":"매일"}, ...]
 */
@Entity
@Table(
    name = "feedback_submission",
    indexes = [Index(name = "idx_feedback_submission_created", columnList = "created_at")]
)
class FeedbackSubmission(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_submission_id")
    val id: Long = 0L,

    // 탈퇴 등으로 사용자가 사라져도 응답은 보존하기 위해 NULL 허용.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @Column(name = "answers", nullable = false, columnDefinition = "TEXT")
    val answers: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
