package digdaserver.domain.ledger.presentation.controller

import digdaserver.domain.ledger.application.service.LedgerService
import digdaserver.domain.ledger.presentation.dto.res.GroupLedgerResponse
import digdaserver.global.infra.logging.LogUserContext.currentUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "Ledger", description = "그룹 가계부 API")
class LedgerController(
    private val ledgerService: LedgerService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(
        summary = "그룹 가계부 월 요약",
        description = "해당 월의 총 지출과 분류별·멤버별·일정별·날짜별 집계를 한 번에 내려줍니다. " +
            "지출 날짜 기준은 일정의 시작일입니다."
    )
    @GetMapping("/group-rooms/{groupRoomId}/ledger")
    fun getMonthlyLedger(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupRoomId: Long,
        @RequestParam year: Int,
        @RequestParam month: Int
    ): ResponseEntity<GroupLedgerResponse> {
        log.info(
            "api=GET /group-rooms/{}/ledger, userId={}, year={}, month={}",
            groupRoomId,
            currentUserId(),
            year,
            month
        )
        val response = ledgerService.getMonthlyLedger(UUID.fromString(userId), groupRoomId, year, month)
        return ResponseEntity.ok(response)
    }
}
