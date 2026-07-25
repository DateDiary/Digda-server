package digdaserver.domain.feedback.application.service

import digdaserver.domain.feedback.presentation.dto.req.SubmitFeedbackRequest
import digdaserver.domain.feedback.presentation.dto.res.FeedbackQuestionResponse
import java.util.UUID

interface FeedbackService {
    /** 앱 렌더링용 — active 문항을 순서대로. */
    fun getActiveQuestions(): List<FeedbackQuestionResponse>

    /** 피드백 제출. */
    fun submit(userId: UUID, request: SubmitFeedbackRequest)
}
