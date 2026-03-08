package digdaserver.domain.group.presentation.dto.res

import digdaserver.domain.group.domain.entity.GroupMember
import digdaserver.domain.group.domain.entity.GroupRole
import java.time.LocalDateTime

data class GroupMemberRes(
    val memberId: String,
    val memberName: String,
    val memberProfile: String?,
    val role: GroupRole,
    val joinedAt: LocalDateTime
) {
    companion object {
        fun of(groupMember: GroupMember): GroupMemberRes {
            return GroupMemberRes(
                memberId = groupMember.memberId,
                memberName = groupMember.memberName,
                memberProfile = groupMember.memberProfile,
                role = groupMember.role,
                joinedAt = groupMember.joinedAt
            )
        }
    }
}
