package digdaserver.domain.group.application.service.impl

import digdaserver.domain.group.application.service.GroupService
import digdaserver.domain.group.domain.entity.Group
import digdaserver.domain.group.domain.entity.GroupMember
import digdaserver.domain.group.domain.entity.GroupRole
import digdaserver.domain.group.domain.repository.GroupMemberRepository
import digdaserver.domain.group.domain.repository.GroupRepository
import digdaserver.domain.group.presentation.dto.req.CreateGroupReq
import digdaserver.domain.group.presentation.dto.req.JoinGroupReq
import digdaserver.domain.group.presentation.dto.req.UpdateGroupReq
import digdaserver.domain.group.presentation.dto.res.GroupDetailRes
import digdaserver.domain.group.presentation.dto.res.GroupMemberRes
import digdaserver.domain.group.presentation.dto.res.GroupRes
import digdaserver.domain.group.presentation.dto.res.GroupSummaryRes
import digdaserver.domain.member.domain.repository.MemberRepository
import digdaserver.global.infra.exception.error.DigdaServerException
import digdaserver.global.infra.exception.error.ErrorCode
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class GroupServiceImpl(
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val memberRepository: MemberRepository
) : GroupService {

    override fun createGroup(userId: String, req: CreateGroupReq): GroupRes {
        val member = memberRepository.findById(userId)
            .orElseThrow { DigdaServerException(ErrorCode.USER_NOT_EXIST) }

        val inviteCode = generateInviteCode()

        val group = groupRepository.save(
            Group(
                name = req.name,
                description = req.description,
                inviteCode = inviteCode,
                ownerId = userId
            )
        )

        groupMemberRepository.save(
            GroupMember(
                group = group,
                memberId = userId,
                memberName = member.name,
                memberProfile = member.profile,
                role = GroupRole.OWNER
            )
        )

        val memberCount = groupMemberRepository.countByGroup(group)
        return GroupRes.of(group, memberCount)
    }

    override fun getMyGroups(userId: String): List<GroupSummaryRes> {
        val groupMembers = groupMemberRepository.findAllByMemberId(userId)
        return groupMembers.map { gm ->
            val memberCount = groupMemberRepository.countByGroup(gm.group)
            GroupSummaryRes.of(gm.group, memberCount, gm.role)
        }
    }

    override fun getGroupDetail(userId: String, groupId: Long): GroupDetailRes {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        val groupMember = groupMemberRepository.findByGroupAndMemberId(group, userId)
            .orElseThrow { DigdaServerException(ErrorCode.FORBIDDEN) }

        val memberCount = groupMemberRepository.countByGroup(group)
        return GroupDetailRes.of(group, memberCount, groupMember.role)
    }

    override fun updateGroup(userId: String, groupId: Long, req: UpdateGroupReq): GroupRes {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (group.ownerId != userId) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        req.name?.let { group.updateName(it) }
        req.description?.let { group.updateDescription(it) }

        val memberCount = groupMemberRepository.countByGroup(group)
        return GroupRes.of(group, memberCount)
    }

    override fun deleteGroup(userId: String, groupId: Long) {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (group.ownerId != userId) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val members = groupMemberRepository.findAllByGroup(group)
        groupMemberRepository.deleteAll(members)
        groupRepository.delete(group)
    }

    override fun joinGroup(userId: String, req: JoinGroupReq): GroupRes {
        val member = memberRepository.findById(userId)
            .orElseThrow { DigdaServerException(ErrorCode.USER_NOT_EXIST) }

        val group = groupRepository.findByInviteCode(req.inviteCode)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.INVALID_PARAMETER)
        }

        groupMemberRepository.save(
            GroupMember(
                group = group,
                memberId = userId,
                memberName = member.name,
                memberProfile = member.profile,
                role = GroupRole.MEMBER
            )
        )

        val memberCount = groupMemberRepository.countByGroup(group)
        return GroupRes.of(group, memberCount)
    }

    override fun leaveGroup(userId: String, groupId: Long) {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        val groupMember = groupMemberRepository.findByGroupAndMemberId(group, userId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (groupMember.role == GroupRole.OWNER) {
            throw DigdaServerException(ErrorCode.INVALID_PARAMETER)
        }

        groupMemberRepository.delete(groupMember)
    }

    override fun getGroupMembers(groupId: Long): List<GroupMemberRes> {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        return groupMemberRepository.findAllByGroup(group).map { GroupMemberRes.of(it) }
    }

    private fun generateInviteCode(): String {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).uppercase()
    }
}
