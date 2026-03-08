package digdaserver.domain.group.presentation.controller

import digdaserver.domain.group.application.service.GroupService
import digdaserver.domain.group.presentation.dto.req.CreateGroupReq
import digdaserver.domain.group.presentation.dto.req.JoinGroupReq
import digdaserver.domain.group.presentation.dto.req.UpdateGroupReq
import digdaserver.domain.group.presentation.dto.res.GroupDetailRes
import digdaserver.domain.group.presentation.dto.res.GroupMemberRes
import digdaserver.domain.group.presentation.dto.res.GroupRes
import digdaserver.domain.group.presentation.dto.res.GroupSummaryRes
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/group")
@Tag(name = "Group", description = "그룹 전용 API")
class GroupController(
    private val groupService: GroupService
) {

    @Operation(summary = "그룹 생성 API", description = "새로운 그룹을 생성합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "그룹 생성 성공"),
            ApiResponse(responseCode = "404", description = "유저 존재하지 않음")
        ]
    )
    @PostMapping
    fun createGroup(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody req: CreateGroupReq
    ): ResponseEntity<GroupRes> {
        return ResponseEntity.status(201).body(groupService.createGroup(userId, req))
    }

    @Operation(summary = "내 그룹 목록 조회 API", description = "내가 속한 그룹 목록을 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "그룹 목록 조회 성공")
        ]
    )
    @GetMapping
    fun getMyGroups(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<List<GroupSummaryRes>> {
        return ResponseEntity.ok(groupService.getMyGroups(userId))
    }

    @Operation(summary = "그룹 상세 조회 API", description = "그룹 상세 정보를 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "그룹 상세 조회 성공"),
            ApiResponse(responseCode = "406", description = "그룹 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
        ]
    )
    @GetMapping("/{groupId}")
    fun getGroupDetail(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long
    ): ResponseEntity<GroupDetailRes> {
        return ResponseEntity.ok(groupService.getGroupDetail(userId, groupId))
    }

    @Operation(summary = "그룹 수정 API", description = "그룹 정보를 수정합니다. 오너만 수정 가능합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "그룹 수정 성공"),
            ApiResponse(responseCode = "406", description = "그룹 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "권한 없음")
        ]
    )
    @PatchMapping("/{groupId}")
    fun updateGroup(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long,
        @RequestBody req: UpdateGroupReq
    ): ResponseEntity<GroupRes> {
        return ResponseEntity.ok(groupService.updateGroup(userId, groupId, req))
    }

    @Operation(summary = "그룹 삭제 API", description = "그룹을 삭제합니다. 오너만 삭제 가능합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "그룹 삭제 성공"),
            ApiResponse(responseCode = "406", description = "그룹 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "권한 없음")
        ]
    )
    @DeleteMapping("/{groupId}")
    fun deleteGroup(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long
    ): ResponseEntity<Unit> {
        groupService.deleteGroup(userId, groupId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "그룹 참여 API", description = "초대 코드로 그룹에 참여합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "그룹 참여 성공"),
            ApiResponse(responseCode = "404", description = "유저 존재하지 않음"),
            ApiResponse(responseCode = "406", description = "그룹 존재하지 않음"),
            ApiResponse(responseCode = "422", description = "이미 그룹 멤버")
        ]
    )
    @PostMapping("/join")
    fun joinGroup(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody req: JoinGroupReq
    ): ResponseEntity<GroupRes> {
        return ResponseEntity.status(201).body(groupService.joinGroup(userId, req))
    }

    @Operation(summary = "그룹 탈퇴 API", description = "그룹에서 탈퇴합니다. 오너는 탈퇴 불가합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "그룹 탈퇴 성공"),
            ApiResponse(responseCode = "406", description = "그룹 존재하지 않음"),
            ApiResponse(responseCode = "422", description = "오너는 탈퇴 불가")
        ]
    )
    @DeleteMapping("/{groupId}/leave")
    fun leaveGroup(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long
    ): ResponseEntity<Unit> {
        groupService.leaveGroup(userId, groupId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "그룹 멤버 목록 조회 API", description = "그룹 멤버 목록을 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "그룹 멤버 목록 조회 성공"),
            ApiResponse(responseCode = "406", description = "그룹 존재하지 않음")
        ]
    )
    @GetMapping("/{groupId}/members")
    fun getGroupMembers(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupId: Long
    ): ResponseEntity<List<GroupMemberRes>> {
        return ResponseEntity.ok(groupService.getGroupMembers(groupId))
    }
}
