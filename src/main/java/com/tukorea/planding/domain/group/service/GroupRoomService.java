package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.dto.request.GroupCreateRequest;
import com.tukorea.planding.domain.group.dto.request.GroupUpdateRequest;
import com.tukorea.planding.domain.group.dto.response.GroupResponse;
import com.tukorea.planding.domain.group.dto.response.GroupUserResponse;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.entity.UserGroup;
import com.tukorea.planding.domain.group.repository.normal.GroupRoomRepository;
import com.tukorea.planding.domain.group.service.query.GroupQueryService;
import com.tukorea.planding.domain.group.service.query.UserGroupQueryService;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupRoomService {

    private final UserQueryService userQueryService;
    private final UserGroupQueryService userGroupQueryService;
    private final GroupQueryService groupQueryService;

    @Transactional
    public GroupResponse createGroupRoom(UserInfo userInfo, GroupCreateRequest createGroupRoom) {
        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());

        GroupRoom newGroupRoom = GroupRoom.createGroupRoom(createGroupRoom, user);
        GroupRoom savedGroupRoom = groupQueryService.createGroup(newGroupRoom);

        final UserGroup userGroup = UserGroup.createUserGroup(user, savedGroupRoom);

        // 중간테이블에 유저, 그룹 정보 저장
        userGroupQueryService.save(userGroup);

        return toGroupResponse(newGroupRoom);
    }

    @Transactional
    public GroupResponse updateGroupNameOrDescription(UserInfo userInfo, GroupUpdateRequest groupUpdateRequest) {
        GroupRoom groupRoom = groupQueryService.getGroupById(groupUpdateRequest.groupId());

        // TODO 그룹의 팀원도 변경가능하도록
        if (!groupRoom.getOwner().equals(userInfo.getUserCode())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        groupRoom.updateNameOrDes(groupUpdateRequest.name(), groupUpdateRequest.description());

        return toGroupResponse(groupRoom);
    }

    public void deleteGroup(UserInfo userInfo, Long groupId) {
        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());
        GroupRoom groupRoom = groupQueryService.getGroupById(groupId);

        if (!groupRoom.getOwner().equals(user.getUserCode())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        groupQueryService.delete(groupRoom);
    }

    // 유저가 속한 그룹룸 가져오기
    public List<GroupResponse> getAllGroupRoomByUser(UserInfo userInfo) {
        List<GroupRoom> groupRooms = groupQueryService.findGroupsByUserId(userInfo.getId());
        return groupRooms.stream()
                .map(this::toGroupResponse)
                .collect(Collectors.toList());
    }

    public List<GroupUserResponse> getGroupUsers(Long groupId) {
        List<User> users = groupQueryService.getGroupUsers(groupId);
        return users.stream()
                .map(GroupUserResponse::toGroupUserResponse)
                .collect(Collectors.toList());
    }

    private GroupResponse toGroupResponse(GroupRoom groupRoom) {
        return GroupResponse.from(groupRoom);
    }
}
