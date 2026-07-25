package digdaserver.domain.feedback.domain.entity

/**
 * 피드백 폼 문항 유형. 어드민이 문항을 편집하고 앱이 동적으로 렌더링한다.
 *
 * - [SECTION]     : 섹션 헤더(입력 없음). title/description 만 사용.
 * - [SHORT_TEXT]  : 한 줄 주관식.
 * - [PARAGRAPH]   : 여러 줄 주관식.
 * - [SINGLE_CHOICE]: 단일 선택(라디오). options = ["a","b",...].
 * - [SCALE]       : 척도(예: 1~5, 0~10). options = {"min":1,"max":5}.
 * - [GRID]        : 행×열 선택. options = {"rows":["..."],"cols":["만족","보통",...]}.
 */
enum class FeedbackQuestionType {
    SECTION,
    SHORT_TEXT,
    PARAGRAPH,
    SINGLE_CHOICE,
    SCALE,
    GRID
}
