package digdaserver.domain.member.presentation.dto.member.res

data class MyPageRes(
    val userName: String,
    val profile: String?,
    val heritageDtos: List<HeritageDto>
) {
    companion object {
        fun of(userName: String, profile: String?, heritageDtos: List<HeritageDto>): MyPageRes {
            return MyPageRes(userName, profile, heritageDtos)
        }
    }
}
