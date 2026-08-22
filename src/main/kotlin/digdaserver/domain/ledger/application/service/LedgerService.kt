package digdaserver.domain.ledger.application.service

import digdaserver.domain.ledger.presentation.dto.res.GroupLedgerResponse
import java.util.UUID

interface LedgerService {

    /** 그룹 가계부 월 요약 — 전체 가계부 화면 한 장에 필요한 집계 전부. */
    fun getMonthlyLedger(userId: UUID, groupRoomId: Long, year: Int, month: Int): GroupLedgerResponse
}
