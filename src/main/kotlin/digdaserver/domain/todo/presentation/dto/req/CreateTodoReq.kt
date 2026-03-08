package digdaserver.domain.todo.presentation.dto.req

import jakarta.validation.constraints.NotBlank

data class CreateTodoReq(
    @field:NotBlank
    val content: String,
    val assigneeId: String? = null
)
