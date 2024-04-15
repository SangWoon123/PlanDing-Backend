package com.tukorea.planding.group.service;

import com.tukorea.planding.domain.group.dto.GroupCreateRequest;
import com.tukorea.planding.domain.group.dto.GroupScheduleRequest;
import com.tukorea.planding.domain.group.dto.GroupInviteRequest;
import com.tukorea.planding.domain.group.dto.GroupResponse;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.repository.GroupRoomRepository;
import com.tukorea.planding.domain.group.service.GroupRoomService;
import com.tukorea.planding.domain.group.service.GroupScheduleService;
import com.tukorea.planding.domain.schedule.dto.ScheduleRequest;
import com.tukorea.planding.domain.schedule.dto.ScheduleResponse;
import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.schedule.repository.ScheduleRepository;
import com.tukorea.planding.domain.schedule.service.ScheduleService;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.mapper.UserMapper;
import com.tukorea.planding.domain.user.repository.UserRepository;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import com.tukorea.planding.global.oauth.details.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
public class GroupScheduleTest {

    private static final String TEST_EMAIL = "test@";
    private static final String TEST_TITLE = "Test Schedule";
    private static final String TEST_CONTENT = "Test Content";
    private static final LocalDate TEST_DATE = LocalDate.of(2024, 01, 02);


    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupScheduleService groupScheduleService;
    @Autowired
    private GroupRoomRepository groupRoomRepository;
    @Autowired
    private GroupRoomService groupRoomService;


    @Test
    @DisplayName("성공: 유저 A가 작성한 스케줄 조회")
    public void createGroupScheduleTest() {
        User user = createUserAndSave(TEST_EMAIL, "code");
        GroupResponse groupRoom = groupRoomService.createGroupRoom(UserMapper.toUserInfo(user), GroupCreateRequest
                .builder()
                .name("group_name")
                .build());

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        GroupScheduleRequest requestSchedule = GroupScheduleRequest.builder()
                .userId(user.getId())
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .scheduleDate(TEST_DATE)
                .build();

        // 스케줄 생성
        groupScheduleService.createGroupSchedule(groupRoom.code(), requestSchedule);


        // 생성된 스케줄 검증
        GroupRoom group = groupRoomRepository.findByGroupCode(groupRoom.code())
                .orElseThrow(() -> new AssertionError("Group room not found"));
        assertNotNull(group);


        assertEquals(1, group.getSchedules().size());
        Schedule createdSchedule = group.getSchedules().get(0);
        assertEquals(TEST_TITLE, createdSchedule.getTitle());
        assertEquals(TEST_CONTENT, createdSchedule.getContent());
        assertEquals(TEST_DATE, createdSchedule.getScheduleDate());
        assertEquals(startTime, createdSchedule.getStartTime());
        assertEquals(endTime, createdSchedule.getEndTime());
    }

    @Test
    @DisplayName("성공: 유저 A가 작성한 스케줄 같은 그룹방 유저B가 조회시")
    public void createGroupScheduleTest2() {
        //given
        User userA = createUserAndSave(TEST_EMAIL, "code");
        GroupResponse groupRoom = groupRoomService.createGroupRoom(UserMapper.toUserInfo(userA), GroupCreateRequest
                .builder()
                .name("group_name")
                .build());

        User userB = createUserAndSave("testB", "codeB");

        GroupInviteRequest groupInviteRequest = GroupInviteRequest
                .builder()
                .inviteGroupCode(groupRoom.code())
                .userCode(userB.getUserCode())
                .build();

        groupRoomService.handleInvitation(UserMapper.toUserInfo(userA), groupInviteRequest);

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        GroupScheduleRequest requestSchedule = GroupScheduleRequest.builder()
                .userId(userA.getId())
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .scheduleDate(TEST_DATE)
                .build();

        // when
        groupScheduleService.createGroupSchedule(groupRoom.code(), requestSchedule);

        //then
        List<ScheduleResponse> result = scheduleService.getSchedulesByGroupRoom(groupRoom.id(), UserMapper.toUserInfo(userB));

        assertNotNull(result);
        assertEquals(result.get(0).title(), requestSchedule.title());
        assertEquals(result.get(0).complete(), requestSchedule.content());
        assertEquals(result.get(0).startTime(), requestSchedule.startTime());
        assertEquals(result.get(0).endTime(), requestSchedule.endTime());
    }

    @Test
    @DisplayName("실패: 유저 A가 작성한 스케줄 외부 유저C가 조회시")
    public void createGroupScheduleFailTest() {
        //given
        User userA = createUserAndSave(TEST_EMAIL, "code");
        GroupResponse groupRoom = groupRoomService.createGroupRoom(UserMapper.toUserInfo(userA), GroupCreateRequest
                .builder()
                .name("group_name")
                .build());

        User userC = createUserAndSave("testC", "codeC");

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        GroupScheduleRequest requestSchedule = GroupScheduleRequest.builder()
                .userId(userA.getId())
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .scheduleDate(TEST_DATE)
                .build();

        // when
        groupScheduleService.createGroupSchedule(groupRoom.code(), requestSchedule);

        //then
        assertThrows(BusinessException.class, () -> scheduleService.getSchedulesByGroupRoom(groupRoom.id(), UserMapper.toUserInfo(userC)));
    }

    @Test
    @DisplayName("성공: 유저 A가 작성한 스케줄 수정")
    public void update1() {
        User user = createUserAndSave(TEST_EMAIL, "code");
        GroupResponse groupRoom = groupRoomService.createGroupRoom(UserMapper.toUserInfo(user), GroupCreateRequest
                .builder()
                .name("group_name")
                .build());

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        GroupScheduleRequest requestSchedule = GroupScheduleRequest.builder()
                .userId(user.getId())
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .scheduleDate(TEST_DATE)
                .build();

        // 스케줄 생성
        ScheduleResponse groupSchedule = groupScheduleService.createGroupSchedule(groupRoom.code(), requestSchedule);

        Schedule schedule = scheduleRepository.findById(groupSchedule.id())
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + groupSchedule.id()));

        ScheduleRequest updateSchedule = ScheduleRequest.builder()
                .startTime(startTime)
                .endTime(endTime)
                .title("update")
                .content("update")
                .scheduleDate(TEST_DATE)
                .build();

        //when
        scheduleService.updateScheduleByGroupRoom(groupRoom.id(), schedule.getId(), updateSchedule, UserMapper.toUserInfo(user));

        //then
        Schedule result = scheduleRepository.findById(groupSchedule.id())
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + groupSchedule.id()));

        assertEquals(result.getTitle(), "update");
        assertEquals(result.getContent(), "update");
    }

    @Test
    @DisplayName("성공: 같은 그룹방 유저B가 수정")
    public void update2() {
        //given
        User userA = createUserAndSave(TEST_EMAIL, "code");
        GroupResponse groupRoom = groupRoomService.createGroupRoom(UserMapper.toUserInfo(userA), GroupCreateRequest
                .builder()
                .name("group_name")
                .build());

        User userB = createUserAndSave("testB", "codeB");

        GroupInviteRequest groupInviteRequest = GroupInviteRequest
                .builder()
                .inviteGroupCode(groupRoom.code())
                .userCode(userB.getUserCode())
                .build();

        groupRoomService.handleInvitation(UserMapper.toUserInfo(userA), groupInviteRequest);

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        GroupScheduleRequest requestSchedule = GroupScheduleRequest.builder()
                .userId(userA.getId())
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .scheduleDate(TEST_DATE)
                .build();

        // 스케줄 생성
        ScheduleResponse groupSchedule = groupScheduleService.createGroupSchedule(groupRoom.code(), requestSchedule);

        Schedule schedule = scheduleRepository.findById(groupSchedule.id())
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + groupSchedule.id()));

        ScheduleRequest updateSchedule = ScheduleRequest.builder()
                .startTime(startTime)
                .endTime(endTime)
                .title("update")
                .content("update")
                .scheduleDate(TEST_DATE)
                .build();

        //when
        scheduleService.updateScheduleByGroupRoom(groupRoom.id(), schedule.getId(), updateSchedule, UserMapper.toUserInfo(userB));

        //then
        Schedule result = scheduleRepository.findById(groupSchedule.id())
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + groupSchedule.id()));

        assertEquals(result.getTitle(), "update");
        assertEquals(result.getContent(), "update");
    }

    @Test
    @DisplayName("실패: 외부 유저C가 수정")
    public void update3() {
        User user = createUserAndSave(TEST_EMAIL, "code");
        GroupResponse groupRoom = groupRoomService.createGroupRoom(UserMapper.toUserInfo(user), GroupCreateRequest
                .builder()
                .name("group_name")
                .build());

        User userC = createUserAndSave("testC", "codeC");

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        GroupScheduleRequest requestSchedule = GroupScheduleRequest.builder()
                .userId(user.getId())
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .scheduleDate(TEST_DATE)
                .build();

        // 스케줄 생성
        ScheduleResponse groupSchedule = groupScheduleService.createGroupSchedule(groupRoom.code(), requestSchedule);

        Schedule schedule = scheduleRepository.findById(groupSchedule.id())
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + groupSchedule.id()));

        ScheduleRequest updateSchedule = ScheduleRequest.builder()
                .startTime(startTime)
                .endTime(endTime)
                .title("update")
                .content("update")
                .scheduleDate(TEST_DATE)
                .build();

        //when
        assertThrows(BusinessException.class, () -> scheduleService.updateScheduleByGroupRoom(groupRoom.id(), schedule.getId(), updateSchedule, UserMapper.toUserInfo(userC)));
    }

    @Test
    @DisplayName("성공: 유저 A가 작성한 스케줄 삭제")
    public void delete1() {
        User user = createUserAndSave(TEST_EMAIL, "code");
        GroupResponse groupRoom = groupRoomService.createGroupRoom(UserMapper.toUserInfo(user), GroupCreateRequest
                .builder()
                .name("group_name")
                .build());

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        GroupScheduleRequest requestSchedule = GroupScheduleRequest.builder()
                .userId(user.getId())
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .scheduleDate(TEST_DATE)
                .build();

        // 스케줄 생성
        ScheduleResponse groupSchedule = groupScheduleService.createGroupSchedule(groupRoom.code(), requestSchedule);

        scheduleService.deleteScheduleByGroupRoom(groupRoom.id(), groupSchedule.id(), UserMapper.toUserInfo(user));


        assertThrows(BusinessException.class, () -> scheduleRepository.findById(groupSchedule.id()).orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND)));
    }

    @Test
    @DisplayName("성공: 같은 그룹방 유저 B가 삭제")
    public void delete2() {
        //given
        User userA = createUserAndSave(TEST_EMAIL, "code");
        GroupResponse groupRoom = groupRoomService.createGroupRoom(UserMapper.toUserInfo(userA), GroupCreateRequest
                .builder()
                .name("group_name")
                .build());

        User userB = createUserAndSave("testB", "codeB");

        GroupInviteRequest groupInviteRequest = GroupInviteRequest
                .builder()
                .inviteGroupCode(groupRoom.code())
                .userCode(userB.getUserCode())
                .build();

        groupRoomService.handleInvitation(UserMapper.toUserInfo(userA), groupInviteRequest);

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        GroupScheduleRequest requestSchedule = GroupScheduleRequest.builder()
                .userId(userA.getId())
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .scheduleDate(TEST_DATE)
                .build();

        // 스케줄 생성
        ScheduleResponse groupSchedule = groupScheduleService.createGroupSchedule(groupRoom.code(), requestSchedule);

        //when
        scheduleService.deleteScheduleByGroupRoom(groupRoom.id(), groupSchedule.id(), UserMapper.toUserInfo(userB));
        //then
        assertThrows(BusinessException.class, () -> scheduleRepository.findById(groupSchedule.id()).orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND)));
    }

    @Test
    @DisplayName("실패: 외부 유저 C가 수정")
    public void delete3() {
        User user = createUserAndSave(TEST_EMAIL, "code");
        GroupResponse groupRoom = groupRoomService.createGroupRoom(UserMapper.toUserInfo(user), GroupCreateRequest
                .builder()
                .name("group_name")
                .build());

        User userC = createUserAndSave("testC", "codeC");

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        GroupScheduleRequest requestSchedule = GroupScheduleRequest.builder()
                .userId(user.getId())
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .scheduleDate(TEST_DATE)
                .build();

        // 스케줄 생성
        ScheduleResponse groupSchedule = groupScheduleService.createGroupSchedule(groupRoom.code(), requestSchedule);

        //when
        assertThrows(BusinessException.class, () -> scheduleService.deleteScheduleByGroupRoom(groupRoom.id(), groupSchedule.id(), UserMapper.toUserInfo(userC)));
    }


    private User createUserAndSave(String email, String userCode) {
        User user = User.builder()
                .email(email)
                .userCode(userCode)
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }
}
