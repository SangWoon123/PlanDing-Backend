package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.dto.GroupCreateRequest;
import com.tukorea.planding.domain.group.dto.GroupResponse;
import com.tukorea.planding.domain.group.dto.GroupUpdateRequest;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.entity.UserGroup;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupRoomService {

    private final UserQueryService userQueryService;
    private final UserGroupService userGroupService;
    private final GroupQueryService groupQueryService;

    @Transactional
    public GroupResponse createGroupRoom(UserInfo userInfo, GroupCreateRequest createGroupRoom) {
        User user = userQueryService.getUserByUserCode(userInfo);

        GroupRoom newGroupRoom = GroupRoom.createGroupRoom(createGroupRoom, user);
        GroupRoom savedGroupRoom = groupQueryService.createGroup(newGroupRoom);

        final UserGroup userGroup = UserGroup.createUserGroup(user, savedGroupRoom);

        // 중간테이블에 유저, 그룹 정보 저장
        userGroupService.save(userGroup);

        return toGroupResponse(newGroupRoom);
    }

    @Transactional
    public GroupResponse updateGroupNameOrDescription(UserInfo userInfo, GroupUpdateRequest groupUpdateRequest) {
        User user = userQueryService.getUserByUserCode(userInfo);

        GroupRoom groupRoom = groupQueryService.getGroupByCode(groupUpdateRequest.groupCode());

        // TODO 그룹의 팀원도 변경가능하도록
        if (!groupRoom.getOwner().equals(user.getUserCode())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        groupRoom.updateNameOrDes(groupUpdateRequest.name(), groupUpdateRequest.description());

        return toGroupResponse(groupRoom);
    }

    // 유저가 속한 그룹룸 가져오기
    public List<GroupResponse> getAllGroupRoomByUser(UserInfo userInfo) {
        User user = userQueryService.getUserByUserCode(userInfo);

        List<GroupRoom> groupRooms = groupQueryService.findGroupsByUser(user);

        return groupRooms.stream()
                .map(this::toGroupResponse)
                .collect(Collectors.toList());
    }

    private GroupResponse toGroupResponse(GroupRoom groupRoom) {
        return GroupResponse.from(groupRoom);
    }

}
