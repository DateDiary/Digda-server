package digdaserver.domain.schedule.presentation.dto.req

import digdaserver.domain.ledger.presentation.dto.req.ExpenseWriteRequest
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class UpdateScheduleRequest(
    val title: String? = null,
    val color: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val allDay: Boolean? = null,
    val participantIds: List<UUID>? = null,
    /**
     * 그룹 가계부 — 보내면 이 일정의 지출 목록을 **통째로 교체**한다.
     * null 이면 지출은 손대지 않는다(빈 배열 = 전부 삭제와 구분됨).
     */
    val expenses: List<ExpenseWriteRequest>? = null
)
