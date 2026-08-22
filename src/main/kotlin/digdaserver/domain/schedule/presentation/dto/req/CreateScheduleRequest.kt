package digdaserver.domain.schedule.presentation.dto.req

import digdaserver.domain.ledger.presentation.dto.req.ExpenseWriteRequest
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class CreateScheduleRequest(
    val title: String,
    val color: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val allDay: Boolean,
    val participantIds: List<UUID>? = null,
    /** 그룹 가계부 — 이 일정에서 쓴 돈. 생략하면 지출 없는 일정. */
    val expenses: List<ExpenseWriteRequest>? = null
)
