package com.tukorea.planding.group.service;

import com.tukorea.planding.group.dao.GroupRoomRepository;
import com.tukorea.planding.group.dao.UserGroupMembershipRepository;
import com.tukorea.planding.group.domain.GroupRoom;
import com.tukorea.planding.group.domain.UserGroupMembership;
import com.tukorea.planding.group.dto.RequestCreateGroupRoom;
import com.tukorea.planding.group.dto.RequestInviteGroupRoom;
import com.tukorea.planding.group.dto.ResponseGroupRoom;
import com.tukorea.planding.user.dao.UserRepository;
import com.tukorea.planding.user.domain.User;
import com.tukorea.planding.user.dto.UserInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupRoomService {

    private final UserRepository userRepository;
    private final UserGroupMembershipRepository userGroupMembershipRepository;
    private final GroupRoomRepository groupRoomRepository;

    @Transactional
    public ResponseGroupRoom createGroupRoom(UserInfo userInfo, RequestCreateGroupRoom createGroupRoom) {
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        GroupRoom newGroupRoom = GroupRoom.builder()
                .title(createGroupRoom.getTitle())
                .owner(user.getCode())
                .build();

        newGroupRoom.addUser(user);
        GroupRoom savedGroupRoom = groupRoomRepository.save(newGroupRoom);

        // 중간테이블에 유저, 그룹 정보 저장
        userGroupMembershipRepository.saveAll(newGroupRoom.getGroupMemberships());

        return ResponseGroupRoom.from(savedGroupRoom);
    }

    @Transactional
    public ResponseGroupRoom inviteGroupRoom(UserInfo userInfo, RequestInviteGroupRoom invitedUserInfo) {
        RequestInviteGroupRoom checking = RequestInviteGroupRoom.checking(invitedUserInfo);

        User invitingUser = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        GroupRoom groupRoom = groupRoomRepository.findByGroupCode(invitedUserInfo.getInviteGroupCode())
                .orElseThrow(() -> new IllegalArgumentException("GroupRoom Not Found"));

        validInvitePermission(groupRoom, invitingUser);

        User invitedUser = findUserByRequest(checking);
        groupRoom.addUser(invitedUser);

        return ResponseGroupRoom.from(groupRoom);
    }

    // 유저가 속한 그룹룸 가져오기
    public List<ResponseGroupRoom> getAllGroupRoomByUser(UserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GroupRoom> groupRooms = groupRoomRepository.findGroupRoomsByUserId(user.getId());

        return groupRooms.stream()
                .map(ResponseGroupRoom::from)
                .collect(Collectors.toList());
    }

    private User findUserByRequest(RequestInviteGroupRoom checking) {
        if (checking.getUserCode() == null) {
            return userRepository.findByEmail(checking.getUserEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("InvitedUser not found"));
        } else {
            return userRepository.findByCode(checking.getUserCode())
                    .orElseThrow(() -> new UsernameNotFoundException("InvitedUser not found"));
        }
    }

    private void validInvitePermission(GroupRoom groupRoom, User invitingUser) {
        if (!groupRoom.getOwner().equals(invitingUser.getCode())) {
            throw new IllegalArgumentException("User does not have permission to invite this groupRoom");
        }
    }
}
