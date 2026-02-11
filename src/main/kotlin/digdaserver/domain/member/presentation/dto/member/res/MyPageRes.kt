package digdaserver.domain.member.presentation.dto.member.res

data class MyPageRes(
    val userName: String,
    val profile: String?
) {
    companion object {
        fun of(userName: String, profile: String?): MyPageRes {
            return MyPageRes(userName, profile)
        }
    }
}
