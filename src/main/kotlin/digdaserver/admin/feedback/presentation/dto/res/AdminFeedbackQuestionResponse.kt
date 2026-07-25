package digdaserver.admin.feedback.presentation.dto.res

import digdaserver.domain.feedback.domain.entity.FeedbackQuestion
import digdaserver.domain.feedback.domain.entity.FeedbackQuestionType

data class AdminFeedbackQuestionResponse(
    val id: Long,
    val order: Int,
    val type: FeedbackQuestionType,
    val title: String,
    val description: String?,
    val required: Boolean,
    val options: String?,
    val active: Boolean
) {
    companion object {
        fun from(q: FeedbackQuestion): AdminFeedbackQuestionResponse = AdminFeedbackQuestionResponse(
            id = q.id,
            order = q.displayOrder,
            type = q.type,
            title = q.title,
            description = q.description,
            required = q.required,
            options = q.options,
            active = q.active
        )
    }
}
