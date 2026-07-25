package digdaserver.domain.feedback.presentation.dto.res

import digdaserver.domain.feedback.domain.entity.FeedbackQuestion
import digdaserver.domain.feedback.domain.entity.FeedbackQuestionType

/**
 * 앱이 피드백 폼을 렌더링하기 위한 문항 응답. [options] 는 유형별 JSON 문자열 그대로 전달한다.
 */
data class FeedbackQuestionResponse(
    val id: Long,
    val order: Int,
    val type: FeedbackQuestionType,
    val title: String,
    val description: String?,
    val required: Boolean,
    val options: String?
) {
    companion object {
        fun from(q: FeedbackQuestion): FeedbackQuestionResponse = FeedbackQuestionResponse(
            id = q.id,
            order = q.displayOrder,
            type = q.type,
            title = q.title,
            description = q.description,
            required = q.required,
            options = q.options
        )
    }
}
