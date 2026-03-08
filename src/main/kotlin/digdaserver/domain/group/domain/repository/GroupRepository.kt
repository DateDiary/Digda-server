package digdaserver.domain.group.domain.repository

import digdaserver.domain.group.domain.entity.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface GroupRepository : JpaRepository<Group, Long> {
    fun findByInviteCode(inviteCode: String): Optional<Group>
}
