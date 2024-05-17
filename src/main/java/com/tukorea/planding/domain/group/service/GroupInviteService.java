package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.dto.request.GroupInviteRequest;
import com.tukorea.planding.domain.group.dto.response.GroupInviteMessageResponse;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.entity.UserGroup;
import com.tukorea.planding.domain.group.service.query.GroupQueryService;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupInviteService {
    private final UserQueryService userQueryService;
    private final GroupQueryService groupQueryService;
    private final UserGroupService userGroupService;
    private final RedisGroupInviteService redisGroupInviteService;


    @Transactional
    public GroupInviteMessageResponse inviteGroupRoom(UserInfo userInfo, GroupInviteRequest groupInviteRequest) {
        // 초대하는 사용자와 초대 대상 사용자가 같은지 확인
        if (userInfo.getUserCode().equals(groupInviteRequest.getUserCode())) {
            throw new BusinessException(ErrorCode.CANNOT_INVITE_YOURSELF);
        }

        if (!groupQueryService.getGroupById(groupInviteRequest.getGroupId()).getOwner().equals(userInfo.getUserCode())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_GROUP_ROOM_INVITATION);
        }

        if (groupQueryService.existGroupInUser(groupInviteRequest.getUserCode(), groupInviteRequest.getGroupId())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_IN_GROUP);
        }

        GroupInviteMessageResponse groupInviteMessageResponse = GroupInviteMessageResponse.create("IN" + UUID.randomUUID(), groupInviteRequest.getGroupId(), groupInviteRequest.getUserCode(), userInfo.getId());

        redisGroupInviteService.createInvitation(groupInviteRequest.getUserCode(), groupInviteMessageResponse);

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

    public void declineInvitation(UserInfo userInfo, String inviteCode) {
        redisGroupInviteService.deleteInvitation(userInfo.getUserCode(), inviteCode);
    }
}