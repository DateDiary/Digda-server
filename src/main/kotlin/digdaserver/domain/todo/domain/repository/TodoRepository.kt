package digdaserver.domain.todo.domain.repository

import digdaserver.domain.group.domain.entity.Group
import digdaserver.domain.todo.domain.entity.Todo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : JpaRepository<Todo, Long> {
    fun findAllByGroup(group: Group): List<Todo>
    fun findAllByGroupAndIsCompleted(group: Group, isCompleted: Boolean): List<Todo>
}
