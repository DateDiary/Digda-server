package digdaserver.domain.report.presentation.controller

import digdaserver.domain.report.application.service.ReportService
import digdaserver.domain.report.presentation.dto.req.CreateReportRequest
import digdaserver.domain.report.presentation.dto.res.ReportResponse
import digdaserver.global.infra.logging.LogUserContext.currentUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "Report", description = "신고 API")
class ReportController(
    private val reportService: ReportService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(
        summary = "신고하기",
        description = "일기/댓글/일정/사용자를 신고합니다. 콘텐츠 신고는 신고자 본인에게서 자동 숨김됩니다."
    )
    @PostMapping("/reports")
    fun createReport(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: CreateReportRequest
    ): ResponseEntity<ReportResponse> {
        // 신고 상세 사유는 사용자 작성 텍스트라 길이만. 원본은 어드민 신고관리에서 본다.
        log.info(
            "api=POST /reports, userId={}, targetType={}, targetId={}, reason={}, groupRoomId={}, detailLength={}",
            currentUserId(),
            request.targetType,
            request.targetId,
            request.reason,
            request.groupRoomId,
            request.detail?.length ?: 0
        )
        val response = reportService.createReport(UUID.fromString(userId), request)
        log.info(
            "api=POST /reports 완료, userId={}, reportId={}, targetType={}, targetId={}, status={}",
            currentUserId(),
            response.id,
            response.targetType,
            response.targetId,
            response.status
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
