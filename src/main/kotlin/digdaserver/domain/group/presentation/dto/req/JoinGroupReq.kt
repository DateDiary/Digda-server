package digdaserver.domain.group.presentation.dto.req

import jakarta.validation.constraints.NotBlank

data class JoinGroupReq(
    @field:NotBlank
    val inviteCode: String
)
