package digdaserver.domain.feedback.presentation.controller

import digdaserver.domain.feedback.application.service.FeedbackService
import digdaserver.domain.feedback.presentation.dto.req.SubmitFeedbackRequest
import digdaserver.domain.feedback.presentation.dto.res.FeedbackQuestionResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "Feedback", description = "앱 자체 피드백 폼 API")
class FeedbackController(
    private val feedbackService: FeedbackService
) {

    @Operation(summary = "피드백 문항 조회", description = "어드민이 구성한 활성 문항을 순서대로 조회합니다.")
    @GetMapping("/feedback/questions")
    fun questions(): ResponseEntity<List<FeedbackQuestionResponse>> =
        ResponseEntity.ok(feedbackService.getActiveQuestions())

    @Operation(summary = "피드백 제출", description = "마이페이지 피드백 폼의 응답을 제출합니다.")
    @PostMapping("/feedback")
    fun submit(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: SubmitFeedbackRequest
    ): ResponseEntity<Void> {
        feedbackService.submit(UUID.fromString(userId), request)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}
