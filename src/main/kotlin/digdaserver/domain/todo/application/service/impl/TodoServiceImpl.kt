package digdaserver.domain.todo.application.service.impl

import digdaserver.domain.group.domain.repository.GroupMemberRepository
import digdaserver.domain.group.domain.repository.GroupRepository
import digdaserver.domain.todo.application.service.TodoService
import digdaserver.domain.todo.domain.entity.Todo
import digdaserver.domain.todo.domain.repository.TodoRepository
import digdaserver.domain.todo.presentation.dto.req.CreateTodoReq
import digdaserver.domain.todo.presentation.dto.req.UpdateTodoStatusReq
import digdaserver.domain.todo.presentation.dto.res.TodoRes
import digdaserver.global.infra.exception.error.DigdaServerException
import digdaserver.global.infra.exception.error.ErrorCode
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class TodoServiceImpl(
    private val todoRepository: TodoRepository,
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository
) : TodoService {

    override fun createTodo(userId: String, groupId: Long, req: CreateTodoReq): TodoRes {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val todo = todoRepository.save(
            Todo(
                group = group,
                authorId = userId,
                assigneeId = req.assigneeId,
                content = req.content
            )
        )

        return TodoRes.of(todo)
    }

    override fun getTodos(userId: String, groupId: Long, completed: Boolean?): List<TodoRes> {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val todos = if (completed != null) {
            todoRepository.findAllByGroupAndIsCompleted(group, completed)
        } else {
            todoRepository.findAllByGroup(group)
        }

        return todos.map { TodoRes.of(it) }
    }

    override fun updateTodoStatus(userId: String, groupId: Long, todoId: Long, req: UpdateTodoStatusReq): TodoRes {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val todo = todoRepository.findById(todoId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        todo.updateStatus(req.isCompleted)

        return TodoRes.of(todo)
    }

    override fun deleteTodo(userId: String, groupId: Long, todoId: Long) {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val todo = todoRepository.findById(todoId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (todo.authorId != userId) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        todoRepository.delete(todo)
    }
}
