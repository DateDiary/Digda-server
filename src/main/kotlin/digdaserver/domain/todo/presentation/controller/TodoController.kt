package digdaserver.domain.todo.presentation.controller

import digdaserver.domain.todo.application.service.TodoService
import digdaserver.domain.todo.presentation.dto.req.CreateTodoRequest
import digdaserver.domain.todo.presentation.dto.req.ToggleTodoRequest
import digdaserver.domain.todo.presentation.dto.res.TodoListResponse
import digdaserver.domain.todo.presentation.dto.res.TodoResponse
import digdaserver.global.infra.logging.LogUserContext.currentUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "Todo", description = "할 일 API")
class TodoController(
    private val todoService: TodoService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "할 일 목록 조회", description = "그룹방의 할 일 목록을 조회합니다. 미완료 → 완료 순서로 정렬됩니다.")
    @GetMapping("/group-rooms/{groupRoomId}/todos")
    fun getTodos(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupRoomId: Long
    ): ResponseEntity<TodoListResponse> {
        log.info("api=GET /group-rooms/{}/todos, userId={}", groupRoomId, currentUserId())
        val response = todoService.getTodos(UUID.fromString(userId), groupRoomId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "할 일 생성", description = "그룹방에 새 할 일을 추가합니다.")
    @PostMapping("/group-rooms/{groupRoomId}/todos")
    fun createTodo(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupRoomId: Long,
        @RequestBody request: CreateTodoRequest
    ): ResponseEntity<TodoResponse> {
        log.info(
            "api=POST /group-rooms/{}/todos, userId={}, textLength={}",
            groupRoomId,
            currentUserId(),
            request.text.length
        )
        val response = todoService.createTodo(UUID.fromString(userId), groupRoomId, request.text)
        log.info(
            "api=POST /group-rooms/{}/todos 완료, userId={}, todoId={}",
            groupRoomId,
            currentUserId(),
            response.id
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "할 일 완료 토글", description = "할 일의 완료 여부를 토글합니다. 모든 구성원이 가능합니다.")
    @PatchMapping("/group-rooms/{groupRoomId}/todos/{todoId}")
    fun toggleTodo(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupRoomId: Long,
        @PathVariable todoId: Long,
        @RequestBody request: ToggleTodoRequest
    ): ResponseEntity<TodoResponse> {
        log.info(
            "api=PATCH /group-rooms/{}/todos/{}, userId={}, completed={}",
            groupRoomId,
            todoId,
            currentUserId(),
            request.completed
        )
        val response = todoService.toggleTodo(UUID.fromString(userId), groupRoomId, todoId, request.completed)
        log.info(
            "api=PATCH /group-rooms/{}/todos/{} 완료, userId={}, completed={}",
            groupRoomId,
            todoId,
            currentUserId(),
            response.completed
        )
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "할 일 삭제", description = "할 일을 삭제합니다. 작성자 또는 방장만 가능합니다.")
    @DeleteMapping("/group-rooms/{groupRoomId}/todos/{todoId}")
    fun deleteTodo(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupRoomId: Long,
        @PathVariable todoId: Long
    ): ResponseEntity<Void> {
        log.info(
            "api=DELETE /group-rooms/{}/todos/{}, userId={}",
            groupRoomId,
            todoId,
            currentUserId()
        )
        todoService.deleteTodo(UUID.fromString(userId), groupRoomId, todoId)
        log.info(
            "api=DELETE /group-rooms/{}/todos/{} 완료, userId={}",
            groupRoomId,
            todoId,
            currentUserId()
        )
        return ResponseEntity.noContent().build()
    }
}
