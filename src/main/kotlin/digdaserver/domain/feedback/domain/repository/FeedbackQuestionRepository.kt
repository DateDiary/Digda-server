package digdaserver.domain.feedback.domain.repository

import digdaserver.domain.feedback.domain.entity.FeedbackQuestion
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackQuestionRepository : JpaRepository<FeedbackQuestion, Long> {
    fun findAllByActiveTrueOrderByDisplayOrderAsc(): List<FeedbackQuestion>
    fun findAllByOrderByDisplayOrderAsc(): List<FeedbackQuestion>
}
