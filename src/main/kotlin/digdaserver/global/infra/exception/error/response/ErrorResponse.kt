package digdaserver.global.infra.exception.error.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import digdaserver.global.infra.exception.error.ErrorCode
import digdaserver.global.infra.exception.error.HistoryException
import java.time.LocalDateTime

@JsonInclude(Include.NON_NULL)
data class ErrorResponse(
    val status: Int,
    val code: Int,
    val message: String,
    val time: LocalDateTime,
    val retryAt: LocalDateTime? = null
) {
    companion object {

        fun of(e: HistoryException): ErrorResponse =
            ErrorResponse(
                status = e.httpStatusCode,
                code = e.errorCode.code,
                message = e.message ?: e.errorCode.message,
                time = LocalDateTime.now()
            )

        fun of(errorCode: ErrorCode): ErrorResponse =
            ErrorResponse(
                status = errorCode.httpCode,
                code = errorCode.code,
                message = errorCode.message,
                time = LocalDateTime.now()
            )

        fun of(errorCode: ErrorCode, customMessage: String): ErrorResponse =
            ErrorResponse(
                status = errorCode.httpCode,
                code = errorCode.code,
                message = customMessage,
                time = LocalDateTime.now()
            )

        fun of(
            errorCode: ErrorCode,
            now: LocalDateTime,
            retryAt: LocalDateTime
        ): ErrorResponse =
            ErrorResponse(
                status = errorCode.httpCode,
                code = errorCode.code,
                message = errorCode.message,
                time = now,
                retryAt = retryAt
            )
    }
}
