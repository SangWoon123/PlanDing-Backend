package com.tukorea.planding.group.service;

import com.tukorea.planding.group.dao.GroupRoomRepository;
import com.tukorea.planding.group.domain.GroupRoom;
import com.tukorea.planding.group.domain.UserGroupMembership;
import com.tukorea.planding.group.dto.RequestGroupRoom;
import com.tukorea.planding.group.dto.ResponseGroupRoom;
import com.tukorea.planding.schedule.dao.ScheduleRepository;
import com.tukorea.planding.schedule.service.ScheduleService;
import com.tukorea.planding.user.dao.UserRepository;
import com.tukorea.planding.user.domain.User;
import com.tukorea.planding.user.dto.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
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
                .code("#abcd")
                .build();

        UserInfo userInfo = User.toUserInfo(user);

        userRepository.save(user);

        ResponseGroupRoom groupRoom = groupRoomService.createGroupRoom(userInfo);

        Optional<GroupRoom> getgroup = groupRoomRepository.findById(groupRoom.getId());

        Assertions.assertNotNull(getgroup);
        Assertions.assertEquals(groupRoom.getCode(), getgroup.get().getGroupCode());
        Assertions.assertEquals(groupRoom.getOwnerCode(), getgroup.get().getOwner());
    }

    @Test
    @DisplayName("유저A가 유저B를 그룹방에 초대한다.")
    public void inviteGroup() {
        User userA = User.builder()
                .code("#abcd")
                .build();
        userRepository.save(userA);

        User userB = User.builder()
                .code("#1234")
                .build();
        userRepository.save(userB);

        GroupRoom groupRoom = GroupRoom.builder()
                .owner(userA.getCode())
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
    @DisplayName("서비스 코드 inviteGorupRomm 테스트")
    public void inviteGroupService() {
        User userA = User.builder()
                .email("userA")
                .code("#abcd")
                .build();
        userRepository.save(userA);

        User userB = User.builder()
                .email("userB")
                .code("#1234")
                .build();
        userRepository.save(userB);

        GroupRoom groupRoom = GroupRoom.builder()
                .owner(userA.getCode())
                .build();

        GroupRoom save = groupRoomRepository.save(groupRoom);


        RequestGroupRoom requestGroupRoom = RequestGroupRoom
                .builder()
                .inviteGroupCode(save.getGroupCode())
                .userCode(userB.getCode())
                .build();

        // 유저 B 그룹방에 초대
        groupRoomService.inviteGroupRoom(User.toUserInfo(userA),requestGroupRoom);

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
                .code("#abcd")
                .build();
        userRepository.save(userA);

        User userB = User.builder()
                .email("userB")
                .code("#1234")
                .build();
        userRepository.save(userB);

        User userC = User.builder()
                .email("userC")
                .code("#qwer")
                .build();
        userRepository.save(userC);

        GroupRoom groupRoom = GroupRoom.builder()
                .owner(userA.getCode())
                .build();

        GroupRoom save = groupRoomRepository.save(groupRoom);


        RequestGroupRoom requestGroupRoom = RequestGroupRoom
                .builder()
                .inviteGroupCode(save.getGroupCode())
                .userCode(userB.getCode())
                .build();

        // 그룹방에 유저 B가 초대되었는지 확인
        Assertions.assertThrows(IllegalArgumentException.class,() ->
                        groupRoomService.inviteGroupRoom(User.toUserInfo(userC), requestGroupRoom),
                "User does not have permission to invite this groupRoom");
    }
}