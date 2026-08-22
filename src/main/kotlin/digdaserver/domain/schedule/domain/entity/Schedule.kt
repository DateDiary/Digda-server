package digdaserver.domain.schedule.domain.entity

import digdaserver.domain.group_room.domain.entity.GroupRoom
import digdaserver.domain.ledger.domain.entity.ScheduleExpense
import digdaserver.domain.user.domain.entity.User
import digdaserver.global.common.entity.BaseTimeEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "schedule")
class Schedule(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_room_id", nullable = false)
    val groupRoom: GroupRoom,

    @Column(nullable = false, length = 50)
    var title: String,

    @Column(nullable = false, length = 7)
    var color: String,

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate,

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDate,

    @Column(name = "start_time")
    var startTime: LocalTime? = null,

    @Column(name = "end_time")
    var endTime: LocalTime? = null,

    @Column(name = "all_day", nullable = false)
    var allDay: Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    val createdBy: User

) : BaseTimeEntity() {

    @OneToMany(mappedBy = "schedule", cascade = [CascadeType.ALL], orphanRemoval = true)
    val participants: MutableList<ScheduleParticipant> = mutableListOf()

    /**
     * 그룹 가계부 지출.
     *
     * 읽기/쓰기는 전부 ScheduleExpenseRepository 를 통해 하고 이 컬렉션은 건드리지 않는다
     * (컬렉션을 로드하지 않으므로 orphanRemoval 스냅샷 비교와 충돌하지 않는다).
     * 여기 매핑을 두는 목적은 **삭제 전파 하나** — 일정/그룹방이 지워질 때 지출이 FK 로
     * 남아 삭제가 실패하는 것을 막는다.
     */
    @OneToMany(mappedBy = "schedule", cascade = [CascadeType.ALL], orphanRemoval = true)
    val expenses: MutableList<ScheduleExpense> = mutableListOf()

    fun update(
        title: String?,
        color: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        startTime: LocalTime?,
        endTime: LocalTime?,
        allDay: Boolean?
    ) {
        title?.let { this.title = it }
        color?.let { this.color = it }
        startDate?.let { this.startDate = it }
        endDate?.let { this.endDate = it }
        startTime?.let { this.startTime = it }
        endTime?.let { this.endTime = it }
        allDay?.let { this.allDay = it }
    }

    fun clearParticipants() {
        this.participants.clear()
    }

    fun addParticipant(participant: ScheduleParticipant) {
        this.participants.add(participant)
    }
}
