package digdaserver.global.infra.feignclient.openai

import digdaserver.domain.openai.dto.image.req.OpenAIVisionReq
import digdaserver.domain.openai.dto.image.res.OpenAIVisionRes
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "OpenAIVisionAPI",
    url = "\${openai.api.url}"
)
interface OpenAIVisionFeignClient {

    @PostMapping("/v1/chat/completions")
    fun generateVision(
        @RequestHeader("Authorization") apiKey: String,
        @RequestBody request: OpenAIVisionReq
    ): OpenAIVisionRes
}
