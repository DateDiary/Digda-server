package digdaserver.admin.feedback.presentation.controller

import digdaserver.admin.common.dto.res.AdminPageResponse
import digdaserver.admin.feedback.application.service.AdminFeedbackService
import digdaserver.admin.feedback.presentation.dto.req.SaveFeedbackQuestionsRequest
import digdaserver.admin.feedback.presentation.dto.res.AdminFeedbackQuestionResponse
import digdaserver.admin.feedback.presentation.dto.res.AdminFeedbackSubmissionResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 어드민 피드백 관리 — 문항 편집(전체 교체) + 응답 목록 조회.
 * `/api/admin` 하위라 SecurityConfig 에서 ROLE_ADMIN 으로 보호된다.
 */
@RestController
@RequestMapping("/api/admin/feedback")
@Tag(name = "Admin - Feedback", description = "관리자 피드백 문항/응답 관리 API")
class AdminFeedbackController(
    private val adminFeedbackService: AdminFeedbackService
) {

    @Operation(summary = "피드백 문항 조회", description = "비활성 포함 전체 문항을 순서대로 조회합니다.")
    @GetMapping("/questions")
    fun questions(): ResponseEntity<List<AdminFeedbackQuestionResponse>> =
        ResponseEntity.ok(adminFeedbackService.getQuestions())

    @Operation(summary = "피드백 문항 저장", description = "보낸 목록으로 전체 문항을 교체합니다(순서는 배열 인덱스).")
    @PutMapping("/questions")
    fun saveQuestions(
        @RequestBody request: SaveFeedbackQuestionsRequest
    ): ResponseEntity<List<AdminFeedbackQuestionResponse>> {
        return ResponseEntity.ok(adminFeedbackService.saveQuestions(request))
    }

    @Operation(summary = "피드백 응답 목록", description = "제출된 피드백을 최신순 페이징으로 조회합니다.")
    @GetMapping("/submissions")
    fun submissions(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<AdminPageResponse<AdminFeedbackSubmissionResponse>> =
        ResponseEntity.ok(adminFeedbackService.getSubmissions(page, size))
}
