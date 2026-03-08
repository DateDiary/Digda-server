package digdaserver.domain.diary.presentation.dto.req

import jakarta.validation.constraints.NotBlank

data class CreateDiaryReq(
    @field:NotBlank
    val title: String,

    @field:NotBlank
    val content: String,

    val imageUrl: String? = null
)
