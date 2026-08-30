package digdaserver.domain.ledger.application.service

import digdaserver.domain.ledger.domain.entity.ScheduleExpense
import digdaserver.domain.ledger.domain.repository.ScheduleExpenseRepository
import digdaserver.domain.ledger.presentation.dto.req.ExpenseWriteRequest
import digdaserver.domain.membership.domain.repository.MembershipRepository
import digdaserver.domain.schedule.domain.entity.Schedule
import digdaserver.domain.user.domain.entity.User
import digdaserver.global.infra.exception.error.DigdaException
import digdaserver.global.infra.exception.error.ErrorCode
import org.springframework.stereotype.Component

/**
 * 일정 저장 흐름에서 지출 목록을 반영하는 헬퍼.
 *
 * 일정 도메인이 가계부 엔티티를 직접 다루지 않도록 쓰기 규칙(검증·전체 교체)을 여기에 모은다.
 * 호출자의 트랜잭션에 합류한다.
 */
@Component
class ScheduleExpenseWriter(
    private val expenseRepository: ScheduleExpenseRepository,
    private val membershipRepository: MembershipRepository
) {

    companion object {
        /** 일정 하나에 붙일 수 있는 지출 건수 상한. */
        const val MAX_EXPENSES_PER_SCHEDULE = 30

        /** 원 단위 상한 — 오타로 0 을 몇 개 더 찍었을 때 집계가 망가지는 걸 막는 선. */
        const val MAX_AMOUNT = 9_999_999_999L

        const val MAX_MEMO_LENGTH = 100
    }

    /**
     * 일정의 지출 목록을 [requests] 로 **전체 교체**한다.
     *
     * 부분 갱신(추가/수정/삭제 구분)이 아니라 통째로 갈아끼우는 이유는, 화면이 "일정 수정"
     * 한 번으로 항목 추가·삭제·순서를 모두 보내기 때문. 부분 갱신이면 클라이언트가 삭제된
     * 항목 id 를 따로 추적해야 하고 그게 곧 유실 버그가 된다.
     */
    fun replaceAll(
        schedule: Schedule,
        groupRoomId: Long,
        requests: List<ExpenseWriteRequest>,
        author: User
    ) {
        validate(requests)

        val existing = expenseRepository.findAllByScheduleId(schedule.id)
        if (existing.isNotEmpty()) expenseRepository.deleteAll(existing)

        if (requests.isEmpty()) {
            // 전부 지운 뒤 새로 넣을 게 없으면 여기서 끝. flush 는 트랜잭션 커밋에 맡긴다.
            return
        }

        val entities = requests.map { req ->
            ScheduleExpense(
                schedule = schedule,
                payer = resolvePayer(groupRoomId, req),
                amount = req.amount,
                category = req.category,
                memo = req.memo?.trim()?.takeIf { it.isNotEmpty() },
                createdBy = author
            )
        }
        expenseRepository.saveAll(entities)
    }

    /** 일정 복사 — 원본의 지출을 그대로 복제한다(낸 사람·분류·메모 유지). */
    fun copyAll(source: Schedule, target: Schedule, author: User) {
        val sourceExpenses = expenseRepository.findAllByScheduleId(source.id)
        if (sourceExpenses.isEmpty()) return
        expenseRepository.saveAll(
            sourceExpenses.map { e ->
                ScheduleExpense(
                    schedule = target,
                    payer = e.payer,
                    amount = e.amount,
                    category = e.category,
                    memo = e.memo,
                    createdBy = author
                )
            }
        )
    }

    /**
     * 일정에 지출 **한 건만** 덧붙인다. [replaceAll] 과 달리 기존 항목을 건드리지 않는다.
     *
     * 일정 상세에서 금액을 바로 저장하는 경로용. 그 화면은 목록 전체를 들고 있지 않아
     * 전체 교체를 시키면 아직 못 받아본 다른 멤버의 지출을 덮어 지운다.
     */
    fun append(
        schedule: Schedule,
        groupRoomId: Long,
        request: ExpenseWriteRequest,
        author: User
    ): ScheduleExpense {
        validateOne(request)
        if (expenseRepository.countByScheduleId(schedule.id) >= MAX_EXPENSES_PER_SCHEDULE) {
            throw DigdaException(ErrorCode.EXPENSE_LIMIT_EXCEEDED)
        }
        return expenseRepository.save(
            ScheduleExpense(
                schedule = schedule,
                payer = resolvePayer(groupRoomId, request),
                amount = request.amount,
                category = request.category,
                memo = request.memo?.trim()?.takeIf { it.isNotEmpty() },
                createdBy = author
            )
        )
    }

    private fun validate(requests: List<ExpenseWriteRequest>) {
        if (requests.size > MAX_EXPENSES_PER_SCHEDULE) {
            throw DigdaException(ErrorCode.EXPENSE_LIMIT_EXCEEDED)
        }
        requests.forEach { validateOne(it) }
    }

    private fun validateOne(req: ExpenseWriteRequest) {
        if (req.amount <= 0 || req.amount > MAX_AMOUNT) {
            throw DigdaException(ErrorCode.EXPENSE_AMOUNT_INVALID)
        }
        if ((req.memo?.trim()?.length ?: 0) > MAX_MEMO_LENGTH) {
            throw DigdaException(ErrorCode.EXPENSE_MEMO_TOO_LONG)
        }
    }

    /** 낸 사람은 반드시 해당 그룹의 멤버여야 한다. 미지정(null)은 허용. */
    private fun resolvePayer(groupRoomId: Long, req: ExpenseWriteRequest): User? {
        val payerId = req.payerId ?: return null
        return membershipRepository.findByGroupRoomIdAndUserId(groupRoomId, payerId)
            .orElseThrow { DigdaException(ErrorCode.INVALID_EXPENSE_PAYER) }
            .user
    }
}
