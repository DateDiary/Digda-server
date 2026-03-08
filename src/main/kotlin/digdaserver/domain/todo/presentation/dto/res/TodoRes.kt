package digdaserver.domain.todo.presentation.dto.res

import digdaserver.domain.todo.domain.entity.Todo
import java.time.LocalDateTime

data class TodoRes(
    val id: Long,
    val groupId: Long,
    val authorId: String,
    val assigneeId: String?,
    val content: String,
    val isCompleted: Boolean,
    val createdAt: LocalDateTime,
    val completedAt: LocalDateTime?
) {
    companion object {
        fun of(todo: Todo): TodoRes {
            return TodoRes(
                id = todo.id,
                groupId = todo.group.id,
                authorId = todo.authorId,
                assigneeId = todo.assigneeId,
                content = todo.content,
                isCompleted = todo.isCompleted,
                createdAt = todo.createdAt,
                completedAt = todo.completedAt
            )
        }
    }
}
