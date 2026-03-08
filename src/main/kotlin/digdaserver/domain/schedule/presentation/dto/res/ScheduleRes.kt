package digdaserver.domain.schedule.presentation.dto.res

import digdaserver.domain.schedule.domain.entity.Schedule
import java.time.LocalDateTime

data class ScheduleRes(
    val id: Long,
    val groupId: Long,
    val authorId: String,
    val title: String,
    val content: String?,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val createdAt: LocalDateTime
) {
    companion object {
        fun of(schedule: Schedule): ScheduleRes {
            return ScheduleRes(
                id = schedule.id,
                groupId = schedule.group.id,
                authorId = schedule.authorId,
                title = schedule.title,
                content = schedule.content,
                startAt = schedule.startAt,
                endAt = schedule.endAt,
                createdAt = schedule.createdAt
            )
        }
    }
}
