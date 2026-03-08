package digdaserver.domain.diary.application.service.impl

import digdaserver.domain.diary.application.service.DiaryService
import digdaserver.domain.diary.domain.entity.Diary
import digdaserver.domain.diary.domain.repository.DiaryRepository
import digdaserver.domain.diary.presentation.dto.req.CreateDiaryReq
import digdaserver.domain.diary.presentation.dto.req.UpdateDiaryReq
import digdaserver.domain.diary.presentation.dto.res.DiaryDetailRes
import digdaserver.domain.diary.presentation.dto.res.DiaryRes
import digdaserver.domain.group.domain.repository.GroupMemberRepository
import digdaserver.domain.group.domain.repository.GroupRepository
import digdaserver.domain.member.domain.repository.MemberRepository
import digdaserver.global.infra.exception.error.DigdaServerException
import digdaserver.global.infra.exception.error.ErrorCode
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
@Transactional
class DiaryServiceImpl(
    private val diaryRepository: DiaryRepository,
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val memberRepository: MemberRepository
) : DiaryService {

    override fun createDiary(userId: String, groupId: Long, req: CreateDiaryReq): DiaryRes {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val member = memberRepository.findById(userId)
            .orElseThrow { DigdaServerException(ErrorCode.USER_NOT_EXIST) }

        val diary = diaryRepository.save(
            Diary(
                group = group,
                authorId = userId,
                authorName = member.name,
                title = req.title,
                content = req.content,
                imageUrl = req.imageUrl
            )
        )

        return DiaryRes.of(diary)
    }

    override fun getGroupDiaries(userId: String, groupId: Long, page: Int, size: Int): List<DiaryRes> {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val pageable = PageRequest.of(page, size)
        return diaryRepository.findAllByGroupOrderByCreatedAtDesc(group, pageable)
            .map { DiaryRes.of(it) }
    }

    override fun getDiaryDetail(userId: String, groupId: Long, diaryId: Long): DiaryDetailRes {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val diary = diaryRepository.findById(diaryId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        return DiaryDetailRes.of(diary)
    }

    override fun updateDiary(userId: String, groupId: Long, diaryId: Long, req: UpdateDiaryReq): DiaryRes {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val diary = diaryRepository.findById(diaryId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (diary.authorId != userId) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        diary.update(req.title, req.content, req.imageUrl)

        return DiaryRes.of(diary)
    }

    override fun deleteDiary(userId: String, groupId: Long, diaryId: Long) {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val diary = diaryRepository.findById(diaryId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (diary.authorId != userId) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        diaryRepository.delete(diary)
    }

    override fun getMyDiaries(userId: String, page: Int, size: Int): List<DiaryRes> {
        val pageable = PageRequest.of(page, size)
        return diaryRepository.findAllByAuthorIdOrderByCreatedAtDesc(userId, pageable)
            .map { DiaryRes.of(it) }
    }
}
