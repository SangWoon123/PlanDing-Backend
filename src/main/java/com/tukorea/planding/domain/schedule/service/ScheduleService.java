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
import com.tukorea.planding.domain.schedule.dto.ScheduleRequest;
import com.tukorea.planding.domain.schedule.dto.ScheduleResponse;
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

    public ScheduleResponse createSchedule(UserInfo userInfo, ScheduleRequest scheduleRequest) {
        User user = validateUserByUserCode(userInfo.getUserCode());

        Schedule newSchedule = Schedule.builder()
                .user(user)
                .title(scheduleRequest.title())
                .content(scheduleRequest.content())
                .scheduleDate(scheduleRequest.scheduleDate())
                .startTime(scheduleRequest.startTime())
                .endTime(scheduleRequest.endTime())
                .isComplete(false)
                .build();

        Schedule save = scheduleRepository.save(newSchedule);

        return ScheduleResponse.from(save);
    }

    public ScheduleResponse getSchedule(Long scheduleId, UserInfo userInfo) {
        User user = validateUserByUserCode(userInfo.getUserCode());
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_SCHEDULE);
        }

        return ScheduleResponse.from(schedule);
    }

    public void deleteSchedule(UserInfo userInfo, Long scheduleId) {
        Schedule schedule = findScheduleById(scheduleId);
        User user = schedule.getUser();

        if (!user.getUserCode().equals(userInfo.getUserCode())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_SCHEDULE);
        }

        scheduleRepository.delete(schedule);
    }

    public List<ScheduleResponse> getWeekSchedule(LocalDate startDate, LocalDate endDate, UserInfo userInfo) {
        User user = validateUserByUserCode(userInfo.getUserCode());

        List<Schedule> schedules = scheduleRepositoryCustom.findWeeklyScheduleByUser(startDate, endDate, user);

        List<ScheduleResponse> scheduleResponses = schedules.stream()
                .map(ScheduleResponse::from)
                .sorted(ScheduleResponse.getComparatorByStartTime())
                .collect(Collectors.toList());

        return scheduleResponses;
    }

    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleRequest scheduleRequest, UserInfo userInfo) {
        // [1] 유저 확인
        User user = validateUserByUserCode(userInfo.getUserCode());

        // [2] 스케줄 확인
        Schedule schedule = findScheduleById(scheduleId);

        // [3] 유저가 스케줄 업데이트할 수 있는 권한인지 체크
        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_SCHEDULE);
        }

        // [4] 스케줄 업데이트
        schedule.update(scheduleRequest.title(), scheduleRequest.content(), scheduleRequest.startTime(), scheduleRequest.endTime());

        return ScheduleResponse.from(schedule);
    }

    /*
    그룹룸 스케줄관련 코드

    */
    public List<ScheduleResponse> getSchedulesByGroupRoom(Long groupRoomId, UserInfo userInfo) {
        // [1] 유저가 그룹룸에 접근할 권리가있는지 확인
        if (!userGroupMembershipRepositoryCustomImpl.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // [2] 그룹룸 ID를 기반으로 스케줄을 조회
        List<Schedule> schedules = scheduleRepository.findByGroupRoomId(groupRoomId);

        // [3] dto 반환
        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    public ScheduleResponse updateScheduleByGroupRoom(Long groupRoomId, Long scheduleId, ScheduleRequest scheduleRequest, UserInfo userInfo) {
        // [1] 그룹룸에 수정하려는 유저가 존재하는지 확인
        if (!userGroupMembershipRepositoryCustomImpl.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // [2] 스케줄을 업데이트
        Schedule schedule = findScheduleById(scheduleId);
        schedule.update(scheduleRequest.title(), scheduleRequest.content(), scheduleRequest.startTime(), scheduleRequest.endTime());

        // [3] dto 반환
        return ScheduleResponse.from(schedule);
    }

    public void deleteScheduleByGroupRoom(Long groupRoomId, Long scheduleId, UserInfo userInfo) {
        if (!userGroupMembershipRepositoryCustomImpl.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        scheduleRepository.deleteById(scheduleId);
    }

    public ScheduleResponse updateScheduleStatus(Long scheduleId, ScheduleStatus status) {
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
        return ScheduleResponse.from(schedule);
    }

    /*
    공통 로직
     */
    public List<ScheduleResponse> findOverlapSchedule(Long userId, ScheduleRequest scheduleRequest) {
        List<Schedule> overlapSchedules = scheduleRepositoryCustom.findOverlapSchedules(userId, scheduleRequest.scheduleDate(), scheduleRequest.startTime(), scheduleRequest.endTime());
        return overlapSchedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    private User validateUserByUserCode(String userCode) {
        return userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Schedule findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));
    }
}
