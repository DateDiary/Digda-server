package digdaserver.domain.inquiry.presentation.controller

import digdaserver.domain.inquiry.application.service.InquiryService
import digdaserver.domain.inquiry.presentation.dto.req.CreateInquiryRequest
import digdaserver.domain.inquiry.presentation.dto.res.InquiryResponse
import digdaserver.global.infra.logging.LogUserContext.currentUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "Inquiry", description = "고객센터 문의 API")
class InquiryController(
    private val inquiryService: InquiryService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(
        summary = "고객센터 문의 작성",
        description = "마이페이지 고객센터에서 문의를 보냅니다. 하루 2건까지만 작성할 수 있습니다."
    )
    @PostMapping("/inquiries")
    fun create(
        @AuthenticationPrincipal userId: String,
        @RequestBody @Valid
        request: CreateInquiryRequest
    ): ResponseEntity<InquiryResponse> {
        log.info(
            "api=POST /inquiries, userId={}, contentLength={}",
            currentUserId(),
            request.content.length
        )
        val response = inquiryService.create(UUID.fromString(userId), request)
        log.info(
            "api=POST /inquiries 완료, userId={}, inquiryId={}, status={}",
            currentUserId(),
            response.id,
            response.status
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "내 문의 목록", description = "내가 보낸 고객센터 문의를 최신순으로 조회합니다.")
    @GetMapping("/inquiries")
    fun myInquiries(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<List<InquiryResponse>> {
        log.info("api=GET /inquiries, userId={}", currentUserId())
        return ResponseEntity.ok(inquiryService.myInquiries(UUID.fromString(userId)))
    }
}
