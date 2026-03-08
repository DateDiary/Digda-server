package digdaserver.domain.schedule.domain.repository

import digdaserver.domain.group.domain.entity.Group
import digdaserver.domain.schedule.domain.entity.Schedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ScheduleRepository : JpaRepository<Schedule, Long> {
    fun findAllByGroupAndStartAtBetween(
        group: Group,
        startAt: LocalDateTime,
        endAt: LocalDateTime
    ): List<Schedule>
}
