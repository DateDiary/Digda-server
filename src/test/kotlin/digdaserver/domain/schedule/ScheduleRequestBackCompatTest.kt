package digdaserver.domain.schedule

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import digdaserver.domain.ledger.domain.entity.ExpenseCategory
import digdaserver.domain.schedule.presentation.dto.req.CreateScheduleRequest
import digdaserver.domain.schedule.presentation.dto.req.UpdateScheduleRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * 구버전 앱(2.2.1 이하) 호환 — 가계부가 붙기 전 앱이 보내는 요청 본문이
 * 새 서버에서 그대로 동작해야 한다.
 *
 * 특히 [UpdateScheduleRequest.expenses] 는 `null`(미전송)과 빈 배열(전부 삭제)의
 * 의미가 다르다. 구버전 앱은 이 필드를 아예 보내지 않으므로 반드시 null 로 읽혀야
 * 하며, 그래야 "구버전 앱으로 일정 제목만 고쳤는데 가계부가 날아가는" 사고가 안 난다.
 */
class ScheduleRequestBackCompatTest {

    private val mapper: ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())

    @Nested
    @DisplayName("구버전 앱 요청 (expenses 필드 없음)")
    inner class LegacyClient {

        /** 2.2.1 앱의 ScheduleWriteRequest.toJson() 이 만들던 본문 그대로. */
        private val legacyCreateJson = """
            {
              "title": "부산 여행",
              "color": "#FF6B6B",
              "startDate": "2026-08-09",
              "endDate": "2026-08-11",
              "allDay": true,
              "participantIds": ["11111111-1111-1111-1111-111111111111"]
            }
        """.trimIndent()

        private val legacyUpdateJson = """
            {
              "title": "부산 여행 (수정)",
              "startDate": "2026-08-09",
              "endDate": "2026-08-11",
              "allDay": true
            }
        """.trimIndent()

        @Test
        @DisplayName("일정 생성 — 파싱되고 expenses 는 null")
        fun createParsesWithNullExpenses() {
            val req = mapper.readValue(legacyCreateJson, CreateScheduleRequest::class.java)

            assertThat(req.title).isEqualTo("부산 여행")
            assertThat(req.allDay).isTrue()
            assertThat(req.participantIds).hasSize(1)
            // 서비스가 `request.expenses ?: emptyList()` 로 받아 지출 없이 생성한다.
            assertThat(req.expenses).isNull()
        }

        @Test
        @DisplayName("일정 수정 — expenses 가 null 이라 기존 지출을 건드리지 않는다")
        fun updateKeepsExpensesUntouched() {
            val req = mapper.readValue(legacyUpdateJson, UpdateScheduleRequest::class.java)

            assertThat(req.title).isEqualTo("부산 여행 (수정)")
            // 핵심: null 이어야 ScheduleServiceImpl 의 `request.expenses?.let { ... }`
            // 블록이 통째로 건너뛰어져 가계부가 보존된다.
            assertThat(req.expenses).isNull()
        }

        @Test
        @DisplayName("시간 지정 일정 — startTime/endTime 도 그대로 파싱된다")
        fun timedScheduleStillParses() {
            val json = """
                {
                  "title": "저녁 약속",
                  "color": "#60A5FA",
                  "startDate": "2026-08-04",
                  "endDate": "2026-08-04",
                  "startTime": "18:30",
                  "endTime": "21:00",
                  "allDay": false
                }
            """.trimIndent()

            val req = mapper.readValue(json, CreateScheduleRequest::class.java)

            assertThat(req.allDay).isFalse()
            assertThat(req.startTime.toString()).isEqualTo("18:30")
            assertThat(req.expenses).isNull()
        }
    }

    @Nested
    @DisplayName("신버전 앱 요청 (expenses 포함)")
    inner class CurrentClient {

        @Test
        @DisplayName("빈 배열은 null 과 구분된다 — 전부 삭제 의도")
        fun emptyArrayIsNotNull() {
            val json = """{"title":"제목만 수정","expenses":[]}"""

            val req = mapper.readValue(json, UpdateScheduleRequest::class.java)

            assertThat(req.expenses).isNotNull()
            assertThat(req.expenses).isEmpty()
        }

        @Test
        @DisplayName("지출 항목이 필드까지 정확히 파싱된다")
        fun expensesParse() {
            val json = """
                {
                  "title": "부산 여행",
                  "expenses": [
                    {
                      "amount": 280000,
                      "category": "LODGING",
                      "payerId": "11111111-1111-1111-1111-111111111111",
                      "memo": "해운대 숙소"
                    },
                    { "amount": 30000 }
                  ]
                }
            """.trimIndent()

            val req = mapper.readValue(json, UpdateScheduleRequest::class.java)
            val expenses = req.expenses!!

            assertThat(expenses).hasSize(2)
            assertThat(expenses[0].amount).isEqualTo(280_000L)
            assertThat(expenses[0].category).isEqualTo(ExpenseCategory.LODGING)
            assertThat(expenses[0].memo).isEqualTo("해운대 숙소")
            // category/payerId/memo 는 선택 — 금액만 보내도 기본값으로 채워진다.
            assertThat(expenses[1].category).isEqualTo(ExpenseCategory.ETC)
            assertThat(expenses[1].payerId).isNull()
            assertThat(expenses[1].memo).isNull()
        }
    }
}
