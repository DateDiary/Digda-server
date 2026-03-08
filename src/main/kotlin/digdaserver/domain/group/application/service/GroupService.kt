package digdaserver.domain.group.application.service

import digdaserver.domain.group.presentation.dto.req.CreateGroupReq
import digdaserver.domain.group.presentation.dto.req.JoinGroupReq
import digdaserver.domain.group.presentation.dto.req.UpdateGroupReq
import digdaserver.domain.group.presentation.dto.res.GroupDetailRes
import digdaserver.domain.group.presentation.dto.res.GroupMemberRes
import digdaserver.domain.group.presentation.dto.res.GroupRes
import digdaserver.domain.group.presentation.dto.res.GroupSummaryRes

interface GroupService {
    fun createGroup(userId: String, req: CreateGroupReq): GroupRes
    fun getMyGroups(userId: String): List<GroupSummaryRes>
    fun getGroupDetail(userId: String, groupId: Long): GroupDetailRes
    fun updateGroup(userId: String, groupId: Long, req: UpdateGroupReq): GroupRes
    fun deleteGroup(userId: String, groupId: Long)
    fun joinGroup(userId: String, req: JoinGroupReq): GroupRes
    fun leaveGroup(userId: String, groupId: Long)
    fun getGroupMembers(groupId: Long): List<GroupMemberRes>
}
