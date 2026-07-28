package digdaserver.global.infra.logging

import org.slf4j.MDC

/**
 * 요청 단위 사용자 식별자를 MDC 에 담아두는 곳.
 *
 * 서버에는 로그인 아이디 컬럼이 없다(소셜 로그인 전용). 그래서 운영에서 사람이 읽을 수 있는
 * 식별자로 **이메일 앞부분**(local-part)을 쓴다. 예: chltmdgh522@naver.com → chltmdgh522
 *
 * 이메일은 JWT 의 `email` 클레임에 이미 실려 있어서 DB 조회 없이 토큰에서 바로 꺼낸다.
 *
 * - [USER_ID] : 로그 본문에 찍히는 값. 컨트롤러의 `userId={}` 자리에 들어간다.
 * - [USER_LOG_KEY] : logback SiftingAppender 의 discriminator. 파일 경로로 쓰이므로
 *   경로 문자를 모두 제거한 안전한 형태여야 한다(/logs/users/{key}/digda.log).
 */
object LogUserContext {

    const val USER_ID = "userId"
    const val USER_LOG_KEY = "userLogKey"

    /** 이메일이 없거나(=이론상) 비로그인 요청일 때 쓰는 값. */
    const val UNKNOWN = "unknown"

    /** 폴더명 허용 문자 — 나머지는 전부 `_` 로 치환한다. */
    private val SAFE_CHARS = Regex("[^a-z0-9._-]")

    /** 파일시스템/로그 안정성을 위한 길이 상한. */
    private const val MAX_KEY_LENGTH = 40

    /**
     * 이메일에서 로그용 식별자를 뽑는다.
     *
     * `null`/공백이거나 `@` 앞이 비면 [UNKNOWN] 으로 떨어진다. 상위 디렉터리 탈출(`..`)이나
     * 경로 구분자가 폴더명으로 새어나가지 않게 화이트리스트 방식으로 정규화한다.
     */
    fun toLogKey(email: String?): String {
        val localPart = email?.substringBefore('@')?.trim()?.lowercase()
        if (localPart.isNullOrBlank()) return UNKNOWN

        val sanitized = SAFE_CHARS.replace(localPart, "_")
            .trimStart('.')
            .take(MAX_KEY_LENGTH)

        return sanitized.ifBlank { UNKNOWN }
    }

    /** 요청 진입 시 1회 호출. 이후 모든 로그가 이 사용자 파일로도 함께 흘러간다. */
    fun bind(email: String?) {
        val key = toLogKey(email)
        MDC.put(USER_ID, key)
        MDC.put(USER_LOG_KEY, key)
    }

    /**
     * 컨트롤러 로그의 `userId={}` 에 넣을 값.
     *
     * 인증 정보가 없는 요청(로그인/약관 조회 등)에서는 `-` 를 돌려준다.
     */
    fun currentUserId(): String = MDC.get(USER_ID) ?: "-"

    /**
     * 사용자별 로그 파일로의 라우팅만 끊는다. userId 는 그대로 남겨둔다.
     *
     * 어드민 요청에 쓴다 — 어드민 트래픽은 사용자 폴더에 쌓이면 안 되지만,
     * 예외가 터졌을 때 "누가 했는지" 는 메인 로그에 남아야 하기 때문.
     */
    fun detachUserFile() {
        MDC.remove(USER_LOG_KEY)
    }

    /**
     * 요청 종료 시 반드시 호출. 톰캣 스레드가 재사용되기 때문에 지우지 않으면
     * 다음 요청이 이전 사용자 폴더에 로그를 쓰게 된다.
     */
    fun clear() {
        MDC.remove(USER_ID)
        MDC.remove(USER_LOG_KEY)
    }
}
