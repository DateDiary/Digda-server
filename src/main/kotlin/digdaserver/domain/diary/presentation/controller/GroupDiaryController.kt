package digdaserver.domain.diary.presentation.controller

import digdaserver.domain.diary.application.service.DiaryService
import digdaserver.domain.diary.presentation.dto.req.CreateDiaryReq
import digdaserver.domain.diary.presentation.dto.req.UpdateDiaryReq
import digdaserver.domain.diary.presentation.dto.res.DiaryDetailRes
import digdaserver.domain.diary.presentation.dto.res.DiaryRes
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
@RequestMapping("/api/group/{groupId}/diary")
@Tag(name = "Diary", description = "다이어리 전용 API")
class GroupDiaryController(
    private val diaryService: DiaryService
) {

    @Operation(summary = "다이어리 작성 API", description = "그룹에 새로운 다이어리를 작성합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "다이어리 작성 성공"),
            ApiResponse(responseCode = "406", description = "그룹 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
        ]
    )
    @PostMapping
    fun createDiary(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @Valid @RequestBody req: CreateDiaryReq
    ): ResponseEntity<DiaryRes> {
        return ResponseEntity.status(201).body(diaryService.createDiary(userId, groupId, req))
    }

    @Operation(summary = "그룹 다이어리 목록 조회 API", description = "그룹의 다이어리 목록을 최신순으로 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "다이어리 목록 조회 성공"),
            ApiResponse(responseCode = "406", description = "그룹 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
        ]
    )
    @GetMapping
    fun getGroupDiaries(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<DiaryRes>> {
        return ResponseEntity.ok(diaryService.getGroupDiaries(userId, groupId, page, size))
    }

    @Operation(summary = "다이어리 상세 조회 API", description = "다이어리 상세 정보를 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "다이어리 상세 조회 성공"),
            ApiResponse(responseCode = "406", description = "그룹 또는 다이어리 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
        ]
    )
    @GetMapping("/{diaryId}")
    fun getDiaryDetail(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @PathVariable diaryId: Long
    ): ResponseEntity<DiaryDetailRes> {
        return ResponseEntity.ok(diaryService.getDiaryDetail(userId, groupId, diaryId))
    }

    @Operation(summary = "다이어리 수정 API", description = "다이어리를 수정합니다. 작성자만 수정 가능합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "다이어리 수정 성공"),
            ApiResponse(responseCode = "406", description = "그룹 또는 다이어리 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "권한 없음")
        ]
    )
    @PatchMapping("/{diaryId}")
    fun updateDiary(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @PathVariable diaryId: Long,
        @RequestBody req: UpdateDiaryReq
    ): ResponseEntity<DiaryRes> {
        return ResponseEntity.ok(diaryService.updateDiary(userId, groupId, diaryId, req))
    }

    @Operation(summary = "다이어리 삭제 API", description = "다이어리를 삭제합니다. 작성자만 삭제 가능합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "다이어리 삭제 성공"),
            ApiResponse(responseCode = "406", description = "그룹 또는 다이어리 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "권한 없음")
        ]
    )
    @DeleteMapping("/{diaryId}")
    fun deleteDiary(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @PathVariable diaryId: Long
    ): ResponseEntity<Unit> {
        diaryService.deleteDiary(userId, groupId, diaryId)
        return ResponseEntity.noContent().build()
    }
}
