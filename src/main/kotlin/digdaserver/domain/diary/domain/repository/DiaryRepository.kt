package digdaserver.domain.diary.domain.repository

import digdaserver.domain.diary.domain.entity.Diary
import digdaserver.domain.group.domain.entity.Group
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DiaryRepository : JpaRepository<Diary, Long> {
    fun findAllByGroupOrderByCreatedAtDesc(group: Group, pageable: Pageable): List<Diary>
    fun findAllByAuthorIdOrderByCreatedAtDesc(authorId: String, pageable: Pageable): List<Diary>
}
