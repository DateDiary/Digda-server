package digdaserver.domain.diary.presentation.dto.req

data class UpdateDiaryReq(
    val title: String? = null,
    val content: String? = null,
    val imageUrl: String? = null
)
