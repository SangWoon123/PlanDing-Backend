package com.tukorea.planding.schedule.service;

import com.tukorea.planding.domain.group.repository.GroupRoomRepository;
import com.tukorea.planding.domain.group.service.GroupRoomService;
import com.tukorea.planding.domain.group.service.GroupScheduleService;
import com.tukorea.planding.domain.schedule.repository.ScheduleRepository;
import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.schedule.dto.RequestSchedule;
import com.tukorea.planding.domain.schedule.dto.ResponseSchedule;
import com.tukorea.planding.domain.schedule.service.ScheduleService;
import com.tukorea.planding.domain.user.repository.UserRepository;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.global.oauth.details.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ScheduleServiceTest {

    private static final String TEST_EMAIL = "test@";
    private static final String TEST_TITLE = "Test Schedule";
    private static final String TEST_CONTENT = "Test Content";
    private static final LocalDate TEST_DATE = LocalDate.of(2024, 01, 02);
    private static final LocalDate END_DATE = LocalDate.of(2024, 01, 03);


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
    void createSchedule() {
        //given
        UserInfo userInfo = UserInfo.builder()
                .email("test@")
                .build();

        User user = User.builder()
                .email("test@")
                .build();

        userRepository.save(user);

        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        RequestSchedule requestSchedule = RequestSchedule.builder()
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .scheduleDate(TEST_DATE)
                .build();

        //when
        ResponseSchedule schedule = scheduleService.createSchedule(userInfo, requestSchedule);

        //then
        assertNotNull(schedule);
        assertEquals(TEST_TITLE, schedule.getTitle());
        assertEquals(TEST_CONTENT, schedule.getContent());
        assertEquals(startTime, schedule.getStartTime());
        assertEquals(endTime, schedule.getEndTime());
    }

    @Test
    void deleteSchedule() {
        //given
        UserInfo userInfo = UserInfo.builder()
                .email(TEST_EMAIL)
                .build();

        createUserAndSave(TEST_EMAIL);

        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        RequestSchedule requestSchedule = RequestSchedule.builder()
                .startTime(startTime)
                .endTime(endTime)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .scheduleDate(TEST_DATE)
                .build();

        //when
        ResponseSchedule schedule = scheduleService.createSchedule(userInfo, requestSchedule);
        scheduleService.deleteSchedule(userInfo, schedule.getId());

        //then
        assertEquals(Optional.empty(), scheduleRepository.findById(schedule.getId()));
    }

    @Test
    @DisplayName("주간 개인 스케줄 가져오기")
    public void getWeekSchedule() {
        //given
        UserInfo userInfo = UserInfo.builder()
                .email("test@")
                .role(Role.USER)
                .userCode("#CODE")
                .username("username")
                .build();

        User user = createUserAndSave(TEST_EMAIL);

        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        Schedule schedule = Schedule
                .builder()
                .user(user)
                .scheduleDate(TEST_DATE)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        scheduleRepository.save(schedule);

        Schedule schedule1 = createAndSaveSchedule(user, TEST_TITLE, TEST_CONTENT, startTime, endTime, TEST_DATE);

        scheduleRepository.save(schedule1);

        //when
        List<ResponseSchedule> result = scheduleService.getWeekSchedule(TEST_DATE, END_DATE, userInfo);

        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(result.get(0).getStartTime(), schedule1.getStartTime());
        assertEquals(result.get(1).getStartTime(), schedule.getStartTime());
    }

    @Test
    @DisplayName("스케줄 수정 테스트")
    public void updateSchedule() {
        //given
        User user = createUserAndSave(TEST_EMAIL);
        UserInfo userInfo = User.toUserInfo(user);

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);
        Schedule schedule = createAndSaveSchedule(user, TEST_TITLE, TEST_CONTENT, startTime, endTime, TEST_DATE);

        //when
        String updateTitle = "update_title";
        String updateContent = "update_content";
        schedule.update(updateTitle, updateContent, null, null);

        //then
        assertEquals(schedule.getTitle(), updateTitle);
        assertEquals(schedule.getContent(), updateContent);
        assertEquals(schedule.getStartTime(), startTime);
        assertEquals(schedule.getEndTime(), endTime);
    }

    @Test
    @DisplayName("실패: 스케줄 수정시 endTime < startTime")
    public void updateError() {
        //given
        User user = createUserAndSave(TEST_EMAIL);

        LocalTime startTime = LocalTime.of(7, 0);
        LocalTime endTime = LocalTime.of(9, 0);
        Schedule schedule = createAndSaveSchedule(user, TEST_TITLE, TEST_CONTENT, startTime, endTime, TEST_DATE);

        assertEquals(startTime, schedule.getStartTime());
        assertEquals(endTime, schedule.getEndTime());
        //when
        String updateTitle = "update_title";
        String updateContent = "update_content";

        //then
        assertThrows(IllegalArgumentException.class, () -> schedule.update(updateTitle, updateContent, LocalTime.of(10, 0), LocalTime.of(9, 10)));
    }

    private User createUserAndSave(String email) {
        User user = User.builder()
                .email(email)
                .role(Role.USER)
                .userCode("#CODE")
                .username("username").build();
        return userRepository.save(user);
    }

    private Schedule createAndSaveSchedule(User user, String title, String content, LocalTime startTime, LocalTime endTime, LocalDate date) {
        Schedule schedule = Schedule.builder()
                .user(user)
                .scheduleDate(date)
                .title(title)
                .content(content)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        return scheduleRepository.save(schedule);
    }

}