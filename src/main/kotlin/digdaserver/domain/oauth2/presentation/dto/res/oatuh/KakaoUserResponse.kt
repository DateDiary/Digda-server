package digdaserver.domain.oauth2.presentation.dto.res.oatuh

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoUserResponse(

    @JsonProperty("id")
    val id: String? = null,

    @JsonProperty("kakao_account")
    val kakaoAccount: KakaoAccount? = null
) {

    /** 프로필 이름 */
    fun getName(): String? {
        return kakaoAccount?.profile?.nickname
    }

    /** 이메일 */
    fun getEmail(): String? {
        return kakaoAccount?.email
    }

    /** 프로필 이미지 URL */
    fun getProfile(): String? {
        return kakaoAccount?.profile?.profileImageUrl
    }

    data class KakaoAccount(
        @JsonProperty("profile")
        val profile: Profile? = null,

        @JsonProperty("email")
        val email: String? = null
    )

    data class Profile(
        @JsonProperty("nickname")
        val nickname: String? = null,

        @JsonProperty("profile_image_url")
        val profileImageUrl: String? = null
    )
}
