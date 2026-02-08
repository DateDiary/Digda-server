package digdaserver.domain.oauth2.presentation.dto.res.oatuh

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoTokenResponse(

    @JsonProperty("access_token")
    val accessToken: String? = null,

    @JsonProperty("refresh_token")
    val refreshToken: String? = null,

    @JsonProperty("id_token")
    val idToken: String? = null,

    @JsonProperty("expires_in")
    val expiresIn: Long? = null
)
