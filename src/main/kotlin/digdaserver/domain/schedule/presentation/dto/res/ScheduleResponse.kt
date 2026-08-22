package digdaserver.domain.schedule.presentation.dto.res

import digdaserver.domain.ledger.domain.entity.ScheduleExpense
import digdaserver.domain.ledger.presentation.dto.res.ScheduleExpenseResponse
import digdaserver.domain.schedule.domain.entity.Schedule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class ScheduleResponse(
    val id: Long,
    val title: String,
    val color: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val allDay: Boolean,
    val participants: List<UserSummary>,
    val createdBy: UserSummary,
    val commentCount: Int,
    val createdAt: LocalDateTime,
    /** 차단/신고로 숨겨진 일정인지. 목록에서는 보통 제외되며, 상세 직접 접근 방어용. */
    val hidden: Boolean = false,
    val hiddenReason: String? = null,
    /** 그룹 가계부 — 이 일정에 달린 지출 목록. */
    val expenses: List<ScheduleExpenseResponse> = emptyList(),
    /** 지출 합계. 캘린더 가계부 모드가 일정별 금액만 필요할 때 쓴다. */
    val expenseTotal: Long = 0L
) {
    /** 숨김 처리 시 금액도 함께 가린다 — 제목만 지우면 지출로 내용이 유추된다. */
    fun asHidden(reason: String): ScheduleResponse = copy(
        title = "",
        hidden = true,
        hiddenReason = reason,
        expenses = emptyList(),
        expenseTotal = 0L
    )

    companion object {
        fun from(
            schedule: Schedule,
            commentCount: Int,
            expenses: List<ScheduleExpense> = emptyList()
        ): ScheduleResponse = ScheduleResponse(
            id = schedule.id,
            title = schedule.title,
            color = schedule.color,
            startDate = schedule.startDate,
            endDate = schedule.endDate,
            startTime = schedule.startTime,
            endTime = schedule.endTime,
            allDay = schedule.allDay,
            participants = schedule.participants.map { UserSummary.from(it.user) },
            createdBy = UserSummary.from(schedule.createdBy),
            commentCount = commentCount,
            createdAt = schedule.createdAt,
            expenses = expenses.map { ScheduleExpenseResponse.from(it) },
            expenseTotal = expenses.sumOf { it.amount }
        )
    }
}
