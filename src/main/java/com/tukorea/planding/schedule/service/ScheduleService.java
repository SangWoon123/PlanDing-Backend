package com.tukorea.planding.schedule.service;

import com.tukorea.planding.schedule.dao.ScheduleRepository;
import com.tukorea.planding.schedule.domain.Schedule;
import com.tukorea.planding.schedule.dto.RequestSchedule;
import com.tukorea.planding.schedule.dto.ResponseSchedule;
import com.tukorea.planding.user.dao.UserRepository;
import com.tukorea.planding.user.domain.User;
import com.tukorea.planding.user.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public ResponseSchedule createSchedule(UserInfo userInfo, RequestSchedule requestSchedule) {
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Schedule newSchedule = Schedule.builder()
                .user(user)
                .title(requestSchedule.getTitle())
                .content(requestSchedule.getContent())
                .date(requestSchedule.getDate())
                .startTime(requestSchedule.getStartTime())
                .endTime(requestSchedule.getEndTime())
                .complete(false)
                .build();

        Schedule save = scheduleRepository.save(newSchedule);

        return ResponseSchedule.from(save);
    }

    public void deleteSchedule(UserInfo userInfo,Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));

        User user = schedule.getUser();

        if (!user.getEmail().equals(userInfo.getEmail())) {
            throw new IllegalArgumentException("User does not have permission to delete this schedule");
        }

        scheduleRepository.delete(schedule);
    }
}
