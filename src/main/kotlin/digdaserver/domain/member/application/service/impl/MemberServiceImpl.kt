package digdaserver.domain.member.application.service.impl

import digdaserver.domain.member.application.service.MemberService
import digdaserver.domain.member.domain.entity.Member
import digdaserver.domain.member.domain.repository.MemberRepository
import digdaserver.domain.member.presentation.dto.member.res.MyPageRes
import digdaserver.global.infra.exception.error.ErrorCode
import digdaserver.global.infra.exception.error.DigdaServerException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class MemberServiceImpl(
    private val memberRepository: MemberRepository,
) : MemberService {

    override fun infoMyPage(userId: String): MyPageRes {
        val member = getMemberOrThrow(userId)

        return MyPageRes.of(
            member.name,
            member.profile
        )
    }

    private fun getMemberOrThrow(userId: String): Member {
        return memberRepository.findById(userId)
            .orElseThrow { DigdaServerException(ErrorCode.USER_NOT_EXIST) }
    }
}
