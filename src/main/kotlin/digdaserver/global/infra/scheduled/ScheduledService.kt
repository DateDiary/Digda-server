package digdaserver.global.infra.scheduled

import digdaserver.domain.point.domain.repository.PointRepository
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@Transactional
class ScheduledService(
    private val pointRepository: PointRepository
) {

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    fun resetPointsAtMidnight() {
        pointRepository.updateAllBalanceToFixedValue(2)
    }
}
