package digdaserver.domain.schedule.domain.entity

import digdaserver.domain.group.domain.entity.Group
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "schedule")
class Schedule(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    val group: Group,

    @Column(nullable = false)
    val authorId: String,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = true)
    var content: String? = null,

    @Column(nullable = false)
    var startAt: LocalDateTime,

    @Column(nullable = false)
    var endAt: LocalDateTime,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun update(title: String?, content: String?, startAt: LocalDateTime?, endAt: LocalDateTime?) {
        title?.let { this.title = it }
        this.content = content ?: this.content
        startAt?.let { this.startAt = it }
        endAt?.let { this.endAt = it }
    }
}
