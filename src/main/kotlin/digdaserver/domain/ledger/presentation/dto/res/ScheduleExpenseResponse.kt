package digdaserver.domain.ledger.presentation.dto.res

import digdaserver.domain.ledger.domain.entity.ExpenseCategory
import digdaserver.domain.ledger.domain.entity.ScheduleExpense
import digdaserver.domain.schedule.presentation.dto.res.UserSummary

data class ScheduleExpenseResponse(
    val id: Long,
    val amount: Long,
    val category: ExpenseCategory,
    /** 앱이 분류 한글명을 하드코딩하지 않도록 서버가 함께 내려준다. */
    val categoryLabel: String,
    val memo: String?,
    /** 탈퇴한 멤버가 낸 지출이면 null. */
    val payer: UserSummary?
) {
    companion object {
        fun from(expense: ScheduleExpense): ScheduleExpenseResponse = ScheduleExpenseResponse(
            id = expense.id,
            amount = expense.amount,
            category = expense.category,
            categoryLabel = expense.category.label,
            memo = expense.memo,
            payer = expense.payer?.let { UserSummary.from(it) }
        )
    }
}
