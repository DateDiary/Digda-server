package digdaserver.domain.ledger.domain.entity

import digdaserver.domain.schedule.domain.entity.Schedule
import digdaserver.domain.user.domain.entity.User
import digdaserver.global.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

/**
 * 일정에 붙는 지출 한 건 (그룹 가계부).
 *
 * 가계부는 "그룹방당 하나"이고, 모든 지출은 반드시 일정에 매달린다. 그래서 지출의
 * 소속 그룹·발생 날짜는 별도 컬럼 없이 [schedule] 을 통해 얻는다 — 일정 날짜를 나중에
 * 옮겨도 가계부 월 집계가 자동으로 따라오게 하기 위함(비정규화 시 drift 발생).
 *
 * [payer]/[createdBy] 는 nullable — 회원탈퇴 시 지출 기록 자체는 보존하고 사람만
 * 비워 "탈퇴한 멤버"로 표시한다.
 */
@Entity
@Table(name = "schedule_expense")
class ScheduleExpense(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_expense_id")
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    val schedule: Schedule,

    /** 돈을 낸 사람. 그룹 멤버여야 하며, 탈퇴 시 NULL 로 남는다. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id")
    var payer: User? = null,

    /** 원(KRW) 단위 정수. 소수점은 다루지 않는다. */
    @Column(nullable = false)
    var amount: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    var category: ExpenseCategory,

    @Column(length = 100)
    var memo: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    val createdBy: User? = null

) : BaseTimeEntity()
