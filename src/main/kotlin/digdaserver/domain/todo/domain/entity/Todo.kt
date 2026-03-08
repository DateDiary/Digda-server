package digdaserver.domain.todo.domain.entity

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
@Table(name = "todo")
class Todo(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    val group: Group,

    @Column(nullable = false)
    val authorId: String,

    @Column(nullable = true)
    val assigneeId: String? = null,

    @Column(nullable = false)
    var content: String,

    @Column(nullable = false)
    var isCompleted: Boolean = false,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    var completedAt: LocalDateTime? = null
) {
    fun updateStatus(isCompleted: Boolean) {
        this.isCompleted = isCompleted
        this.completedAt = if (isCompleted) LocalDateTime.now() else null
    }
}
