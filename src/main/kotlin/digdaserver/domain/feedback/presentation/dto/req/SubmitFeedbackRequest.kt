package digdaserver.domain.feedback.presentation.dto.req

/**
 * 앱 피드백 제출. [answers] 는 화면에 렌더된 문항 순서의 응답 목록.
 * answer 는 표시용 문자열(척도=숫자, 그리드="행: 열, ...") 로 앱에서 가공해 보낸다.
 */
data class SubmitFeedbackRequest(
    val answers: List<FeedbackAnswerItem> = emptyList()
) {
    data class FeedbackAnswerItem(
        val questionId: Long,
        val answer: String
    )
}
