package com.tukorea.planding.domain.invite;

import com.tukorea.planding.domain.group.dto.request.GroupInviteRequest;
import com.tukorea.planding.domain.group.dto.response.GroupInviteResponse;
import com.tukorea.planding.domain.group.entity.GroupInvite;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.entity.InviteStatus;
import com.tukorea.planding.domain.group.entity.UserGroup;
import com.tukorea.planding.domain.group.repository.invite.GroupInviteRepository;
import com.tukorea.planding.domain.group.service.UserGroupService;
import com.tukorea.planding.domain.group.service.query.GroupInviteQueryService;
import com.tukorea.planding.domain.group.service.query.GroupQueryService;
import com.tukorea.planding.domain.notify.service.NotificationService;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupInviteService2 {
    private final UserQueryService userQueryService;
    private final GroupInviteRepository groupInviteRepository;
    private final GroupQueryService groupQueryService;
    private final GroupInviteQueryService groupInviteQueryService;
    private final UserGroupService userGroupService;
    private final NotificationService notificationService;
    private final RedisGroupInviteService redisGroupInviteService;


    @Transactional
    public GroupInviteDTO inviteGroupRoom(UserInfo userInfo, GroupInviteRequest groupInviteRequest) {
        // 초대하는 사용자와 초대 대상 사용자가 같은지 확인
        if (userInfo.getUserCode().equals(groupInviteRequest.getUserCode())) {
            throw new BusinessException(ErrorCode.CANNOT_INVITE_YOURSELF);
        }

        GroupInviteDTO groupInviteDTO = new GroupInviteDTO();
        groupInviteDTO.setGroupRoomId(groupInviteRequest.getGroupId());
        groupInviteDTO.setInvitingUserId(userInfo.getId());
        groupInviteDTO.setInvitedUserCode(groupInviteRequest.getUserCode());
        groupInviteDTO.setCreatedAt(LocalDateTime.now());
        groupInviteDTO.setExpiredAt(LocalDateTime.now().plusDays(1));
        groupInviteDTO.setInviteCode("IN" + LocalDateTime.now());

        redisGroupInviteService.createInvitation(groupInviteRequest.getUserCode(), groupInviteDTO);

        return groupInviteDTO;
    }

    @Transactional
    public void acceptInvitation(UserInfo userInfo, String code, Long groupId) {
        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());
        GroupRoom group = groupQueryService.getGroupById(groupId);


        final UserGroup userGroup = UserGroup.createUserGroup(user, group);
        userGroupService.save(userGroup);

        redisGroupInviteService.deleteInvitation(userInfo.getUserCode(), code);
    }

    public List<GroupInviteDTO> getInvitations(UserInfo userInfo) {
        return redisGroupInviteService.getAllInvitations(userInfo.getUserCode());
    }

}
