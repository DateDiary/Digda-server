package digdaserver.domain.oauth2.presentation.controller

import digdaserver.domain.oauth2.application.service.TermsDocumentService
import digdaserver.domain.oauth2.application.service.TermsService
import digdaserver.domain.oauth2.presentation.dto.req.TermsAgreeRequest
import digdaserver.domain.oauth2.presentation.dto.res.TermsDocumentResponse
import digdaserver.global.infra.logging.LogUserContext.currentUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 API")
class TermsController(
    private val termsService: TermsService,
    private val termsDocumentService: TermsDocumentService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "약관 동의", description = "신규 가입자가 필수/선택 약관에 동의합니다. isNewUser: true일 때만 호출.")
    @PostMapping("/terms")
    fun agreeToTerms(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: TermsAgreeRequest
    ): ResponseEntity<Void> {
        log.info(
            "api=POST /auth/terms, userId={}, termsOfService={}, privacyPolicy={}, " +
                "marketingConsent={}, pushConsent={}",
            currentUserId(),
            request.termsOfService,
            request.privacyPolicy,
            request.marketingConsent,
            request.pushConsent
        )
        termsService.agreeToTerms(UUID.fromString(userId), request)
        log.info("api=POST /auth/terms 완료, userId={}", currentUserId())
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "약관 문서 조회", description = "약관 전문 HTML을 조회합니다. 인증 불필요. 앱에 있음 폐지")
    @GetMapping("/terms/{type}")
    fun getTermsDocument(
        @PathVariable type: String
    ): ResponseEntity<TermsDocumentResponse> {
        log.info("api=GET /auth/terms/{}, userId={}", type, currentUserId())
        val response = termsDocumentService.getTermsDocument(type)
        return ResponseEntity.ok(response)
    }
}
