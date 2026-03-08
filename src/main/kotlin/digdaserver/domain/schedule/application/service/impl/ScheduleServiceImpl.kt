package digdaserver.domain.schedule.application.service.impl

import digdaserver.domain.group.domain.repository.GroupMemberRepository
import digdaserver.domain.group.domain.repository.GroupRepository
import digdaserver.domain.schedule.application.service.ScheduleService
import digdaserver.domain.schedule.domain.entity.Schedule
import digdaserver.domain.schedule.domain.repository.ScheduleRepository
import digdaserver.domain.schedule.presentation.dto.req.CreateScheduleReq
import digdaserver.domain.schedule.presentation.dto.req.UpdateScheduleReq
import digdaserver.domain.schedule.presentation.dto.res.ScheduleDetailRes
import digdaserver.domain.schedule.presentation.dto.res.ScheduleRes
import digdaserver.global.infra.exception.error.DigdaServerException
import digdaserver.global.infra.exception.error.ErrorCode
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Service
@Transactional
class ScheduleServiceImpl(
    private val scheduleRepository: ScheduleRepository,
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository
) : ScheduleService {

    override fun createSchedule(userId: String, groupId: Long, req: CreateScheduleReq): ScheduleRes {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        if (req.endAt.isBefore(req.startAt)) {
            throw DigdaServerException(ErrorCode.INVALID_PARAMETER)
        }

        val schedule = scheduleRepository.save(
            Schedule(
                group = group,
                authorId = userId,
                title = req.title,
                content = req.content,
                startAt = req.startAt,
                endAt = req.endAt
            )
        )

        return ScheduleRes.of(schedule)
    }

    override fun getSchedules(userId: String, groupId: Long, month: String): List<ScheduleRes> {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"))
        val startOfMonth: LocalDateTime = yearMonth.atDay(1).atStartOfDay()
        val endOfMonth: LocalDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59)

        return scheduleRepository.findAllByGroupAndStartAtBetween(group, startOfMonth, endOfMonth)
            .map { ScheduleRes.of(it) }
    }

    override fun getScheduleDetail(userId: String, groupId: Long, scheduleId: Long): ScheduleDetailRes {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        return ScheduleDetailRes.of(schedule)
    }

    override fun updateSchedule(userId: String, groupId: Long, scheduleId: Long, req: UpdateScheduleReq): ScheduleRes {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (schedule.authorId != userId) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val effectiveStartAt = req.startAt ?: schedule.startAt
        val effectiveEndAt = req.endAt ?: schedule.endAt

        if (effectiveEndAt.isBefore(effectiveStartAt)) {
            throw DigdaServerException(ErrorCode.INVALID_PARAMETER)
        }

        schedule.update(
            title = req.title,
            content = req.content,
            startAt = req.startAt,
            endAt = req.endAt
        )

        return ScheduleRes.of(schedule)
    }

    override fun deleteSchedule(userId: String, groupId: Long, scheduleId: Long) {
        val group = groupRepository.findById(groupId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (!groupMemberRepository.existsByGroupAndMemberId(group, userId)) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        val schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow { DigdaServerException(ErrorCode.OBJECT_NOT_FOUND) }

        if (schedule.authorId != userId) {
            throw DigdaServerException(ErrorCode.FORBIDDEN)
        }

        scheduleRepository.delete(schedule)
    }
}
