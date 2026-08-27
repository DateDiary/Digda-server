package digdaserver.domain.ledger.application.service.impl

import digdaserver.domain.group_room.domain.repository.GroupRoomRepository
import digdaserver.domain.ledger.application.service.LedgerService
import digdaserver.domain.ledger.application.service.ScheduleExpenseWriter
import digdaserver.domain.ledger.domain.entity.ExpenseCategory
import digdaserver.domain.ledger.domain.entity.ScheduleExpense
import digdaserver.domain.ledger.domain.repository.ScheduleExpenseRepository
import digdaserver.domain.ledger.presentation.dto.req.ExpenseWriteRequest
import digdaserver.domain.ledger.presentation.dto.res.GroupLedgerResponse
import digdaserver.domain.ledger.presentation.dto.res.LedgerCategoryStat
import digdaserver.domain.ledger.presentation.dto.res.LedgerDailyStat
import digdaserver.domain.ledger.presentation.dto.res.LedgerMemberStat
import digdaserver.domain.ledger.presentation.dto.res.LedgerScheduleStat
import digdaserver.domain.ledger.presentation.dto.res.ScheduleExpenseListResponse
import digdaserver.domain.membership.domain.repository.MembershipRepository
import digdaserver.domain.schedule.domain.repository.ScheduleRepository
import digdaserver.domain.schedule.presentation.dto.res.UserSummary
import digdaserver.domain.user.domain.repository.UserRepository
import digdaserver.global.infra.exception.error.DigdaException
import digdaserver.global.infra.exception.error.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
@Transactional(readOnly = true)
class LedgerServiceImpl(
    private val expenseRepository: ScheduleExpenseRepository,
    private val groupRoomRepository: GroupRoomRepository,
    private val membershipRepository: MembershipRepository,
    private val scheduleRepository: ScheduleRepository,
    private val userRepository: UserRepository,
    private val expenseWriter: ScheduleExpenseWriter
) : LedgerService {

    companion object {
        /** 월 범위 필드(`firstEntryMonth`/`lastEntryMonth`) 표기 — `2025-03`. */
        private val MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM")
    }

    override fun getMonthlyLedger(
        userId: UUID,
        groupRoomId: Long,
        year: Int,
        month: Int
    ): GroupLedgerResponse {
        if (month !in 1..12) throw DigdaException(ErrorCode.INVALID_PARAMETER)

        val groupRoom = groupRoomRepository.findById(groupRoomId)
            .orElseThrow { DigdaException(ErrorCode.GROUP_ROOM_NOT_FOUND) }
        if (groupRoom.deletedAt != null) throw DigdaException(ErrorCode.GROUP_ROOM_ALREADY_DELETED)

        membershipRepository.findByGroupRoomIdAndUserId(groupRoomId, userId)
            .orElseThrow { DigdaException(ErrorCode.NOT_GROUP_ROOM_MEMBER) }

        val ym = YearMonth.of(year, month)
        val from = ym.atDay(1)
        val to = ym.atEndOfMonth()
        val prev = ym.minusMonths(1)

        val expenses = expenseRepository.findAllByGroupRoomIdAndPeriod(groupRoomId, from, to)
        val prevExpenses = expenseRepository
            .findAllByGroupRoomIdAndPeriod(groupRoomId, prev.atDay(1), prev.atEndOfMonth())

        val total = expenses.sumOf { it.amount }

        return GroupLedgerResponse(
            year = year,
            month = month,
            totalAmount = total,
            prevMonthTotal = prevExpenses.sumOf { it.amount },
            allTimeTotal = expenseRepository.sumAmountByGroupRoomId(groupRoomId) ?: 0L,
            entryCount = expenses.size,
            categories = buildCategoryStats(expenses, total),
            members = buildMemberStats(expenses, total),
            schedules = buildScheduleStats(expenses),
            daily = buildDailyStats(expenses),
            firstEntryMonth = expenseRepository.findFirstEntryDate(groupRoomId)?.format(MONTH_FORMAT),
            lastEntryMonth = expenseRepository.findLastEntryDate(groupRoomId)?.format(MONTH_FORMAT)
        )
    }

    @Transactional
    override fun addScheduleExpense(
        userId: UUID,
        groupRoomId: Long,
        scheduleId: Long,
        request: ExpenseWriteRequest
    ): ScheduleExpenseListResponse {
        val groupRoom = groupRoomRepository.findById(groupRoomId)
            .orElseThrow { DigdaException(ErrorCode.GROUP_ROOM_NOT_FOUND) }
        if (groupRoom.deletedAt != null) throw DigdaException(ErrorCode.GROUP_ROOM_ALREADY_DELETED)

        // 일정과 같은 규칙 — 그룹 멤버면 누구나 금액을 적을 수 있다(작성자 제한 없음).
        membershipRepository.findByGroupRoomIdAndUserId(groupRoomId, userId)
            .orElseThrow { DigdaException(ErrorCode.NOT_GROUP_ROOM_MEMBER) }

        val schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow { DigdaException(ErrorCode.SCHEDULE_NOT_FOUND) }
        // 다른 그룹의 일정 id 로 남의 가계부에 금액을 꽂지 못하게 소속을 확인한다.
        if (schedule.groupRoom.id != groupRoomId) throw DigdaException(ErrorCode.SCHEDULE_NOT_FOUND)

        val author = userRepository.findById(userId)
            .orElseThrow { DigdaException(ErrorCode.USER_NOT_FOUND) }
        if (author.restricted) throw DigdaException(ErrorCode.USER_RESTRICTED)

        expenseWriter.append(schedule, groupRoomId, request, author)
        groupRoom.updateLastActivity()

        return ScheduleExpenseListResponse.from(
            scheduleId,
            expenseRepository.findAllByScheduleId(scheduleId)
        )
    }

    /**
     * 분류별 합계 — 금액 내림차순. 이 달에 쓰지 않은 분류는 목록에서 뺀다
     * (0원 막대가 그래프를 늘려 실제 지출 비교를 방해한다).
     */
    private fun buildCategoryStats(
        expenses: List<ScheduleExpense>,
        total: Long
    ): List<LedgerCategoryStat> {
        val byCategory: Map<ExpenseCategory, Long> = expenses
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }

        return byCategory.entries
            .sortedByDescending { it.value }
            .map { (category, amount) ->
                LedgerCategoryStat(
                    category = category,
                    label = category.label,
                    amount = amount,
                    ratio = ratioOf(amount, total)
                )
            }
    }

    /**
     * 낸 사람별 합계 — 금액 내림차순. 탈퇴 멤버(payer=null)와 '누가 냈는지 미지정'은
     * 둘 다 payer=null 한 묶음으로 합쳐진다.
     */
    private fun buildMemberStats(
        expenses: List<ScheduleExpense>,
        total: Long
    ): List<LedgerMemberStat> {
        val byPayer = expenses.groupBy { it.payer?.id }
        return byPayer.entries
            .map { (_, list) ->
                val amount = list.sumOf { it.amount }
                LedgerMemberStat(
                    payer = list.first().payer?.let { UserSummary.from(it) },
                    amount = amount,
                    ratio = ratioOf(amount, total)
                )
            }
            .sortedByDescending { it.amount }
    }

    /** 지출이 있는 일정별 합계 — 금액 내림차순. */
    private fun buildScheduleStats(expenses: List<ScheduleExpense>): List<LedgerScheduleStat> {
        return expenses
            .groupBy { it.schedule.id }
            .map { (_, list) ->
                val schedule = list.first().schedule
                LedgerScheduleStat(
                    scheduleId = schedule.id,
                    title = schedule.title,
                    color = schedule.color,
                    startDate = schedule.startDate,
                    amount = list.sumOf { it.amount },
                    entryCount = list.size
                )
            }
            .sortedByDescending { it.amount }
    }

    /** 날짜별 합계 — 지출이 있는 날만, 날짜 오름차순. */
    private fun buildDailyStats(expenses: List<ScheduleExpense>): List<LedgerDailyStat> {
        val byDate: Map<LocalDate, Long> = expenses
            .groupBy { it.schedule.startDate }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
        return byDate.entries
            .sortedBy { it.key }
            .map { (date, amount) -> LedgerDailyStat(date = date, amount = amount) }
    }

    /** 총액이 0 이면 0.0 — 앱에서 NaN 막대가 나오지 않도록 나눗셈은 여기서만 한다. */
    private fun ratioOf(amount: Long, total: Long): Double =
        if (total <= 0L) 0.0 else amount.toDouble() / total.toDouble()
}
