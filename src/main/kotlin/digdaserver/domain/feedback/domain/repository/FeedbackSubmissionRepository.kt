package digdaserver.domain.feedback.domain.repository

import digdaserver.domain.feedback.domain.entity.FeedbackSubmission
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackSubmissionRepository : JpaRepository<FeedbackSubmission, Long> {
    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<FeedbackSubmission>
}
