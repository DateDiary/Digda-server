package digdaserver.global.infra.exception.error

enum class ErrorCode(
    val code: Int,
    val message: String,
    val httpCode: Int
) {

    // Common
    SERVER_UNTRACKED_ERROR(-100, "미등록 서버 에러입니다. 서버 팀에 연락주세요.", 500),
    OBJECT_NOT_FOUND(-101, "조회된 객체가 없습니다.", 406),
    INVALID_PARAMETER(-102, "잘못된 파라미터입니다.", 422),
    PARAMETER_VALIDATION_ERROR(-103, "파라미터 검증 에러입니다.", 422),
    PARAMETER_GRAMMAR_ERROR(-104, "파라미터 문법 에러입니다.", 422),

    // Auth
    UNAUTHORIZED(-200, "인증 자격이 없습니다.", 401),
    FORBIDDEN(-201, "권한이 없습니다.", 403),
    ID_ERROR_TOKEN(-202, "잘못된 ID 토큰입니다.", 401),
    APPLE_ERROR_KEY(-202, "키 파싱 실패입니다.", 401),
    JWT_ERROR_TOKEN(-202, "잘못된 토큰입니다.", 401),
    JWT_EXPIRE_TOKEN(-203, "만료된 토큰입니다.", 401),
    AUTHORIZED_ERROR(-204, "인증 과정 중 에러가 발생했습니다.", 500),
    INVALID_ACCESS_TOKEN(-205, "Access Token이 유효하지 않습니다.", 401),
    JWT_UNMATCHED_CLAIMS(-206, "토큰 인증 정보가 일치하지 않습니다", 401),
    INVALID_REFRESH_TOKEN(-207, "Refresh Token이 유효하지 않습니다.", 401),
    REFRESH_TOKEN_NOT_EXIST(-208, "Refresh Token이 DB에 존재하지 않습니다.", 401),
    DUPLICATE_LOGIN_NOT_EXIST(-209, "중복 로그인은 허용되지 않습니다.", 401),

    // OAuth2
    INVALID_PROVIDER(-220, "지원하지 않는 소셜 로그인 제공자입니다.", 400),
    APPLE_JWT_ERROR(-221, "Apple JWT 생성 중 오류가 발생했습니다.", 500),
    UNSUPPORTED_PROVIDER(-222, "지원하지 않는 OAuth2 제공자입니다.", 400),

    // User
    INVALID_ROLE(-210, "해당 역할이 존재하지 않습니다.", 400),
    USER_NOT_EXIST(-211, "존재하지 않는 유저입니다.", 404),
    DUPLICATE_EMAIL(-212, "이미 사용 중인 이메일입니다.", 409),
    ADMIN_PERMISSION_REQUIRED(-213, "관리자 권한이 필요합니다.", 403),

    // OpenAI
    OPENAI_NOT_EXIST(-300, "OpenAI 오류", 500),
    IMAGE_UPLOAD_FAILED(-301, "이미지 주소가 존재하지 않습니다.", 404),

    // Point
    POINT_NOT_EXIST(-1000, "해당 포인트가 존재하지 않습니다.", 404),
    POINT_HISTORY_NOT_EXIST(-1001, "해당 포인트 내역이 존재하지 않습니다.", 404),
    EVENT_PARTICIPATION_LIMIT_EXCEEDED(-1002, "이벤트 응모 가능 횟수를 초과했습니다.", 400),
    INSUFFICIENT_POINT_FOR_HERITAGE(-1003, "포인트가 부족하여 문화재 이미지를 인식할 수 없습니다.", 400),
    EVENT_NOT_EXIST(-1004, "존재하지 않는 이벤트입니다.", 404)
}
