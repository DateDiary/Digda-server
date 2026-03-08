package digdaserver.domain.todo.presentation.controller

import digdaserver.domain.todo.application.service.TodoService
import digdaserver.domain.todo.presentation.dto.req.CreateTodoReq
import digdaserver.domain.todo.presentation.dto.req.UpdateTodoStatusReq
import digdaserver.domain.todo.presentation.dto.res.TodoRes
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/group/{groupId}/todo")
@Tag(name = "Todo", description = "할 일 전용 API")
class TodoController(
    private val todoService: TodoService
) {

    @Operation(summary = "할 일 생성 API", description = "그룹 내 할 일을 생성합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "할 일 생성 성공"),
            ApiResponse(responseCode = "406", description = "그룹 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
        ]
    )
    @PostMapping
    fun createTodo(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @Valid @RequestBody req: CreateTodoReq
    ): ResponseEntity<TodoRes> {
        return ResponseEntity.status(201).body(todoService.createTodo(userId, groupId, req))
    }

    @Operation(summary = "할 일 목록 조회 API", description = "그룹 내 할 일 목록을 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "할 일 목록 조회 성공"),
            ApiResponse(responseCode = "406", description = "그룹 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
        ]
    )
    @GetMapping
    fun getTodos(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @RequestParam(required = false) completed: Boolean?
    ): ResponseEntity<List<TodoRes>> {
        return ResponseEntity.ok(todoService.getTodos(userId, groupId, completed))
    }

    @Operation(summary = "할 일 상태 변경 API", description = "할 일의 완료 상태를 변경합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "할 일 상태 변경 성공"),
            ApiResponse(responseCode = "406", description = "그룹 또는 할 일 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
        ]
    )
    @PatchMapping("/{todoId}/status")
    fun updateTodoStatus(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @PathVariable todoId: Long,
        @Valid @RequestBody req: UpdateTodoStatusReq
    ): ResponseEntity<TodoRes> {
        return ResponseEntity.ok(todoService.updateTodoStatus(userId, groupId, todoId, req))
    }

    @Operation(summary = "할 일 삭제 API", description = "할 일을 삭제합니다. 작성자만 삭제 가능합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "할 일 삭제 성공"),
            ApiResponse(responseCode = "406", description = "그룹 또는 할 일 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "권한 없음")
        ]
    )
    @DeleteMapping("/{todoId}")
    fun deleteTodo(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @PathVariable todoId: Long
    ): ResponseEntity<Unit> {
        todoService.deleteTodo(userId, groupId, todoId)
        return ResponseEntity.noContent().build()
    }
}
