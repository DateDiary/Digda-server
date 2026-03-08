package digdaserver.domain.diary.presentation.dto.res

import digdaserver.domain.diary.domain.entity.Diary
import java.time.LocalDateTime

data class DiaryDetailRes(
    val id: Long,
    val groupId: Long,
    val groupName: String,
    val authorId: String,
    val authorName: String,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun of(diary: Diary): DiaryDetailRes {
            return DiaryDetailRes(
                id = diary.id,
                groupId = diary.group.id,
                groupName = diary.group.name,
                authorId = diary.authorId,
                authorName = diary.authorName,
                title = diary.title,
                content = diary.content,
                imageUrl = diary.imageUrl,
                createdAt = diary.createdAt,
                updatedAt = diary.updatedAt
            )
        }
    }
}
