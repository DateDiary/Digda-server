package digdaserver.domain.ledger.presentation.dto.req

import digdaserver.domain.ledger.domain.entity.ExpenseCategory
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

/**
 * 일정 생성/수정에 실려 오는 지출 한 건.
 *
 * 가계부에는 별도 엔드포인트를 두지 않고 일정 저장에 함께 태운다. 화면에서 일정과
 * 금액을 한 번에 저장하기 때문에, 따로 두면 "일정은 저장됐는데 금액만 실패" 같은
 * 반쪽 저장이 생긴다.
 */
@Schema(description = "일정 지출 항목")
data class ExpenseWriteRequest(

    @Schema(description = "원(KRW) 단위 금액", example = "120000")
    val amount: Long,

    @Schema(description = "지출 분류", example = "LODGING")
    val category: ExpenseCategory = ExpenseCategory.ETC,

    @Schema(description = "돈을 낸 그룹 멤버. 생략하면 '누가 냈는지 미지정'으로 저장된다.")
    val payerId: UUID? = null,

    @Schema(description = "내용 메모 (최대 100자)", example = "숙소값")
    val memo: String? = null
)
