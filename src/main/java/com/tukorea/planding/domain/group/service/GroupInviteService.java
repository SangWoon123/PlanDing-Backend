package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.dto.request.GroupInviteRequest;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.entity.UserGroup;
import com.tukorea.planding.domain.group.service.query.GroupQueryService;
import com.tukorea.planding.domain.group.dto.response.GroupInviteMessageResponse;
import com.tukorea.planding.domain.notify.service.NotificationService;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupInviteService {
    private final UserQueryService userQueryService;
    private final GroupQueryService groupQueryService;
    private final UserGroupService userGroupService;
    private final NotificationService notificationService;
    private final RedisGroupInviteService redisGroupInviteService;


    @Transactional
    public GroupInviteMessageResponse inviteGroupRoom(UserInfo userInfo, GroupInviteRequest groupInviteRequest) {
        // 초대하는 사용자와 초대 대상 사용자가 같은지 확인
        if (userInfo.getUserCode().equals(groupInviteRequest.getUserCode())) {
            throw new BusinessException(ErrorCode.CANNOT_INVITE_YOURSELF);
        }

        // 그룹이 존재하는지
        if (!groupQueryService.existById(groupInviteRequest.getGroupId())) {
            throw new BusinessException(ErrorCode.GROUP_ROOM_NOT_FOUND);
        }

        //그룹에 속해있는지
        // 사용자가 이미 그룹에 속해 있는지 확인
        if (groupQueryService.existGroupInUser(groupInviteRequest.getUserCode(), groupInviteRequest.getGroupId())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_IN_GROUP);
        }

        GroupRoom groupRoom = groupQueryService.getGroupById(groupInviteRequest.getGroupId());


        GroupInviteMessageResponse groupInviteMessageResponse = GroupInviteMessageResponse.create(
                "INV" + UUID.randomUUID()
                , groupInviteRequest.getGroupId()
                , groupInviteRequest.getUserCode()
                , userInfo.getId());


        redisGroupInviteService.createInvitation(groupInviteRequest.getUserCode(), groupInviteMessageResponse);

        notificationService.notifyInvitation(groupInviteRequest.getUserCode(), groupRoom.getName());

        return groupInviteMessageResponse;
    }

    @Transactional
    public void acceptInvitation(UserInfo userInfo, String code, Long groupId) {
        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());
        GroupRoom group = groupQueryService.getGroupById(groupId);

        final UserGroup userGroup = UserGroup.createUserGroup(user, group);
        userGroupService.save(userGroup);

        redisGroupInviteService.deleteInvitation(userInfo.getUserCode(), code);
    }

    public List<GroupInviteMessageResponse> getInvitations(UserInfo userInfo) {
        return redisGroupInviteService.getAllInvitations(userInfo.getUserCode());
    }

    @Transactional
    public void declineInvitation(UserInfo userInfo, String code) {
        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());
        redisGroupInviteService.deleteInvitation(userInfo.getUserCode(), code);
    }

}
