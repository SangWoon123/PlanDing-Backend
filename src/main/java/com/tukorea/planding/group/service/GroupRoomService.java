package com.tukorea.planding.group.service;

import com.tukorea.planding.group.dao.GroupRoomRepository;
import com.tukorea.planding.group.domain.GroupRoom;
import com.tukorea.planding.group.domain.UserGroupMembership;
import com.tukorea.planding.group.dto.RequestGroupRoom;
import com.tukorea.planding.group.dto.ResponseGroupRoom;
import com.tukorea.planding.user.dao.UserRepository;
import com.tukorea.planding.user.domain.User;
import com.tukorea.planding.user.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupRoomService {

    private final UserRepository userRepository;
    private final GroupRoomRepository groupRoomRepository;

    public ResponseGroupRoom createGroupRoom(UserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        GroupRoom newGroupRoom = GroupRoom.builder()
                .owner(user.getCode())
                .build();

        newGroupRoom.addUser(user);
        GroupRoom savedGroupRoom = groupRoomRepository.save(newGroupRoom);

        return ResponseGroupRoom.from(savedGroupRoom);
    }

    //    @Transactional
    public ResponseGroupRoom inviteGroupRoom(UserInfo userInfo, RequestGroupRoom invitedUserInfo) {
        RequestGroupRoom checking = RequestGroupRoom.checking(invitedUserInfo);

        User invitingUser = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        GroupRoom groupRoom = groupRoomRepository.findByGroupCode(invitedUserInfo.getInviteGroupCode())
                .orElseThrow(() -> new IllegalArgumentException("GroupRoom Not Found"));

        validInvitePermission(groupRoom,invitingUser);

        User invitedUser = findUserByRequest(checking);
        groupRoom.addUser(invitedUser);

        return ResponseGroupRoom.from(groupRoom);
    }

    private User findUserByRequest(RequestGroupRoom checking) {
        if (checking.getUserCode() == null) {
            return userRepository.findByEmail(checking.getUserEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("InvitedUser not found"));
        } else {
            return userRepository.findByCode(checking.getUserCode())
                    .orElseThrow(() -> new UsernameNotFoundException("InvitedUser not found"));
        }
    }
    private void validInvitePermission(GroupRoom groupRoom, User invitingUser){
        if (!groupRoom.getOwner().equals(invitingUser.getCode())) {
            throw new IllegalArgumentException("User does not have permission to invite this groupRoom");
        }
    }
}
