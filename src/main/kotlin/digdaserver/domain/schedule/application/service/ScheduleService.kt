package digdaserver.domain.schedule.application.service

import digdaserver.domain.schedule.presentation.dto.req.CreateScheduleReq
import digdaserver.domain.schedule.presentation.dto.req.UpdateScheduleReq
import digdaserver.domain.schedule.presentation.dto.res.ScheduleDetailRes
import digdaserver.domain.schedule.presentation.dto.res.ScheduleRes

interface ScheduleService {
    fun createSchedule(userId: String, groupId: Long, req: CreateScheduleReq): ScheduleRes
    fun getSchedules(userId: String, groupId: Long, month: String): List<ScheduleRes>
    fun getScheduleDetail(userId: String, groupId: Long, scheduleId: Long): ScheduleDetailRes
    fun updateSchedule(userId: String, groupId: Long, scheduleId: Long, req: UpdateScheduleReq): ScheduleRes
    fun deleteSchedule(userId: String, groupId: Long, scheduleId: Long)
}
