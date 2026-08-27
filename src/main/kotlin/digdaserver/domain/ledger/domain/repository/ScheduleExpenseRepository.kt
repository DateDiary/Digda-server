package digdaserver.domain.ledger.domain.repository

import digdaserver.domain.ledger.domain.entity.ScheduleExpense
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface ScheduleExpenseRepository : JpaRepository<ScheduleExpense, Long> {

    /** 일정 목록 화면용 — 여러 일정의 지출을 한 번에 가져와 N+1 을 막는다. */
    @Query("SELECT e FROM ScheduleExpense e WHERE e.schedule.id IN :scheduleIds ORDER BY e.id ASC")
    fun findAllByScheduleIdIn(@Param("scheduleIds") scheduleIds: Collection<Long>): List<ScheduleExpense>

    @Query("SELECT e FROM ScheduleExpense e WHERE e.schedule.id = :scheduleId ORDER BY e.id ASC")
    fun findAllByScheduleId(@Param("scheduleId") scheduleId: Long): List<ScheduleExpense>

    /** 단건 추가 시 건수 상한 검사용 — 목록을 통째로 읽지 않고 개수만 센다. */
    @Query("SELECT COUNT(e) FROM ScheduleExpense e WHERE e.schedule.id = :scheduleId")
    fun countByScheduleId(@Param("scheduleId") scheduleId: Long): Long

    /**
     * 그룹 가계부 집계 원천 — 일정 시작일이 [from]~[to] 인 지출 전부.
     * 날짜 기준은 일정의 시작일이라, 기간 일정이면 시작한 달에 한 번만 잡힌다.
     */
    @Query(
        """
        SELECT e FROM ScheduleExpense e
        JOIN e.schedule s
        WHERE s.groupRoom.id = :groupRoomId AND s.startDate BETWEEN :from AND :to
        """
    )
    fun findAllByGroupRoomIdAndPeriod(
        @Param("groupRoomId") groupRoomId: Long,
        @Param("from") from: LocalDate,
        @Param("to") to: LocalDate
    ): List<ScheduleExpense>

    /**
     * 가계부에 기록이 있는 가장 이른/늦은 날짜 — 월 이동 범위의 양 끝을 잡는 데 쓴다.
     * 지출이 하나도 없으면 NULL. 날짜 기준은 집계와 같은 일정 시작일이라, 화면에서
     * 이동할 수 있는 달과 실제로 숫자가 뜨는 달이 어긋나지 않는다.
     */
    @Query(
        """
        SELECT MIN(s.startDate) FROM ScheduleExpense e
        JOIN e.schedule s
        WHERE s.groupRoom.id = :groupRoomId
        """
    )
    fun findFirstEntryDate(@Param("groupRoomId") groupRoomId: Long): LocalDate?

    @Query(
        """
        SELECT MAX(s.startDate) FROM ScheduleExpense e
        JOIN e.schedule s
        WHERE s.groupRoom.id = :groupRoomId
        """
    )
    fun findLastEntryDate(@Param("groupRoomId") groupRoomId: Long): LocalDate?

    /** 그룹 누적 지출 합계. 지출이 하나도 없으면 NULL 이 나오므로 호출부에서 0 처리. */
    @Query(
        """
        SELECT SUM(e.amount) FROM ScheduleExpense e
        JOIN e.schedule s
        WHERE s.groupRoom.id = :groupRoomId
        """
    )
    fun sumAmountByGroupRoomId(@Param("groupRoomId") groupRoomId: Long): Long?

    /**
     * 회원탈퇴 정리 — 탈퇴자가 만든 일정에 달린 지출은 일정 bulk 삭제 전에 먼저 지운다.
     * (bulk JPQL delete 는 cascade 를 타지 않아 FK 위반이 난다)
     */
    @Modifying
    @Query(
        "DELETE FROM ScheduleExpense e " +
            "WHERE e.schedule.id IN (SELECT s.id FROM Schedule s WHERE s.createdBy.id = :userId)"
    )
    fun deleteAllByScheduleCreatedById(@Param("userId") userId: UUID): Int

    /** 회원탈퇴 정리 — 남는 지출 기록에서 사람만 비운다(금액은 그룹 가계부에 보존). */
    @Modifying
    @Query("UPDATE ScheduleExpense e SET e.payer = null WHERE e.payer.id = :userId")
    fun detachPayer(@Param("userId") userId: UUID): Int

    /** 회원탈퇴 정리 — 작성자 참조도 함께 끊는다(user FK 제약 해소). */
    @Modifying
    @Query("UPDATE ScheduleExpense e SET e.createdBy = null WHERE e.createdBy.id = :userId")
    fun detachCreatedBy(@Param("userId") userId: UUID): Int
}
