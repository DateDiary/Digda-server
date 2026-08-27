package digdaserver.domain.ledger.presentation.dto.res

import digdaserver.domain.ledger.domain.entity.ScheduleExpense

/**
 * 일정 하나에 달린 지출 전체 — 단건 추가 후 화면이 그대로 다시 그릴 수 있게 목록과 합계를 함께 돌려준다.
 *
 * 방금 넣은 한 건만 돌려주면 앱이 합계를 스스로 더해야 하고, 그 사이 다른 멤버가 넣은
 * 금액은 새로고침 전까지 안 보인다. 목록을 통째로 주는 편이 화면 상태를 어긋나지 않게 한다.
 */
data class ScheduleExpenseListResponse(
    val scheduleId: Long,
    val expenses: List<ScheduleExpenseResponse>,
    /** 이 일정에 쓴 돈 합계. */
    val expenseTotal: Long
) {
    companion object {
        fun from(scheduleId: Long, expenses: List<ScheduleExpense>): ScheduleExpenseListResponse =
            ScheduleExpenseListResponse(
                scheduleId = scheduleId,
                expenses = expenses.map { ScheduleExpenseResponse.from(it) },
                expenseTotal = expenses.sumOf { it.amount }
            )
    }
}
