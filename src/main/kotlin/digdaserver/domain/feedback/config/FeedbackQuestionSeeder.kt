package digdaserver.domain.feedback.config

import com.fasterxml.jackson.databind.ObjectMapper
import digdaserver.domain.feedback.domain.entity.FeedbackQuestion
import digdaserver.domain.feedback.domain.entity.FeedbackQuestionType
import digdaserver.domain.feedback.domain.repository.FeedbackQuestionRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 피드백 문항이 하나도 없으면 기본 문항(초기 구글폼 "디그팟 사용 피드백 💬" 이관)을 1회 시드한다.
 * 이후에는 어드민이 편집하므로 이미 문항이 있으면 스킵 — 멱등.
 *
 * ApplicationReadyEvent 는 SchemaAutoMigration(ApplicationRunner) 이후에 발생하므로
 * 시드 시점에 feedback_question 테이블이 존재함을 보장한다.
 */
@Component
@Profile("dev", "prod")
class FeedbackQuestionSeeder(
    private val feedbackQuestionRepository: FeedbackQuestionRepository,
    private val objectMapper: ObjectMapper
) : ApplicationListener<ApplicationReadyEvent> {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        if (feedbackQuestionRepository.count() > 0) return

        var order = 0
        val q = mutableListOf<FeedbackQuestion>()
        fun add(
            type: FeedbackQuestionType,
            title: String,
            required: Boolean = false,
            options: Any? = null,
            description: String? = null
        ) {
            q.add(
                FeedbackQuestion(
                    displayOrder = order++,
                    type = type,
                    title = title,
                    description = description,
                    required = required,
                    options = options?.let { objectMapper.writeValueAsString(it) },
                    active = true
                )
            )
        }

        add(FeedbackQuestionType.SECTION, "1. 기본 정보")
        add(
            FeedbackQuestionType.SINGLE_CHOICE,
            "얼마나 자주 디그팟을 사용하세요?",
            required = true,
            options = listOf("매일", "주 2~3회", "가끔", "거의 안 씀")
        )
        add(
            FeedbackQuestionType.SINGLE_CHOICE,
            "누구와 함께 쓰고 있나요?",
            required = true,
            options = listOf("친구", "연인", "가족", "동아리·모임", "혼자")
        )

        add(FeedbackQuestionType.SECTION, "2. 만족도")
        add(
            FeedbackQuestionType.SCALE,
            "디그팟 전반적인 만족도는?",
            required = true,
            options = mapOf("min" to 1, "max" to 5)
        )
        add(
            FeedbackQuestionType.SCALE,
            "다른 사람에게 추천하고 싶나요?",
            required = true,
            options = mapOf("min" to 0, "max" to 10)
        )

        add(FeedbackQuestionType.SECTION, "3. 기능별 평가")
        add(
            FeedbackQuestionType.GRID,
            "각 기능은 얼마나 만족스러운가요?",
            required = true,
            options = mapOf(
                "rows" to listOf("그림 일기 작성", "일정·캘린더", "시그니처 지도(지역 색칠)", "칭호/정복 시스템", "알림"),
                "cols" to listOf("만족", "보통", "불만족", "안 써봄")
            )
        )

        add(FeedbackQuestionType.SECTION, "4. 자세한 의견")
        add(FeedbackQuestionType.PARAGRAPH, "가장 마음에 드는 기능이나 점은?")
        add(FeedbackQuestionType.PARAGRAPH, "불편하거나 개선됐으면 하는 점은?", required = true)
        add(FeedbackQuestionType.PARAGRAPH, "있었으면 하는 새로운 기능이 있나요?")
        add(FeedbackQuestionType.PARAGRAPH, "버그나 오류를 겪은 적 있나요? (있다면 상황을 적어주세요)")

        add(FeedbackQuestionType.SECTION, "5. 마무리")
        add(FeedbackQuestionType.PARAGRAPH, "추가로 하고 싶은 말이 있다면 자유롭게 적어주세요")
        add(FeedbackQuestionType.SHORT_TEXT, "업데이트 소식이나 추가 인터뷰 안내를 받아볼 이메일 (선택)")

        feedbackQuestionRepository.saveAll(q)
        log.info("[FeedbackQuestionSeeder] 기본 피드백 문항 {}건 시드 완료", q.size)
    }
}
