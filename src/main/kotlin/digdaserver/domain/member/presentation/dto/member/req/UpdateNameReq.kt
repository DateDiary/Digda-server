package digdaserver.domain.member.presentation.dto.member.req

import jakarta.validation.constraints.NotBlank

data class UpdateNameReq(
    @field:NotBlank(message = "이름은 필수입니다.")
    val name: String
)
