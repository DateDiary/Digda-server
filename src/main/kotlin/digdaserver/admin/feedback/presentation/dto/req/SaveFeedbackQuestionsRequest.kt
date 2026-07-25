package digdaserver.admin.feedback.presentation.dto.req

import digdaserver.domain.feedback.domain.entity.FeedbackQuestionType

/**
 * 어드민 피드백 문항 일괄 저장 — 보낸 목록으로 전체를 교체(순서는 배열 인덱스).
 * 문항 편집기가 드래그 정렬/추가/삭제한 최종 상태를 통째로 전송하는 단순 모델.
 */
data class SaveFeedbackQuestionsRequest(
    val questions: List<QuestionItem> = emptyList()
) {
    data class QuestionItem(
        val type: FeedbackQuestionType,
        val title: String,
        val description: String? = null,
        val required: Boolean = false,
        val options: String? = null,
        val active: Boolean = true
    )
}
