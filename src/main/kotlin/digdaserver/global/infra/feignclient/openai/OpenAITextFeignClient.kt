/*
package historywowa.global.infra.feignclient.openai

import historywowa.domain.openai.dto.text.OpenAITextRequest
import historywowa.domain.openai.dto.text.OpenAITextResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
        name = "OpenAITextAPI",
        url = "\${openai.api.url}"
)
interface OpenAITextFeignClient {

    @PostMapping("/v1/chat/completions")
    fun generateText(
            @RequestHeader("Authorization") apiKey: String,
            @RequestBody request: OpenAITextRequest
    ): OpenAITextResponse
}

*/
