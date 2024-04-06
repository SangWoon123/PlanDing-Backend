package com.tukorea.planding.domain.schedule.service;

import com.tukorea.planding.domain.group.repository.UserGroupMembershipRepositoryCustomImpl;
import com.tukorea.planding.domain.schedule.repository.ScheduleRepositoryCustomImpl;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.repository.UserRepository;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import com.tukorea.planding.domain.schedule.repository.ScheduleRepository;
import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.schedule.dto.RequestSchedule;
import com.tukorea.planding.domain.schedule.dto.ResponseSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleRepositoryCustomImpl scheduleRepositoryCustom;
    private final UserRepository userRepository;
    private final UserGroupMembershipRepositoryCustomImpl userGroupMembershipRepositoryCustomImpl;

    public ResponseSchedule createSchedule(UserInfo userInfo, RequestSchedule requestSchedule) {
        User user = validateUserByEmail(userInfo.getEmail());

        Schedule newSchedule = Schedule.builder()
                .user(user)
                .title(requestSchedule.getTitle())
                .content(requestSchedule.getContent())
                .scheduleDate(requestSchedule.getScheduleDate())
                .startTime(requestSchedule.getStartTime())
                .endTime(requestSchedule.getEndTime())
                .isComplete(false)
                .build();

        Schedule save = scheduleRepository.save(newSchedule);

        return ResponseSchedule.from(save);
    }

    public void deleteSchedule(UserInfo userInfo, Long scheduleId) {
        Schedule schedule = findScheduleById(scheduleId);
        User user = schedule.getUser();

        if (!user.getEmail().equals(userInfo.getEmail())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_SCHEDULE);
        }

        scheduleRepository.delete(schedule);
    }

    public List<ResponseSchedule> getWeekSchedule(LocalDate startDate, LocalDate endDate, UserInfo userInfo) {
        User user = validateUserByEmail(userInfo.getEmail());

        List<Schedule> schedules = scheduleRepositoryCustom.findWeeklyScheduleByUser(startDate, endDate, user);

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
            throw new BusinessException(ErrorCode.UNAUTHORIZED_SCHEDULE);
        }

        schedule.update(schedule.getTitle(), schedule.getContent(), schedule.getStartTime(), schedule.getEndTime());

        return ResponseSchedule.from(schedule);
    }

    /*
    그룹룸 스케줄관련 코드
    */
    public List<ResponseSchedule> getSchedulesByGroupRoom(Long groupRoomId, UserInfo userInfo) {
        // 유저가 그룹룸에 접근할 권리가있는지 확인
        if (!userGroupMembershipRepositoryCustomImpl.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 그룹룸 ID를 기반으로 스케줄을 조회
        List<Schedule> schedules = scheduleRepository.findByGroupRoomId(groupRoomId);

        // 조회된 스케줄 리스트를 ResponseSchedule DTO로 변환
        return schedules.stream()
                .map(ResponseSchedule::from)
                .collect(Collectors.toList());
    }

    public ResponseSchedule updateScheduleByGroupRoom(Long groupRoomId, Long scheduleId, RequestSchedule requestSchedule, UserInfo userInfo) {
        if (!userGroupMembershipRepositoryCustomImpl.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        Schedule schedule = findScheduleById(scheduleId);
        schedule.update(requestSchedule.getTitle(), requestSchedule.getContent(), requestSchedule.getStartTime(), requestSchedule.getEndTime());

        return ResponseSchedule.from(schedule);
    }

    public void deleteScheduleByGroupRoom(Long groupRoomId, Long scheduleId, UserInfo userInfo) {
        if (!userGroupMembershipRepositoryCustomImpl.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        scheduleRepository.deleteById(scheduleId);
    }

    private User validateUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Schedule findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));
    }

}
