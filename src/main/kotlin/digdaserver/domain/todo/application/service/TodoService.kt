package digdaserver.domain.todo.application.service

import digdaserver.domain.todo.presentation.dto.req.CreateTodoReq
import digdaserver.domain.todo.presentation.dto.req.UpdateTodoStatusReq
import digdaserver.domain.todo.presentation.dto.res.TodoRes

interface TodoService {
    fun createTodo(userId: String, groupId: Long, req: CreateTodoReq): TodoRes
    fun getTodos(userId: String, groupId: Long, completed: Boolean?): List<TodoRes>
    fun updateTodoStatus(userId: String, groupId: Long, todoId: Long, req: UpdateTodoStatusReq): TodoRes
    fun deleteTodo(userId: String, groupId: Long, todoId: Long)
}
