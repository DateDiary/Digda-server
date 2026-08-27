package digdaserver.domain.ledger.presentation.dto.res

import digdaserver.domain.ledger.domain.entity.ExpenseCategory
import digdaserver.domain.schedule.presentation.dto.res.UserSummary
import java.time.LocalDate

/**
 * 그룹 가계부 월 요약 — 전체 가계부 화면 한 장을 그리는 데 필요한 값 전부.
 *
 * 비율(ratio)은 0.0~1.0 실수로 서버가 계산해 내려준다. 앱이 나눗셈을 하면 총액 0 일 때
 * NaN 이 되어 그래프가 깨지므로, 0 나눗셈 방어를 한 곳에서만 하기 위함.
 */
data class GroupLedgerResponse(
    val year: Int,
    val month: Int,
    /** 이 달 총 지출. */
    val totalAmount: Long,
    /** 지난달 총 지출 — 증감 표시용. */
    val prevMonthTotal: Long,
    /** 그룹이 지금까지 쓴 전체 누적 지출. */
    val allTimeTotal: Long,
    /** 이 달 지출 건수. */
    val entryCount: Int,
    val categories: List<LedgerCategoryStat>,
    val members: List<LedgerMemberStat>,
    /** 이 달 지출이 있는 일정 — 금액 내림차순. */
    val schedules: List<LedgerScheduleStat>,
    /** 이 달 날짜별 합계 — 지출이 있는 날만. 막대 그래프용. */
    val daily: List<LedgerDailyStat>,
    /**
     * 가계부에 기록이 남아 있는 첫 달 (`yyyy-MM`). 지출이 하나도 없으면 null.
     *
     * 앱의 월 이동 범위를 이 값으로 잡는다. "미래는 못 본다" 같은 규칙을 앱에 박아두면
     * 다음 달 여행비를 미리 적어둔 그룹이 정작 자기가 쓴 달을 보지 못한다.
     * 오늘이 낀 달까지 범위에 넣을지는 앱이 정한다 — 기기 시각 기준이라 서버가 단정하지 않는다.
     */
    val firstEntryMonth: String?,
    /** 가계부에 기록이 남아 있는 마지막 달 (`yyyy-MM`). 지출이 하나도 없으면 null. */
    val lastEntryMonth: String?
)

data class LedgerCategoryStat(
    val category: ExpenseCategory,
    val label: String,
    val amount: Long,
    val ratio: Double
)

data class LedgerMemberStat(
    /** 탈퇴한 멤버가 낸 지출이면 null — 앱은 '탈퇴한 멤버'로 표시한다. */
    val payer: UserSummary?,
    val amount: Long,
    val ratio: Double
)

data class LedgerScheduleStat(
    val scheduleId: Long,
    val title: String,
    val color: String,
    val startDate: LocalDate,
    val amount: Long,
    val entryCount: Int
)

data class LedgerDailyStat(
    val date: LocalDate,
    val amount: Long
)
