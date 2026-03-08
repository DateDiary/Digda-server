package digdaserver.domain.todo.presentation.dto.req

import jakarta.validation.constraints.NotNull

data class UpdateTodoStatusReq(
    @field:NotNull
    val isCompleted: Boolean
)
