package digdaserver.admin.feedback.application.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import digdaserver.admin.common.dto.res.AdminPageResponse
import digdaserver.admin.feedback.application.service.AdminFeedbackService
import digdaserver.admin.feedback.presentation.dto.req.SaveFeedbackQuestionsRequest
import digdaserver.admin.feedback.presentation.dto.res.AdminFeedbackQuestionResponse
import digdaserver.admin.feedback.presentation.dto.res.AdminFeedbackSubmissionResponse
import digdaserver.domain.feedback.domain.entity.FeedbackQuestion
import digdaserver.domain.feedback.domain.entity.FeedbackSubmission
import digdaserver.domain.feedback.domain.repository.FeedbackQuestionRepository
import digdaserver.domain.feedback.domain.repository.FeedbackSubmissionRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminFeedbackServiceImpl(
    private val feedbackQuestionRepository: FeedbackQuestionRepository,
    private val feedbackSubmissionRepository: FeedbackSubmissionRepository,
    private val objectMapper: ObjectMapper
) : AdminFeedbackService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getQuestions(): List<AdminFeedbackQuestionResponse> =
        feedbackQuestionRepository.findAllByOrderByDisplayOrderAsc()
            .map { AdminFeedbackQuestionResponse.from(it) }

    @Transactional
    override fun saveQuestions(request: SaveFeedbackQuestionsRequest): List<AdminFeedbackQuestionResponse> {
        // 보낸 목록으로 전체 교체. 응답(feedback_submission)은 제출 시점 스냅샷을 따로 저장하므로
        // 문항을 지워도 기존 응답 조회에는 영향이 없다.
        feedbackQuestionRepository.deleteAllInBatch()
        val entities = request.questions.mapIndexed { index, item ->
            FeedbackQuestion(
                displayOrder = index,
                type = item.type,
                title = item.title.trim(),
                description = item.description?.trim()?.ifBlank { null },
                required = item.required,
                options = item.options?.ifBlank { null },
                active = item.active
            )
        }
        val saved = feedbackQuestionRepository.saveAll(entities)
        log.info("action=피드백 문항 저장, count={}", saved.size)
        return saved.map { AdminFeedbackQuestionResponse.from(it) }
    }

    override fun getSubmissions(page: Int, size: Int): AdminPageResponse<AdminFeedbackSubmissionResponse> {
        val result = feedbackSubmissionRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
        return AdminPageResponse.of(result) { toResponse(it) }
    }

    private fun toResponse(s: FeedbackSubmission): AdminFeedbackSubmissionResponse {
        val answers = parseAnswers(s.answers)
        return AdminFeedbackSubmissionResponse(
            submissionId = s.id,
            userId = s.user?.id?.toString(),
            userName = s.user?.displayedName(),
            answers = answers,
            createdAt = s.createdAt
        )
    }

    private fun parseAnswers(json: String): List<AdminFeedbackSubmissionResponse.Answer> = try {
        val raw: List<Map<String, Any?>> = objectMapper.readValue(
            json,
            objectMapper.typeFactory.constructCollectionType(List::class.java, Map::class.java)
        )
        raw.map {
            AdminFeedbackSubmissionResponse.Answer(
                questionId = (it["questionId"] as? Number)?.toLong(),
                title = it["title"]?.toString() ?: "",
                type = it["type"]?.toString() ?: "",
                answer = it["answer"]?.toString() ?: ""
            )
        }
    } catch (e: Exception) {
        log.warn("action=피드백 응답 파싱 실패, submissionAnswers={}, error={}", json, e.message)
        emptyList()
    }
}
