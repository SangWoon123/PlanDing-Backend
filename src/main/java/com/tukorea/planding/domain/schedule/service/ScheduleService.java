package com.tukorea.planding.domain.schedule.service;

import com.tukorea.planding.domain.group.service.UserGroupService;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.service.UserQueryService;
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

    private final ScheduleQueryService scheduleQueryService;
    private final UserGroupService userGroupService;
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
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);
        schedule.checkOwnership(userInfo.getId());
        return ScheduleResponse.from(schedule);
    }

    public void deleteSchedule(UserInfo userInfo, Long scheduleId) {
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);
        schedule.checkOwnership(userInfo.getId());
        scheduleQueryService.delete(schedule);
    }

    public List<ScheduleResponse> getWeekSchedule(LocalDate startDate, LocalDate endDate, UserInfo userInfo) {
        return scheduleQueryService.findWeeklyScheduleByUser(startDate, endDate, userInfo.getId())
                .stream()
                .map(ScheduleResponse::from)
                .sorted(ScheduleResponse.getComparatorByStartTime())
                .collect(Collectors.toList());
    }

    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleRequest scheduleRequest, UserInfo userInfo) {
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);
        schedule.checkOwnership(userInfo.getId());
        schedule.update(scheduleRequest.title(), scheduleRequest.content(), scheduleRequest.startTime(), scheduleRequest.endTime());
        return ScheduleResponse.from(schedule);
    }

    /*
    그룹룸 스케줄관련 코드

    */
    public ScheduleResponse getGroupSchedule(Long groupRoomId, Long scheduleId, UserInfo userInfo) {
        checkUserAccessToGroupRoom(groupRoomId, userInfo.getId());
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);
        return ScheduleResponse.from(schedule);
    }

    public List<ScheduleResponse> getSchedulesByGroupRoom(Long groupRoomId, UserInfo userInfo) {
        // [1] 유저가 그룹룸에 접근할 권리가있는지 확인
        checkUserAccessToGroupRoom(groupRoomId, userInfo.getId());
        // [2] 그룹룸 ID를 기반으로 스케줄을 조회
        List<Schedule> schedules = scheduleQueryService.findByGroupRoomId(groupRoomId);
        // [3] dto 반환
        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    public ScheduleResponse updateScheduleByGroupRoom(Long groupRoomId, Long scheduleId, ScheduleRequest scheduleRequest, UserInfo userInfo) {
        // [1] 그룹룸에 수정하려는 유저가 존재하는지 확인
        checkUserAccessToGroupRoom(groupRoomId, userInfo.getId());
        // [2] 스케줄을 업데이트
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);
        schedule.update(scheduleRequest.title(), scheduleRequest.content(), scheduleRequest.startTime(), scheduleRequest.endTime());
        // [3] dto 반환
        return ScheduleResponse.from(schedule);
    }


    public void deleteScheduleByGroupRoom(Long groupRoomId, Long scheduleId, UserInfo userInfo) {
        checkUserAccessToGroupRoom(groupRoomId, userInfo.getId());
        scheduleQueryService.deleteById(scheduleId);
    }

    public List<ScheduleResponse> findOverlapSchedule(Long userId, ScheduleRequest scheduleRequest) {
        return scheduleQueryService.findOverlapSchedule(userId, scheduleRequest);
    }

    public List<ScheduleResponse> showTodaySchedule(UserInfo userInfo) {
        return scheduleQueryService.showTodaySchedule(userInfo.getId())
                .stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    private void checkUserAccessToGroupRoom(Long groupRoomId, Long userId) {
        userGroupService.checkUserAccessToGroupRoom(groupRoomId, userId);
    }

}
