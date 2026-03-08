package digdaserver.domain.group.presentation.dto.res

import digdaserver.domain.group.domain.entity.Group
import digdaserver.domain.group.domain.entity.GroupRole

data class GroupSummaryRes(
    val id: Long,
    val name: String,
    val description: String?,
    val memberCount: Long,
    val role: GroupRole
) {
    companion object {
        fun of(group: Group, memberCount: Long, role: GroupRole): GroupSummaryRes {
            return GroupSummaryRes(
                id = group.id,
                name = group.name,
                description = group.description,
                memberCount = memberCount,
                role = role
            )
        }
    }
}
