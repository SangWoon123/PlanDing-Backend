package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.dto.RequestCreateGroupRoom;
import com.tukorea.planding.domain.group.dto.RequestInviteGroupRoom;
import com.tukorea.planding.domain.group.dto.ResponseGroupRoom;
import com.tukorea.planding.domain.group.repository.GroupRoomRepositoryCustomImpl;
import com.tukorea.planding.domain.group.repository.UserGroupMembershipRepository;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import com.tukorea.planding.domain.group.repository.GroupRoomRepository;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.user.repository.UserRepository;
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
    public ResponseGroupRoom createGroupRoom(UserInfo userInfo, RequestCreateGroupRoom createGroupRoom) {
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        GroupRoom newGroupRoom = GroupRoom.builder()
                .name(createGroupRoom.getName())
                .owner(user.getUserCode())
                .build();

        newGroupRoom.addUser(user);
        GroupRoom savedGroupRoom = groupRoomRepository.save(newGroupRoom);

        // 중간테이블에 유저, 그룹 정보 저장
        userGroupMembershipRepository.saveAll(newGroupRoom.getGroupMemberships());

        return ResponseGroupRoom.from(savedGroupRoom);
    }

    @Transactional
    public ResponseGroupRoom inviteGroupRoom(UserInfo userInfo, RequestInviteGroupRoom invitedUserInfo) {
        // 초대하는 유저가 존재하는지 체크하는 로직
        User invitingUser = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        GroupRoom groupRoom = groupRoomRepository.findByGroupCode(invitedUserInfo.getInviteGroupCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_ROOM_NOT_FOUND));

        // 초대하는 유저가 방장인지 체크하는 로직
        validInvitePermission(groupRoom, invitingUser);

        User invitedUser = userRepository.findByUserCode(invitedUserInfo.getUserCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        groupRoom.addUser(invitedUser);

        // 중간테이블에 유저, 그룹 정보 저장
        userGroupMembershipRepository.saveAll(groupRoom.getGroupMemberships());

        return ResponseGroupRoom.from(groupRoom);
    }

    // 유저가 속한 그룹룸 가져오기
    public List<ResponseGroupRoom> getAllGroupRoomByUser(UserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<GroupRoom> groupRooms = groupRoomRepositoryCustom.findGroupRoomsByUserId(user.getId());

        return groupRooms.stream()
                .map(ResponseGroupRoom::from)
                .collect(Collectors.toList());
    }

    private void validInvitePermission(GroupRoom groupRoom, User invitingUser) {
        if (!groupRoom.getOwner().equals(invitingUser.getUserCode())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_GROUP_ROOM_INVITATION);
        }
    }
}
