package digdaserver.domain.invite.presentation.controller

import digdaserver.domain.invite.application.service.InviteService
import digdaserver.domain.invite.presentation.dto.req.InviteCodeRequest
import digdaserver.domain.invite.presentation.dto.res.InviteCodeResponse
import digdaserver.domain.invite.presentation.dto.res.InviteJoinResponse
import digdaserver.domain.invite.presentation.dto.res.InviteValidateResponse
import digdaserver.global.infra.logging.LogUserContext.currentUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "Invite", description = "초대 코드 API")
class InviteController(
    private val inviteService: InviteService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(
        summary = "초대 코드 조회/발급",
        description = "유효한(만료 전) 초대 코드가 있으면 그대로 반환하고, 없거나 만료된 경우에만 새 코드를 발급합니다. 방장만 가능합니다."
    )
    @PostMapping("/group-rooms/{groupRoomId}/invites")
    fun regenerateInviteCode(
        @AuthenticationPrincipal userId: String,
        @PathVariable groupRoomId: Long
    ): ResponseEntity<InviteCodeResponse> {
        log.info("api=POST /group-rooms/{}/invites, userId={}", groupRoomId, currentUserId())
        val response = inviteService.regenerateInviteCode(UUID.fromString(userId), groupRoomId)
        // 코드 값 자체는 알면 누구나 입장 가능하므로 만료시각만 남긴다.
        log.info(
            "api=POST /group-rooms/{}/invites 완료, userId={}, expiresAt={}",
            groupRoomId,
            currentUserId(),
            response.expiresAt
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "초대 코드 검증", description = "초대 코드를 입력하면 그룹방 미리보기 정보를 반환합니다.")
    @PostMapping("/invites/validate")
    fun validateInviteCode(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: InviteCodeRequest
    ): ResponseEntity<InviteValidateResponse> {
        log.info(
            "api=POST /invites/validate, userId={}, code={}",
            currentUserId(),
            maskCode(request.code)
        )
        val response = inviteService.validateInviteCode(UUID.fromString(userId), request.code)
        log.info(
            "api=POST /invites/validate 완료, userId={}, code={}, memberCount={}, maxMembers={}",
            currentUserId(),
            maskCode(request.code),
            response.memberCount,
            response.maxMembers
        )
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "초대 코드로 참여", description = "초대 코드를 통해 그룹방에 참여합니다.")
    @PostMapping("/invites/join")
    fun joinByInviteCode(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: InviteCodeRequest
    ): ResponseEntity<InviteJoinResponse> {
        log.info(
            "api=POST /invites/join, userId={}, code={}",
            currentUserId(),
            maskCode(request.code)
        )
        val response = inviteService.joinByInviteCode(UUID.fromString(userId), request.code)
        log.info(
            "api=POST /invites/join 완료, userId={}, code={}, groupRoomId={}, memberCount={}",
            currentUserId(),
            maskCode(request.code),
            response.groupRoom.id,
            response.memberships.size
        )
        return ResponseEntity.ok(response)
    }

    /**
     * 초대 코드는 알면 누구나 그룹에 들어올 수 있는 값이라 통째로 남기지 않는다.
     * 다만 "코드를 넣었는데 안 돼요" 류 제보를 추적하려면 어느 코드였는지는 구분돼야 해서
     * 앞 2자리만 남기고 가린다.
     */
    private fun maskCode(code: String): String =
        if (code.length <= 2) "**" else code.take(2) + "*".repeat(code.length - 2)
}
