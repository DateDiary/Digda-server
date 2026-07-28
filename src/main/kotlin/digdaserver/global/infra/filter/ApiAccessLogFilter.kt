package digdaserver.global.infra.filter

import digdaserver.global.infra.logging.LogUserContext
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.filter.OncePerRequestFilter

/**
 * 모든 API 요청 진입에 대해 한 줄로 표준 로그를 남긴다.
 *
 * - JWT 필터 뒤에 체이닝되어 [LogUserContext] 가 바인딩한 userId(이메일 앞부분)를 함께 기록
 * - 헬스체크/Swagger/static, 그리고 **관리자 API** 는 로그에서 제외
 * - 응답 종료 시점에 method, path, userId, status, latency 한 줄 로그
 * - 요청이 끝나면 MDC 를 반드시 비운다(톰캣 스레드 재사용 → 다음 요청이 남의 폴더에 쓰는 사고 방지)
 *
 * "내가 분명히 API 진입점마다 로그 박으라고 했지" 라는 운영 요구에 대한
 * 컨트롤러별 일일이 추가하지 않는 글로벌 솔루션.
 */
class ApiAccessLogFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(ApiAccessLogFilter::class.java)

    /**
     * 로그에서 제외할 prefix — 헬스체크/문서/정적자원, 그리고 어드민.
     *
     * 어드민(`/api/admin` 하위 전체)은 운영자 콘솔 트래픽이라 사용자 행동 로그와 성격이 다르다.
     * 접근/진입 로그는 남기지 않지만, GlobalExceptionHandler 의 예외 로그(warn/error)는
     * 어드민도 그대로 남는다 — 어드민이 터져도 원인은 봐야 하니까.
     */
    private val skipPrefixes = listOf(
        "/api/admin",
        "/api/healthcheck",
        "/actuator",
        "/v3/api-docs",
        "/swagger-ui",
        "/favicon.ico"
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val uri = request.requestURI
        val skipped = skipPrefixes.any { uri.startsWith(it) }

        // 제외 대상은 사용자 폴더에 쌓이면 안 되므로 파일 라우팅만 끊는다.
        // userId 는 남겨둬서 예외가 터졌을 때 메인 로그로 추적은 가능하게 한다.
        if (skipped) {
            LogUserContext.detachUserFile()
        }

        val start = System.currentTimeMillis()
        try {
            filterChain.doFilter(request, response)
        } finally {
            if (!skipped) {
                val latency = System.currentTimeMillis() - start
                val query = request.queryString?.let { "?$it" } ?: ""
                log.info(
                    "action=API 진입, method={}, path={}{}, userId={}, status={}, latency={}ms",
                    request.method,
                    uri,
                    query,
                    LogUserContext.currentUserId(),
                    response.status,
                    latency
                )
            }
            LogUserContext.clear()
        }
    }
}
