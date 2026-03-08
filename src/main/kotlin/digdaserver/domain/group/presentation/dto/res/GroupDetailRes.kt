package digdaserver.domain.group.presentation.dto.res

import digdaserver.domain.group.domain.entity.Group
import digdaserver.domain.group.domain.entity.GroupRole
import java.time.LocalDateTime

data class GroupDetailRes(
    val id: Long,
    val name: String,
    val description: String?,
    val inviteCode: String,
    val memberCount: Long,
    val role: GroupRole,
    val createdAt: LocalDateTime
) {
    companion object {
        fun of(group: Group, memberCount: Long, role: GroupRole): GroupDetailRes {
            return GroupDetailRes(
                id = group.id,
                name = group.name,
                description = group.description,
                inviteCode = group.inviteCode,
                memberCount = memberCount,
                role = role,
                createdAt = group.createdAt
            )
        }
    }
}
