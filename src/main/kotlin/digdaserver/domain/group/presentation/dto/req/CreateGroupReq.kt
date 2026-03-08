package digdaserver.domain.group.presentation.dto.req

import jakarta.validation.constraints.NotBlank

data class CreateGroupReq(
    @field:NotBlank
    val name: String,
    val description: String? = null
)
