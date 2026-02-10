package digdaserver.global.infra.feignclient.ios

import feign.Logger
import feign.Request
import feign.codec.ErrorDecoder
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean

class AppleOAuth2FeignConfig {

    @Bean
    fun feignLoggerLevel(): Logger.Level = Logger.Level.FULL

    @Bean
    fun requestOptions(): Request.Options =
        Request.Options(
            10000, // connect timeout
            30000 // read timeout
        )

    @Bean
    fun errorDecoder(): ErrorDecoder = AppleOAuth2ErrorDecoder()

    class AppleOAuth2ErrorDecoder : ErrorDecoder {

        private val log = LoggerFactory.getLogger(AppleOAuth2ErrorDecoder::class.java)
        private val defaultDecoder = ErrorDecoder.Default()

        override fun decode(methodKey: String, response: feign.Response): Exception {
            log.error(
                "Apple OAuth2 API 에러 - Method: {}, Status: {}, Reason: {}",
                methodKey,
                response.status(),
                response.reason()
            )

            return defaultDecoder.decode(methodKey, response)
        }
    }
}
