package com.tukorea.planding.group.service;

import com.tukorea.planding.domain.group.repository.GroupRoomRepository;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.dto.GroupCreateRequest;
import com.tukorea.planding.domain.invitation.dto.InvitationRequest;
import com.tukorea.planding.domain.group.dto.GroupResponse;
import com.tukorea.planding.domain.group.service.GroupRoomService;
import com.tukorea.planding.domain.schedule.repository.ScheduleRepository;
import com.tukorea.planding.domain.schedule.service.ScheduleService;
import com.tukorea.planding.domain.user.mapper.UserMapper;
import com.tukorea.planding.domain.user.repository.UserRepository;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.global.oauth.details.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GroupRoomServiceTest {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRoomService groupRoomService;
    @Autowired
    private GroupRoomRepository groupRoomRepository;

    @Test
    @DisplayName("유저 A가 그룹방을 생성한다.")
    void createGroupRoom() {
        User user = User.builder()
                .userCode("#abcd")
                .build();

        UserInfo userInfo = UserMapper.toUserInfo(user);

        userRepository.save(user);

        GroupResponse groupRoom = groupRoomService.createGroupRoom(userInfo, GroupCreateRequest.builder()
                .name("first_group")
                .build());

        Optional<GroupRoom> getgroup = groupRoomRepository.findById(groupRoom.id());

        Assertions.assertNotNull(getgroup);
        Assertions.assertEquals(groupRoom.code(), getgroup.get().getGroupCode());
        Assertions.assertEquals(groupRoom.ownerCode(), getgroup.get().getOwner());
    }

    @Test
    @DisplayName("유저A가 유저B를 그룹방에 초대한다.")
    public void inviteGroup() {
        User userA = User.builder()
                .userCode("#abcd")
                .email("test")
                .role(Role.USER)
                .build();
        userRepository.save(userA);

        User userB = User.builder()
                .email("test2")
                .role(Role.USER)
                .userCode("#1234")
                .build();
        userRepository.save(userB);

        GroupRoom groupRoom = GroupRoom.builder()
                .owner(userA.getUserCode())
                .groupCode("#group")
                .build();

        groupRoomRepository.save(groupRoom);

        // 유저 B 그룹방에 초대
        userB.joinGroupRoom(groupRoom);

        // 그룹방에 유저 B가 초대되었는지 확인
        GroupRoom savedGroupRoom = groupRoomRepository.findById(groupRoom.getId()).orElse(null);
        assertNotNull(savedGroupRoom);
        assertTrue(savedGroupRoom.getGroupMemberships().stream().anyMatch(membership -> membership.getUser().equals(userB)));

    }

    @Test
    @DisplayName("서비스 코드 inviteGorupRoom 테스트")
    public void inviteGroupService() {
        User userA = User.builder()
                .email("userA")
                .userCode("#abcd")
                .role(Role.USER)
                .build();
        userRepository.save(userA);

        User userB = User.builder()
                .email("userB")
                .userCode("#1234")
                .role(Role.USER)
                .build();
        userRepository.save(userB);

        GroupRoom groupRoom = GroupRoom.builder()
                .owner(userA.getUserCode())
                .build();

        GroupRoom save = groupRoomRepository.save(groupRoom);


        InvitationRequest invitationRequest = InvitationRequest
                .builder()
                .inviteGroupCode(save.getGroupCode())
                .userCode(userB.getUserCode())
                .build();

        // 유저 B 그룹방에 초대
        groupRoomService.handleInvitation(UserMapper.toUserInfo(userA), invitationRequest);

        // 그룹방에 유저 B가 초대되었는지 확인
        GroupRoom savedGroupRoom = groupRoomRepository.findById(groupRoom.getId()).orElse(null);
        assertNotNull(savedGroupRoom);
        assertTrue(savedGroupRoom.getGroupMemberships().stream().anyMatch(membership -> membership.getUser().equals(userB)));
    }

    @Test
    @DisplayName("서비스 코드 inviteGorupRomm 실패테스트")
    public void failInviteGroupService() {
        User userA = User.builder()
                .email("userA")
                .userCode("#abcd")
                .build();
        userRepository.save(userA);

        User userB = User.builder()
                .email("userB")
                .userCode("#1234")
                .build();
        userRepository.save(userB);

        User userC = User.builder()
                .email("userC")
                .userCode("#qwer")
                .build();
        userRepository.save(userC);

        GroupRoom groupRoom = GroupRoom.builder()
                .owner(userA.getUserCode())
                .build();

        GroupRoom save = groupRoomRepository.save(groupRoom);


        InvitationRequest invitationRequest = InvitationRequest
                .builder()
                .inviteGroupCode(save.getGroupCode())
                .userCode(userB.getUserCode())
                .build();

        // 그룹방에 유저 B가 초대되었는지 확인
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                        groupRoomService.handleInvitation(UserMapper.toUserInfo(userC), invitationRequest),
                "User does not have permission to invite this groupRoom");
    }
}