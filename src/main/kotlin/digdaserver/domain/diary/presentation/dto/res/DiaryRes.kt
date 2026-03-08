package digdaserver.domain.diary.presentation.dto.res

import digdaserver.domain.diary.domain.entity.Diary
import java.time.LocalDateTime

data class DiaryRes(
    val id: Long,
    val groupId: Long,
    val groupName: String,
    val authorId: String,
    val authorName: String,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun of(diary: Diary): DiaryRes {
            return DiaryRes(
                id = diary.id,
                groupId = diary.group.id,
                groupName = diary.group.name,
                authorId = diary.authorId,
                authorName = diary.authorName,
                title = diary.title,
                content = diary.content,
                imageUrl = diary.imageUrl,
                createdAt = diary.createdAt
            )
        }
    }
}
