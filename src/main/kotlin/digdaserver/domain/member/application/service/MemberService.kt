package digdaserver.domain.member.application.service

import digdaserver.domain.member.presentation.dto.member.res.MyPageRes
import org.springframework.web.multipart.MultipartFile

interface MemberService {
    fun infoMyPage(userId: String): MyPageRes
    fun updateName(userId: String, name: String): MyPageRes
    fun updateProfileImage(userId: String, file: MultipartFile): MyPageRes
    fun deleteAccount(userId: String)
}
