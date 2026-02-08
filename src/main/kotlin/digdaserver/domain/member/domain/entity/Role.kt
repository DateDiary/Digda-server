package digdaserver.domain.member.domain.entity

import com.fasterxml.jackson.annotation.JsonCreator
import digdaserver.global.infra.exception.error.ErrorCode
import digdaserver.global.infra.exception.error.HistoryException

enum class Role(val key: String) {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    companion object {

        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun from(key: String): Role {
            return entries.find { it.key.equals(key, ignoreCase = true) }
                ?: throw IllegalArgumentException("등급이 없네요.: $key")
        }

        fun getByValue(value: String): Role {
            return entries.find { it.key == value }
                ?: throw HistoryException(ErrorCode.INVALID_ROLE)
        }
    }
}
