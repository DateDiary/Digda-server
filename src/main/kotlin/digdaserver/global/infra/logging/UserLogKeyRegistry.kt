package digdaserver.global.infra.logging

import java.util.Collections
import java.util.UUID

/**
 * userId(UUID) → 로그 식별자(이메일 앞부분) 캐시.
 *
 * 서블릿 요청은 [LogUserContext] 의 MDC 로 충분하지만, STOMP(WebSocket) 프레임은
 * 서블릿 필터를 타지 않고 별도 스레드풀에서 처리돼 MDC 가 전파되지 않는다.
 * WS 컨트롤러가 가진 건 `principal.name`(UUID) 뿐이라, 그걸 사람이 읽는 식별자로
 * 되돌리기 위한 조회 테이블.
 *
 * 채우는 곳은 두 군데 — 로그인된 REST 요청(DigdaJWTFilter)과 STOMP CONNECT(WebSocketConfig).
 * 둘 다 JWT 의 email 클레임을 이미 들고 있어서 DB 조회는 발생하지 않는다.
 *
 * 순수 로깅 보조용이라 miss 는 정상이며(서버 재기동 직후 등) 그때는 UUID 앞 8자리로 떨어진다.
 */
object UserLogKeyRegistry {

    /** 메모리 상한 — 넘으면 가장 오래 참조되지 않은 것부터 밀어낸다. */
    private const val MAX_ENTRIES = 10_000

    private val cache: MutableMap<String, String> = Collections.synchronizedMap(
        object : LinkedHashMap<String, String>(256, 0.75f, true) {
            override fun removeEldestEntry(eldest: Map.Entry<String, String>): Boolean =
                size > MAX_ENTRIES
        }
    )

    fun put(userId: String?, email: String?) {
        if (userId.isNullOrBlank()) return
        cache[userId] = LogUserContext.toLogKey(email)
    }

    /**
     * 로그에 찍을 식별자. 캐시에 없으면 UUID 앞 8자리로 대체해서
     * 최소한 같은 사용자끼리는 묶어볼 수 있게 한다.
     */
    fun of(userId: String?): String {
        if (userId.isNullOrBlank()) return "-"
        return cache[userId] ?: userId.take(8)
    }

    fun of(userId: UUID?): String = of(userId?.toString())
}
