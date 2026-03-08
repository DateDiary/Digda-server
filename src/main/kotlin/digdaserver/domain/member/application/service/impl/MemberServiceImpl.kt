package digdaserver.domain.member.application.service.impl

import digdaserver.domain.member.application.service.MemberService
import digdaserver.domain.member.domain.entity.Member
import digdaserver.domain.member.domain.repository.MemberRepository
import digdaserver.domain.member.presentation.dto.member.res.MyPageRes
import digdaserver.global.infra.exception.error.ErrorCode
import digdaserver.global.infra.exception.error.DigdaServerException
import digdaserver.global.infra.s3.presentation.application.S3Service
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class MemberServiceImpl(
    private val memberRepository: MemberRepository,
    private val s3Service: S3Service,
) : MemberService {

    override fun infoMyPage(userId: String): MyPageRes {
        val member = getMemberOrThrow(userId)
        return MyPageRes.of(member.name, member.profile)
    }

    override fun updateName(userId: String, name: String): MyPageRes {
        val member = getMemberOrThrow(userId)
        member.updateName(name)
        member.updateIsActivateNameFlag()
        return MyPageRes.of(member.name, member.profile)
    }

    override fun updateProfileImage(userId: String, file: MultipartFile): MyPageRes {
        val member = getMemberOrThrow(userId)
        val imageUrl = s3Service.storeImage(file, userId)
        member.updateProfile(imageUrl)
        return MyPageRes.of(member.name, member.profile)
    }

    override fun deleteAccount(userId: String) {
        val member = getMemberOrThrow(userId)
        memberRepository.delete(member)
    }

    private fun getMemberOrThrow(userId: String): Member {
        return memberRepository.findById(userId)
            .orElseThrow { DigdaServerException(ErrorCode.USER_NOT_EXIST) }
    }
}
