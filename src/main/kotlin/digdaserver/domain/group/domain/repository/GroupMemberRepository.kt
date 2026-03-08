package digdaserver.domain.group.domain.repository

import digdaserver.domain.group.domain.entity.Group
import digdaserver.domain.group.domain.entity.GroupMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface GroupMemberRepository : JpaRepository<GroupMember, Long> {
    fun findAllByMemberId(memberId: String): List<GroupMember>
    fun findAllByGroup(group: Group): List<GroupMember>
    fun findByGroupAndMemberId(group: Group, memberId: String): Optional<GroupMember>
    fun countByGroup(group: Group): Long
    fun existsByGroupAndMemberId(group: Group, memberId: String): Boolean
}
