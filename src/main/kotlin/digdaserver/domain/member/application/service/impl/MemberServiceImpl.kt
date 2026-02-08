package digdaserver.domain.member.application.service.impl

import digdaserver.domain.heritage.domain.repository.HeritageRepository
import digdaserver.domain.member.application.service.MemberService
import digdaserver.domain.member.domain.entity.Member
import digdaserver.domain.member.domain.repository.MemberRepository
import digdaserver.domain.member.presentation.dto.member.res.HeritageDto
import digdaserver.domain.member.presentation.dto.member.res.MyPageRes
import digdaserver.global.infra.exception.error.ErrorCode
import digdaserver.global.infra.exception.error.HistoryException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class MemberServiceImpl(
    private val memberRepository: MemberRepository,
    private val heritageRepository: HeritageRepository
) : MemberService {

    override fun infoMyPage(userId: String): MyPageRes {
        val member = getMemberOrThrow(userId)

        val heritageDtos = heritageRepository.findByMember(member)
            .map { HeritageDto.of(it.heritageUrl, it.heritageText) }

        return MyPageRes.of(
            member.name,
            member.profile,
            heritageDtos
        )
    }

    private fun getMemberOrThrow(userId: String): Member {
        return memberRepository.findById(userId)
            .orElseThrow { HistoryException(ErrorCode.USER_NOT_EXIST) }
    }
}
