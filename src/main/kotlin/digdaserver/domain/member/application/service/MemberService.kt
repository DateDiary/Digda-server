package digdaserver.domain.member.application.service

import digdaserver.domain.member.presentation.dto.member.res.MyPageRes

interface MemberService {
    fun infoMyPage(userId: String): MyPageRes
}
