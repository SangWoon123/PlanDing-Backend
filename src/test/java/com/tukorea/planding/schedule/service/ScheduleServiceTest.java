package com.tukorea.planding.schedule.service;

import com.tukorea.planding.schedule.dao.ScheduleRepository;
import com.tukorea.planding.schedule.dto.RequestSchedule;
import com.tukorea.planding.schedule.dto.ResponseSchedule;
import com.tukorea.planding.user.dao.UserRepository;
import com.tukorea.planding.user.domain.User;
import com.tukorea.planding.user.dto.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScheduleServiceTest {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createSchedule() {
        //given
        UserInfo userInfo = UserInfo.builder()
                .email("test@")
                .build();

        User user=User.builder()
                .email("test@")
                .build();

        userRepository.save(user);

        String title = "Test Schedule";
        String content = "Test Content";
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        RequestSchedule requestSchedule = RequestSchedule.builder()
                .startTime(startTime)
                .endTime(endTime)
                .title(title)
                .content(content)
                .build();

        //when
        ResponseSchedule schedule = scheduleService.createSchedule(userInfo, requestSchedule);

        //then
        assertNotNull(schedule);
        assertEquals(title,schedule.getTitle());
        assertEquals(content,schedule.getContent());
        assertEquals(startTime,schedule.getStartTime());
        assertEquals(endTime,schedule.getEndTime());
    }

    @Test
    void deleteSchedule(){
        //given
        UserInfo userInfo = UserInfo.builder()
                .email("test@")
                .build();

        User user=User.builder()
                .email("test@")
                .build();

        userRepository.save(user);

        String title = "Test Schedule";
        String content = "Test Content";
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        RequestSchedule requestSchedule = RequestSchedule.builder()
                .startTime(startTime)
                .endTime(endTime)
                .title(title)
                .content(content)
                .build();

        //when
        ResponseSchedule schedule = scheduleService.createSchedule(userInfo, requestSchedule);
        scheduleService.deleteSchedule(userInfo,schedule.getId());

        //then
        assertEquals(Optional.empty(),scheduleRepository.findById(schedule.getId()));
    }

}