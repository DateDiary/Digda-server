package digdaserver.domain.schedule.presentation.dto.req

import java.time.LocalDateTime

data class UpdateScheduleReq(
    val title: String? = null,
    val content: String? = null,
    val startAt: LocalDateTime? = null,
    val endAt: LocalDateTime? = null
)
