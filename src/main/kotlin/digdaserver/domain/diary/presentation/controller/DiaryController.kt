package digdaserver.domain.diary.presentation.controller

import digdaserver.domain.diary.application.service.DiaryService
import digdaserver.domain.diary.presentation.dto.res.DiaryRes
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/diary")
@Tag(name = "Diary", description = "다이어리 전용 API")
class DiaryController(
    private val diaryService: DiaryService
) {

    @Operation(summary = "내 다이어리 목록 조회 API", description = "내가 작성한 다이어리 목록을 최신순으로 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "내 다이어리 목록 조회 성공")
        ]
    )
    @GetMapping("/my")
    fun getMyDiaries(
        @AuthenticationPrincipal userId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<DiaryRes>> {
        return ResponseEntity.ok(diaryService.getMyDiaries(userId, page, size))
    }
}
