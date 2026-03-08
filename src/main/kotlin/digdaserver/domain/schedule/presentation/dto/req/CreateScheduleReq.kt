package digdaserver.domain.schedule.presentation.dto.req

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class CreateScheduleReq(
    @field:NotBlank
    val title: String,
    val content: String? = null,
    @field:NotNull
    val startAt: LocalDateTime,
    @field:NotNull
    val endAt: LocalDateTime
)
