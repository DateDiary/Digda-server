package digdaserver.domain.ledger.application.service

import digdaserver.domain.ledger.presentation.dto.req.ExpenseWriteRequest
import digdaserver.domain.ledger.presentation.dto.res.GroupLedgerResponse
import digdaserver.domain.ledger.presentation.dto.res.ScheduleExpenseListResponse
import java.util.UUID

interface LedgerService {

    /** 그룹 가계부 월 요약 — 전체 가계부 화면 한 장에 필요한 집계 전부. */
    fun getMonthlyLedger(userId: UUID, groupRoomId: Long, year: Int, month: Int): GroupLedgerResponse

    /**
     * 일정에 지출 한 건을 즉시 추가한다 — 일정 수정을 거치지 않는 빠른 기록 경로.
     *
     * 일정 수정(PUT)의 `expenses` 는 전체 교체라 목록 전부를 들고 있어야 하지만,
     * 이쪽은 한 건만 덧붙인다. 일정 상세에서 금액만 바로 적는 화면이 쓴다.
     */
    fun addScheduleExpense(
        userId: UUID,
        groupRoomId: Long,
        scheduleId: Long,
        request: ExpenseWriteRequest
    ): ScheduleExpenseListResponse
}
