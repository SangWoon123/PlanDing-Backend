package com.tukorea.planding.group.service;

import com.tukorea.planding.group.dao.GroupRoomRepository;
import com.tukorea.planding.group.domain.GroupRoom;
import com.tukorea.planding.group.dto.RequestCreateGroupRoom;
import com.tukorea.planding.group.dto.RequestInviteGroupRoom;
import com.tukorea.planding.group.dto.ResponseGroupRoom;
import com.tukorea.planding.schedule.dao.ScheduleRepository;
import com.tukorea.planding.schedule.domain.Schedule;
import com.tukorea.planding.schedule.dto.RequestSchedule;
import com.tukorea.planding.schedule.dto.ResponseSchedule;
import com.tukorea.planding.schedule.service.ScheduleService;
import com.tukorea.planding.user.dao.UserRepository;
import com.tukorea.planding.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
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
        ResponseGroupRoom groupRoom = groupRoomService.createGroupRoom(User.toUserInfo(user), RequestCreateGroupRoom
                .builder()
                .title("group_name")
                .build());

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        RequestSchedule requestSchedule = RequestSchedule.builder()
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .date(TEST_DATE)
                .build();

        // 스케줄 생성
        groupScheduleService.createGroupSchedule(groupRoom.getCode(), requestSchedule);


        // 생성된 스케줄 검증
        GroupRoom group = groupRoomRepository.findByGroupCode(groupRoom.getCode())
                .orElseThrow(() -> new AssertionError("Group room not found"));
        assertNotNull(group);


        assertEquals(1, group.getSchedules().size());
        Schedule createdSchedule = group.getSchedules().get(0);
        assertEquals(TEST_TITLE, createdSchedule.getTitle());
        assertEquals(TEST_CONTENT, createdSchedule.getContent());
        assertEquals(TEST_DATE, createdSchedule.getDate());
        assertEquals(startTime, createdSchedule.getStartTime());
        assertEquals(endTime, createdSchedule.getEndTime());
    }

    @Test
    @DisplayName("성공: 유저 A가 작성한 스케줄 같은 그룹방 유저B가 조회시")
    public void createGroupScheduleTest2() {
        //given
        User userA = createUserAndSave(TEST_EMAIL, "code");
        ResponseGroupRoom groupRoom = groupRoomService.createGroupRoom(User.toUserInfo(userA), RequestCreateGroupRoom
                .builder()
                .title("group_name")
                .build());

        User userB = createUserAndSave("testB", "codeB");

        RequestInviteGroupRoom requestInviteGroupRoom = RequestInviteGroupRoom
                .builder()
                .inviteGroupCode(groupRoom.getCode())
                .userCode(userB.getCode())
                .build();

        groupRoomService.inviteGroupRoom(User.toUserInfo(userA), requestInviteGroupRoom);

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        RequestSchedule requestSchedule = RequestSchedule.builder()
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .date(TEST_DATE)
                .build();

        // when
        groupScheduleService.createGroupSchedule(groupRoom.getCode(), requestSchedule);

        //then
        List<ResponseSchedule> result = scheduleService.getSchedulesByGroupRoom(groupRoom.getId(), User.toUserInfo(userB));

        assertNotNull(result);
        assertEquals(result.get(0).getTitle(),requestSchedule.getTitle());
        assertEquals(result.get(0).getContent(),requestSchedule.getContent());
        assertEquals(result.get(0).getStartTime(),requestSchedule.getStartTime());
        assertEquals(result.get(0).getEndTime(),requestSchedule.getEndTime());
    }

    @Test
    @DisplayName("실패: 유저 A가 작성한 스케줄 외부 유저C가 조회시")
    public void createGroupScheduleFailTest() {
        //given
        User userA = createUserAndSave(TEST_EMAIL, "code");
        ResponseGroupRoom groupRoom = groupRoomService.createGroupRoom(User.toUserInfo(userA), RequestCreateGroupRoom
                .builder()
                .title("group_name")
                .build());

        User userC = createUserAndSave("testC", "codeC");

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        RequestSchedule requestSchedule = RequestSchedule.builder()
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .date(TEST_DATE)
                .build();

        // when
        groupScheduleService.createGroupSchedule(groupRoom.getCode(), requestSchedule);

        //then
        assertThrows(AccessDeniedException.class,()->scheduleService.getSchedulesByGroupRoom(groupRoom.getId(), User.toUserInfo(userC)));
    }

    private User createUserAndSave(String email, String userCode) {
        User user = User.builder()
                .email(email)
                .code(userCode)
                .build();
        return userRepository.save(user);
    }
}
