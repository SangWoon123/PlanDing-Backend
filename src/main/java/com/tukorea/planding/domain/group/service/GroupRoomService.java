package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.dto.GroupCreateRequest;
import com.tukorea.planding.domain.group.dto.GroupInviteRequest;
import com.tukorea.planding.domain.group.dto.GroupResponse;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.repository.GroupRoomRepository;
import com.tukorea.planding.domain.group.repository.GroupRoomRepositoryCustomImpl;
import com.tukorea.planding.domain.group.repository.UserGroupMembershipRepository;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final UserGroupMembershipRepository userGroupMembershipRepository;
    private final GroupRoomRepository groupRoomRepository;
    private final GroupRoomRepositoryCustomImpl groupRoomRepositoryCustom;

    @Transactional
    public GroupResponse createGroupRoom(UserInfo userInfo, GroupCreateRequest createGroupRoom) {
        User user = userRepository.findByUserCode(userInfo.getUserCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        GroupRoom newGroupRoom = GroupRoom.builder()
                .name(createGroupRoom.name())
                .owner(user.getUserCode())
                .build();

        newGroupRoom.addUser(user);
        GroupRoom savedGroupRoom = groupRoomRepository.save(newGroupRoom);

        // 중간테이블에 유저, 그룹 정보 저장
        userGroupMembershipRepository.saveAll(newGroupRoom.getGroupMemberships());

        return GroupResponse.from(savedGroupRoom);
    }

    @Transactional
    public GroupResponse handleInvitation(UserInfo userInfo, GroupInviteRequest invitedUserInfo) {
        // 초대하는 유저가 존재하는지 체크하는 로직
        User invitingUser = userRepository.findByUserCode(userInfo.getUserCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        GroupRoom groupRoom = groupRoomRepository.findByGroupCode(invitedUserInfo.getInviteGroupCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_ROOM_NOT_FOUND));

        // 초대하는 유저가 방장인지 체크하는 로직
        validInvitePermission(groupRoom, invitingUser);

        User invitedUser = userRepository.findByUserCode(invitedUserInfo.getUserCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 초대한 유저가 이미 그룹에 속해 있는지 확인
        if (groupRoom.getGroupMemberships().contains(invitedUser)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_INVITED);
        }

        groupRoom.addUser(invitedUser);



        // 중간테이블에 유저, 그룹 정보 저장
        userGroupMembershipRepository.saveAll(groupRoom.getGroupMemberships());

        return GroupResponse.from(groupRoom);
    }

    // 유저가 속한 그룹룸 가져오기
    public List<GroupResponse> getAllGroupRoomByUser(UserInfo userInfo) {
        User user = userRepository.findByUserCode(userInfo.getUserCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<GroupRoom> groupRooms = groupRoomRepositoryCustom.findGroupRoomsByUserId(user.getId());

        return groupRooms.stream()
                .map(GroupResponse::from)
                .collect(Collectors.toList());
    }

    private void validInvitePermission(GroupRoom groupRoom, User invitingUser) {
        if (!groupRoom.getOwner().equals(invitingUser.getUserCode())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_GROUP_ROOM_INVITATION);
        }
    }
}
