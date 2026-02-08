package digdaserver.domain.member.domain.repository

import digdaserver.domain.member.domain.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface MemberRepository : JpaRepository<Member, String> {
    fun findByEmail(email: String): Optional<Member>
}
