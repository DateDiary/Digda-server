package digdaserver.domain.ledger.presentation.controller

import digdaserver.domain.ledger.application.service.LedgerService
import digdaserver.domain.ledger.presentation.dto.req.ExpenseWriteRequest
import digdaserver.domain.ledger.presentation.dto.res.GroupLedgerResponse
import digdaserver.domain.ledger.presentation.dto.res.ScheduleExpenseListResponse
import digdaserver.global.infra.logging.LogUserContext.currentUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
            "지출 날짜 기준은 일정의 시작일입니다. 기록이 있는 첫/마지막 달(firstEntryMonth·lastEntryMonth)도 함께 내려가며, " +
            "앱은 이 값으로 월 이동 범위를 잡습니다."
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

    @Operation(
        summary = "일정에 지출 단건 추가",
        description = "일정 수정을 거치지 않고 금액 한 건만 즉시 저장합니다. " +
            "일정 수정(PUT)의 expenses 가 전체 교체인 것과 달리 기존 항목은 건드리지 않습니다. " +
            "응답으로 그 일정의 지출 목록 전체와 합계를 돌려줍니다."
    )
    @PostMapping("/group-rooms/{groupRoomId}/schedules/{scheduleId}/expenses")
    fun addScheduleExpense(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupRoomId: Long,
        @PathVariable scheduleId: Long,
        @RequestBody request: ExpenseWriteRequest
    ): ResponseEntity<ScheduleExpenseListResponse> {
        log.info(
            "api=POST /group-rooms/{}/schedules/{}/expenses, userId={}, amount={}, category={}",
            groupRoomId,
            scheduleId,
            currentUserId(),
            request.amount,
            request.category
        )
        val response = ledgerService.addScheduleExpense(
            UUID.fromString(userId),
            groupRoomId,
            scheduleId,
            request
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
