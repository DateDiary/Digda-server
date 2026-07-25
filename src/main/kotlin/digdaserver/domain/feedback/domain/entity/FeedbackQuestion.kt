package digdaserver.domain.feedback.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * 앱 자체 피드백 폼의 문항 정의. 어드민이 추가/수정/정렬하며, 앱은 active 문항을
 * displayOrder 순으로 받아 동적으로 렌더링한다.
 *
 * [options] 는 유형별 JSON 문자열(파싱은 클라이언트/어드민):
 * - SINGLE_CHOICE: ["매일","가끔",...]
 * - SCALE        : {"min":1,"max":5}
 * - GRID         : {"rows":[...],"cols":[...]}
 * - 그 외        : null
 */
@Entity
@Table(name = "feedback_question")
class FeedbackQuestion(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_question_id")
    val id: Long = 0L,

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    var type: FeedbackQuestionType,

    @Column(name = "title", nullable = false, length = 500)
    var title: String,

    @Column(name = "description", length = 1000)
    var description: String? = null,

    @Column(name = "required", nullable = false)
    var required: Boolean = false,

    @Column(name = "options", columnDefinition = "TEXT")
    var options: String? = null,

    @Column(name = "active", nullable = false)
    var active: Boolean = true
)
