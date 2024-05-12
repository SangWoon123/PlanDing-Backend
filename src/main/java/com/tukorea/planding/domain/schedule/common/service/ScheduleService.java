package com.tukorea.planding.domain.schedule.common.service;

import com.tukorea.planding.domain.group.repository.usergroup.UserGroupRepository;
import com.tukorea.planding.domain.group.repository.usergroup.UserGroupRepositoryCustom;
import com.tukorea.planding.domain.schedule.common.repository.ScheduleRepository;
import com.tukorea.planding.domain.schedule.common.repository.ScheduleRepositoryCustomImpl;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.service.UserQueryService;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.schedule.common.dto.ScheduleRequest;
import com.tukorea.planding.domain.schedule.common.dto.ScheduleResponse;
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

    private final ScheduleQueryService scheduleQueryService;
    private final ScheduleRepository scheduleRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserQueryService userQueryService;

    public ScheduleResponse createSchedule(UserInfo userInfo, ScheduleRequest scheduleRequest) {
        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());

        Schedule newSchedule = Schedule.builder()
                .user(user)
                .title(scheduleRequest.title())
                .content(scheduleRequest.content())
                .scheduleDate(scheduleRequest.scheduleDate())
                .startTime(scheduleRequest.startTime())
                .endTime(scheduleRequest.endTime())
                .isComplete(false)
                .build();

        Schedule save = scheduleQueryService.save(newSchedule);

        return ScheduleResponse.from(save);
    }

    public ScheduleResponse getSchedule(Long scheduleId, UserInfo userInfo) {
        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());

        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);

        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_SCHEDULE);
        }

        return ScheduleResponse.from(schedule);
    }

    public void deleteSchedule(UserInfo userInfo, Long scheduleId) {
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);

        User user = schedule.getUser();

        if (!user.getUserCode().equals(userInfo.getUserCode())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_SCHEDULE);
        }

        scheduleQueryService.delete(schedule);
    }

    public List<ScheduleResponse> getWeekSchedule(LocalDate startDate, LocalDate endDate, UserInfo userInfo) {
        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());

        List<Schedule> schedules = scheduleRepository.findWeeklyScheduleByUser(startDate, endDate, user);

        List<ScheduleResponse> scheduleResponses = schedules.stream()
                .map(ScheduleResponse::from)
                .sorted(ScheduleResponse.getComparatorByStartTime())
                .collect(Collectors.toList());

        return scheduleResponses;
    }

    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleRequest scheduleRequest, UserInfo userInfo) {
        // [1] 유저 확인
        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());

        // [2] 스케줄 확인
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);

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
    public ScheduleResponse getGroupSchedule(Long groupRoomId, Long scheduleId, UserInfo userInfo) {
        if (!userGroupRepository.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);

        return ScheduleResponse.from(schedule);
    }

    public List<ScheduleResponse> getSchedulesByGroupRoom(Long groupRoomId, UserInfo userInfo) {
        // [1] 유저가 그룹룸에 접근할 권리가있는지 확인
        if (!userGroupRepository.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // [2] 그룹룸 ID를 기반으로 스케줄을 조회
        List<Schedule> schedules = scheduleQueryService.findByGroupRoomId(groupRoomId);

        // [3] dto 반환
        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    public ScheduleResponse updateScheduleByGroupRoom(Long groupRoomId, Long scheduleId, ScheduleRequest scheduleRequest, UserInfo userInfo) {
        // [1] 그룹룸에 수정하려는 유저가 존재하는지 확인
        if (!userGroupRepository.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // [2] 스케줄을 업데이트
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);
        schedule.update(scheduleRequest.title(), scheduleRequest.content(), scheduleRequest.startTime(), scheduleRequest.endTime());

        // [3] dto 반환
        return ScheduleResponse.from(schedule);
    }


    public void deleteScheduleByGroupRoom(Long groupRoomId, Long scheduleId, UserInfo userInfo) {
        if (!userGroupRepository.existsByGroupRoomIdAndUserId(groupRoomId, userInfo.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        scheduleQueryService.deleteById(scheduleId);
    }

    public List<ScheduleResponse> findOverlapSchedule(Long userId, ScheduleRequest scheduleRequest) {
        return scheduleQueryService.findOverlapSchedule(userId, scheduleRequest);
    }


}
