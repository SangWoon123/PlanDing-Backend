package com.tukorea.planding.domain.schedule.service;

import com.tukorea.planding.domain.group.repository.UserGroupMembershipRepositoryCustomImpl;
import com.tukorea.planding.domain.schedule.entity.ScheduleStatus;
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
import java.time.LocalTime;
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
        // [1] 유저 확인
        User user = validateUserByEmail(userInfo.getEmail());

        // [2] 스케줄 확인
        Schedule schedule = findScheduleById(scheduleId);

        // [3] 유저가 스케줄 업데이트할 수 있는 권한인지 체크
        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_SCHEDULE);
        }

        // [4] 스케줄 업데이트
        schedule.update(requestSchedule.getTitle(), requestSchedule.getContent(), requestSchedule.getStartTime(), requestSchedule.getEndTime());

        return ResponseSchedule.from(schedule);
    }

    /*
    그룹룸 스케줄관련 코드

    */
    public List<ResponseSchedule> getSchedulesByGroupRoom(Long groupRoomId, UserInfo userInfo) {
        // [1] 유저가 그룹룸에 접근할 권리가있는지 확인
        if (!userGroupMembershipRepositoryCustomImpl.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // [2] 그룹룸 ID를 기반으로 스케줄을 조회
        List<Schedule> schedules = scheduleRepository.findByGroupRoomId(groupRoomId);

        // [3] dto 반환
        return schedules.stream()
                .map(ResponseSchedule::from)
                .collect(Collectors.toList());
    }

    public ResponseSchedule updateScheduleByGroupRoom(Long groupRoomId, Long scheduleId, RequestSchedule requestSchedule, UserInfo userInfo) {
        // [1] 그룹룸에 수정하려는 유저가 존재하는지 확인
        if (!userGroupMembershipRepositoryCustomImpl.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // [2] 스케줄을 업데이트
        Schedule schedule = findScheduleById(scheduleId);
        schedule.update(requestSchedule.getTitle(), requestSchedule.getContent(), requestSchedule.getStartTime(), requestSchedule.getEndTime());

        // [3] dto 반환
        return ResponseSchedule.from(schedule);
    }

    public void deleteScheduleByGroupRoom(Long groupRoomId, Long scheduleId, UserInfo userInfo) {
        if (!userGroupMembershipRepositoryCustomImpl.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        scheduleRepository.deleteById(scheduleId);
    }

    public ResponseSchedule updateScheduleStatus(Long scheduleId, ScheduleStatus status) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        switch (status) {
            case POSSIBLE:
                schedule.markAsPossible();
                break;
            case IMPOSSIBLE:
                schedule.markAsImpossible();
                break;
            case UNDECIDED:
                schedule.markAsUndecided();
                break;
            default:
                throw new IllegalArgumentException("Invalid status");
        }
        return ResponseSchedule.from(schedule);
    }

    /*
    공통 로직
     */
    public List<ResponseSchedule> findOverlapSchedule(Long userId, RequestSchedule requestSchedule) {
        List<Schedule> overlapSchedules = scheduleRepositoryCustom.findOverlapSchedules(userId, requestSchedule.getScheduleDate(), requestSchedule.getStartTime(), requestSchedule.getEndTime());
        return overlapSchedules.stream()
                .map(ResponseSchedule::from)
                .collect(Collectors.toList());
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
