package digdaserver.domain.device.presentation.controller

import digdaserver.domain.device.application.service.DeviceService
import digdaserver.domain.device.presentation.dto.req.DeviceDiagnosticRequest
import digdaserver.domain.device.presentation.dto.req.RegisterDeviceRequest
import digdaserver.domain.device.presentation.dto.res.RegisterDeviceResponse
import digdaserver.global.infra.logging.LogUserContext.currentUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "Device", description = "디바이스(FCM) API")
class DeviceController(
    private val deviceService: DeviceService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "디바이스 토큰 등록", description = "앱 시작 시 또는 토큰 갱신 시 FCM 토큰을 등록합니다. 동일 토큰이면 upsert.")
    @PostMapping("/devices")
    fun registerDevice(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: RegisterDeviceRequest
    ): ResponseEntity<RegisterDeviceResponse> {
        // FCM 토큰은 그 자체로 푸시를 쏠 수 있는 자격증명이라 값은 절대 남기지 않는다.
        log.info(
            "api=POST /devices, userId={}, platform={}, tokenLength={}",
            currentUserId(),
            request.platform,
            request.token.length
        )
        val response = deviceService.registerDevice(UUID.fromString(userId), request.token, request.platform)
        log.info(
            "api=POST /devices 완료, userId={}, platform={}, deviceId={}",
            currentUserId(),
            request.platform,
            response.deviceId
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "디바이스 등록 진단 로깅", description = "iOS 가 FCM 토큰을 못 얻어 등록 실패 시 그 사유를 서버 로그로 남긴다(윈도우 디버깅용). DB 저장 없음.")
    @PostMapping("/devices/diagnostic")
    fun reportDiagnostic(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: DeviceDiagnosticRequest
    ): ResponseEntity<Void> {
        log.info(
            "api=POST /devices/diagnostic, userId={}, detailLength={}",
            currentUserId(),
            request.detail.length
        )
        deviceService.logDiagnostic(UUID.fromString(userId), request.detail)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "디바이스 토큰 해제", description = "로그아웃 시 또는 토큰 만료 시 디바이스를 해제합니다.")
    @DeleteMapping("/devices/{deviceId}")
    fun unregisterDevice(
        @AuthenticationPrincipal userId: String,
        @PathVariable deviceId: Long
    ): ResponseEntity<Void> {
        log.info("api=DELETE /devices/{}, userId={}", deviceId, currentUserId())
        deviceService.unregisterDevice(UUID.fromString(userId), deviceId)
        log.info("api=DELETE /devices/{} 완료, userId={}", deviceId, currentUserId())
        return ResponseEntity.noContent().build()
    }
}
