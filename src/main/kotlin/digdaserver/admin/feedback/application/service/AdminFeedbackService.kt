package digdaserver.admin.feedback.application.service

import digdaserver.admin.common.dto.res.AdminPageResponse
import digdaserver.admin.feedback.presentation.dto.req.SaveFeedbackQuestionsRequest
import digdaserver.admin.feedback.presentation.dto.res.AdminFeedbackQuestionResponse
import digdaserver.admin.feedback.presentation.dto.res.AdminFeedbackSubmissionResponse

interface AdminFeedbackService {
    fun getQuestions(): List<AdminFeedbackQuestionResponse>
    fun saveQuestions(request: SaveFeedbackQuestionsRequest): List<AdminFeedbackQuestionResponse>
    fun getSubmissions(page: Int, size: Int): AdminPageResponse<AdminFeedbackSubmissionResponse>
}
