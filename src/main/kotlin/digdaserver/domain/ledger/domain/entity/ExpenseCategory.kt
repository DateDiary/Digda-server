package digdaserver.domain.ledger.domain.entity

/**
 * 그룹 가계부 지출 분류.
 *
 * 앱이 라벨을 하드코딩하지 않도록 [label] 을 응답에 함께 내려준다. 값이 추가돼도
 * 컬럼은 VARCHAR(32) 라 스키마 변경이 필요 없다(= notification.type 과 같은 방침).
 */
enum class ExpenseCategory(val label: String) {
    FOOD("식비"),
    TRANSPORT("교통"),
    LODGING("숙박"),
    SHOPPING("쇼핑"),
    ETC("기타")
}
