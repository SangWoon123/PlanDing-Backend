package com.tukorea.planding.schedule.service;

import com.tukorea.planding.group.dao.UserGroupMembershipRepository;
import com.tukorea.planding.group.domain.UserGroupMembership;
import com.tukorea.planding.schedule.dao.ScheduleRepository;
import com.tukorea.planding.schedule.domain.Schedule;
import com.tukorea.planding.schedule.dto.RequestSchedule;
import com.tukorea.planding.schedule.dto.ResponseSchedule;
import com.tukorea.planding.user.dao.UserRepository;
import com.tukorea.planding.user.domain.User;
import com.tukorea.planding.user.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    private final UserGroupMembershipRepository userGroupMembershipRepository;

    public ResponseSchedule createSchedule(UserInfo userInfo, RequestSchedule requestSchedule) {
        User user = validateUserByEmail(userInfo.getEmail());

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
        Schedule schedule = findScheduleById(scheduleId);
        User user = schedule.getUser();

        if (!user.getEmail().equals(userInfo.getEmail())) {
            throw new IllegalArgumentException("User does not have permission to delete this schedule");
        }

        scheduleRepository.delete(schedule);
    }

    public List<ResponseSchedule> getSchedule(LocalDate date, UserInfo userInfo) {
        User user = validateUserByEmail(userInfo.getEmail());

        List<Schedule> schedules = scheduleRepository.findByDateAndUser(date, user);

        List<ResponseSchedule> responseSchedules = schedules.stream()
                .map(ResponseSchedule::from)
                .sorted(ResponseSchedule.getComparatorByStartTime())
                .collect(Collectors.toList());

        return responseSchedules;
    }

    public ResponseSchedule updateSchedule(Long scheduleId, RequestSchedule requestSchedule, UserInfo userInfo) {
        User user = validateUserByEmail(userInfo.getEmail());
        Schedule schedule = findScheduleById(scheduleId);

        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not the owner of this Schedule");
        }

        schedule.update(schedule.getTitle(), schedule.getContent(), schedule.getStartTime(), schedule.getEndTime());

        return ResponseSchedule.from(schedule);
    }

    /*
    그룹룸 스케줄관련 코드
    */
    public List<ResponseSchedule> getSchedulesByGroupRoom(Long groupRoomId, UserInfo userInfo) {
        // 유저가 그룹룸에 접근할 권리가있는지 확인
        if (!userGroupMembershipRepository.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new AccessDeniedException("사용자는 이 그룹룸에 접근할 권한이 없습니다.");
        }

        // 그룹룸 ID를 기반으로 스케줄을 조회
        List<Schedule> schedules = scheduleRepository.findByGroupRoomId(groupRoomId);

        // 조회된 스케줄 리스트를 ResponseSchedule DTO로 변환
        return schedules.stream()
                .map(ResponseSchedule::from)
                .collect(Collectors.toList());
    }

    public ResponseSchedule updateScheduleByGroupRoom(Long groupRoomId, Long scheduleId, RequestSchedule requestSchedule, UserInfo userInfo) {
        if (!userGroupMembershipRepository.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new AccessDeniedException("사용자는 이 그룹룸에 접근할 권한이 없습니다.");
        }

        Schedule schedule = findScheduleById(scheduleId);
        schedule.update(requestSchedule.getTitle(), requestSchedule.getContent(), requestSchedule.getStartTime(), requestSchedule.getEndTime());

        return ResponseSchedule.from(schedule);
    }

    public void deleteScheduleByGroupRoom(Long groupRoomId, Long scheduleId, UserInfo userInfo) {
        if (!userGroupMembershipRepository.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new AccessDeniedException("사용자는 이 그룹룸에 접근할 권한이 없습니다.");
        }
        scheduleRepository.deleteById(scheduleId);
    }

    private User validateUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private Schedule findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));
    }

}
