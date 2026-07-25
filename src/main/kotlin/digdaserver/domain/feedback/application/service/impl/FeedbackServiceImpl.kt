package digdaserver.domain.feedback.application.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import digdaserver.domain.feedback.application.service.FeedbackService
import digdaserver.domain.feedback.domain.entity.FeedbackSubmission
import digdaserver.domain.feedback.domain.repository.FeedbackQuestionRepository
import digdaserver.domain.feedback.domain.repository.FeedbackSubmissionRepository
import digdaserver.domain.feedback.presentation.dto.req.SubmitFeedbackRequest
import digdaserver.domain.feedback.presentation.dto.res.FeedbackQuestionResponse
import digdaserver.domain.user.domain.repository.UserRepository
import digdaserver.global.infra.exception.error.DigdaException
import digdaserver.global.infra.exception.error.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class FeedbackServiceImpl(
    private val feedbackQuestionRepository: FeedbackQuestionRepository,
    private val feedbackSubmissionRepository: FeedbackSubmissionRepository,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper
) : FeedbackService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getActiveQuestions(): List<FeedbackQuestionResponse> =
        feedbackQuestionRepository.findAllByActiveTrueOrderByDisplayOrderAsc()
            .map { FeedbackQuestionResponse.from(it) }

    @Transactional
    override fun submit(userId: UUID, request: SubmitFeedbackRequest) {
        val items = request.answers.filter { it.answer.isNotBlank() }
        if (items.isEmpty()) throw DigdaException(ErrorCode.INVALID_PARAMETER)

        val user = userRepository.findById(userId)
            .orElseThrow { DigdaException(ErrorCode.USER_NOT_FOUND) }

        // 제출 시점의 문항 제목/유형을 스냅샷으로 함께 저장(문항이 나중에 바뀌어도 어드민이 읽을 수 있게).
        val questionsById = feedbackQuestionRepository.findAllById(items.map { it.questionId })
            .associateBy { it.id }

        val snapshot = items.map { item ->
            val q = questionsById[item.questionId]
            mapOf(
                "questionId" to item.questionId,
                "title" to (q?.title ?: ""),
                "type" to (q?.type?.name ?: ""),
                "answer" to item.answer
            )
        }
        val answersJson = objectMapper.writeValueAsString(snapshot)

        val saved = feedbackSubmissionRepository.save(
            FeedbackSubmission(user = user, answers = answersJson)
        )
        log.info("action=피드백 제출, userId={}, submissionId={}, answers={}", userId, saved.id, items.size)
    }
}
