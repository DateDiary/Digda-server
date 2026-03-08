package digdaserver.domain.diary.application.service

import digdaserver.domain.diary.presentation.dto.req.CreateDiaryReq
import digdaserver.domain.diary.presentation.dto.req.UpdateDiaryReq
import digdaserver.domain.diary.presentation.dto.res.DiaryDetailRes
import digdaserver.domain.diary.presentation.dto.res.DiaryRes

interface DiaryService {
    fun createDiary(userId: String, groupId: Long, req: CreateDiaryReq): DiaryRes
    fun getGroupDiaries(userId: String, groupId: Long, page: Int, size: Int): List<DiaryRes>
    fun getDiaryDetail(userId: String, groupId: Long, diaryId: Long): DiaryDetailRes
    fun updateDiary(userId: String, groupId: Long, diaryId: Long, req: UpdateDiaryReq): DiaryRes
    fun deleteDiary(userId: String, groupId: Long, diaryId: Long)
    fun getMyDiaries(userId: String, page: Int, size: Int): List<DiaryRes>
}
