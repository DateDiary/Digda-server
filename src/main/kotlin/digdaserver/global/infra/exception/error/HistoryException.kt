package digdaserver.global.infra.exception.error

class HistoryException(
    val errorCode: ErrorCode,
    message: String = errorCode.message,
    cause: Throwable? = null
) : RuntimeException(message, cause) {

    constructor(errorCode: ErrorCode) :
        this(errorCode, errorCode.message, null)

    constructor(errorCode: ErrorCode, detailMessage: String) :
        this(errorCode, "${errorCode.message} → $detailMessage", null)

    constructor(errorCode: ErrorCode, cause: Throwable) :
        this(errorCode, errorCode.message, cause)

    val httpStatusCode: Int
        get() = errorCode.httpCode
}
