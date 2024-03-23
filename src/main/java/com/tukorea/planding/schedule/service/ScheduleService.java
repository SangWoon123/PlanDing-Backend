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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
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

    public void deleteSchedule(UserInfo userInfo, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));

        User user = schedule.getUser();

        if (!user.getEmail().equals(userInfo.getEmail())) {
            throw new IllegalArgumentException("User does not have permission to delete this schedule");
        }

        scheduleRepository.delete(schedule);
    }

    public List<ResponseSchedule> getSchedule(LocalDate date, UserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Schedule> schedules = scheduleRepository.findByDateAndUser(date, user);

        List<ResponseSchedule> responseSchedules = schedules.stream()
                .map(ResponseSchedule::from)
                .sorted(ResponseSchedule.getComparatorByStartTime())
                .collect(Collectors.toList());

        return responseSchedules;
    }

    public ResponseSchedule updateSchedule(Long scheduleId, RequestSchedule requestSchedule, UserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));

        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not the owner of this Schedule");
        }

        schedule.update(schedule.getTitle(), schedule.getContent(),schedule.getStartTime(),schedule.getEndTime());

        return ResponseSchedule.from(schedule);
    }

}
