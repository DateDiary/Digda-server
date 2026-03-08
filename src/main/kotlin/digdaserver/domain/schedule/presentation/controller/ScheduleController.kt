package digdaserver.domain.schedule.presentation.controller

import digdaserver.domain.schedule.application.service.ScheduleService
import digdaserver.domain.schedule.presentation.dto.req.CreateScheduleReq
import digdaserver.domain.schedule.presentation.dto.req.UpdateScheduleReq
import digdaserver.domain.schedule.presentation.dto.res.ScheduleDetailRes
import digdaserver.domain.schedule.presentation.dto.res.ScheduleRes
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/group/{groupId}/schedule")
@Tag(name = "Schedule", description = "일정 전용 API")
class ScheduleController(
    private val scheduleService: ScheduleService
) {

    @Operation(summary = "일정 생성 API", description = "그룹에 새로운 일정을 생성합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "일정 생성 성공"),
            ApiResponse(responseCode = "406", description = "그룹 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "그룹 멤버가 아님"),
            ApiResponse(responseCode = "422", description = "잘못된 파라미터 (endAt이 startAt보다 이전)")
        ]
    )
    @PostMapping
    fun createSchedule(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @Valid @RequestBody req: CreateScheduleReq
    ): ResponseEntity<ScheduleRes> {
        return ResponseEntity.status(201).body(scheduleService.createSchedule(userId, groupId, req))
    }

    @Operation(summary = "일정 목록 조회 API", description = "그룹의 특정 월 일정 목록을 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "일정 목록 조회 성공"),
            ApiResponse(responseCode = "406", description = "그룹 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
        ]
    )
    @GetMapping
    fun getSchedules(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @RequestParam month: String
    ): ResponseEntity<List<ScheduleRes>> {
        return ResponseEntity.ok(scheduleService.getSchedules(userId, groupId, month))
    }

    @Operation(summary = "일정 상세 조회 API", description = "일정 상세 정보를 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "일정 상세 조회 성공"),
            ApiResponse(responseCode = "406", description = "그룹 또는 일정 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
        ]
    )
    @GetMapping("/{scheduleId}")
    fun getScheduleDetail(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @PathVariable scheduleId: Long
    ): ResponseEntity<ScheduleDetailRes> {
        return ResponseEntity.ok(scheduleService.getScheduleDetail(userId, groupId, scheduleId))
    }

    @Operation(summary = "일정 수정 API", description = "일정을 수정합니다. 작성자만 수정 가능합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "일정 수정 성공"),
            ApiResponse(responseCode = "406", description = "그룹 또는 일정 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "권한 없음"),
            ApiResponse(responseCode = "422", description = "잘못된 파라미터 (endAt이 startAt보다 이전)")
        ]
    )
    @PatchMapping("/{scheduleId}")
    fun updateSchedule(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @PathVariable scheduleId: Long,
        @RequestBody req: UpdateScheduleReq
    ): ResponseEntity<ScheduleRes> {
        return ResponseEntity.ok(scheduleService.updateSchedule(userId, groupId, scheduleId, req))
    }

    @Operation(summary = "일정 삭제 API", description = "일정을 삭제합니다. 작성자만 삭제 가능합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "일정 삭제 성공"),
            ApiResponse(responseCode = "406", description = "그룹 또는 일정 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "권한 없음")
        ]
    )
    @DeleteMapping("/{scheduleId}")
    fun deleteSchedule(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @PathVariable scheduleId: Long
    ): ResponseEntity<Unit> {
        scheduleService.deleteSchedule(userId, groupId, scheduleId)
        return ResponseEntity.noContent().build()
    }
}
