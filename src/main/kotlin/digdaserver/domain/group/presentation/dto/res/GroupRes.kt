package digdaserver.domain.group.presentation.dto.res

import digdaserver.domain.group.domain.entity.Group
import java.time.LocalDateTime

data class GroupRes(
    val id: Long,
    val name: String,
    val description: String?,
    val inviteCode: String,
    val memberCount: Long,
    val createdAt: LocalDateTime
) {
    companion object {
        fun of(group: Group, memberCount: Long): GroupRes {
            return GroupRes(
                id = group.id,
                name = group.name,
                description = group.description,
                inviteCode = group.inviteCode,
                memberCount = memberCount,
                createdAt = group.createdAt
            )
        }
    }
}
