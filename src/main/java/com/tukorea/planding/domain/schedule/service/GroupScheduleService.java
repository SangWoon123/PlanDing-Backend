package com.tukorea.planding.domain.schedule.service;

import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.repository.usergroup.UserGroupRepository;
import com.tukorea.planding.domain.group.service.query.GroupQueryService;
import com.tukorea.planding.domain.group.service.query.UserGroupQueryService;
import com.tukorea.planding.domain.notify.dto.NotificationScheduleRequest;
import com.tukorea.planding.domain.notify.entity.NotificationType;
import com.tukorea.planding.domain.notify.service.NotificationService;
import com.tukorea.planding.domain.schedule.dto.request.GroupScheduleRequest;
import com.tukorea.planding.domain.schedule.dto.request.ScheduleRequest;
import com.tukorea.planding.domain.schedule.dto.response.GroupScheduleResponse;
import com.tukorea.planding.domain.schedule.dto.response.ScheduleResponse;
import com.tukorea.planding.domain.schedule.dto.response.UserScheduleAttendance;
import com.tukorea.planding.domain.schedule.entity.*;
import com.tukorea.planding.domain.schedule.repository.GroupScheduleAttendanceRepository;
import com.tukorea.planding.domain.schedule.repository.GroupScheduleRepository;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupScheduleService {

    private final ScheduleQueryService scheduleQueryService;
    private final GroupQueryService groupQueryService;
    private final UserGroupQueryService userGroupQueryService;

    private final GroupScheduleRepository groupScheduleRepository;
    private final GroupScheduleAttendanceRepository groupScheduleAttendanceRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public ScheduleResponse createGroupSchedule(String groupCode, ScheduleRequest requestSchedule) {
        GroupRoom groupRoom = groupQueryService.getGroupByCode(groupCode);

        checkUserAccessToGroupRoom(groupRoom.getId(), requestSchedule.userCode());

        GroupSchedule groupSchedule = GroupSchedule.builder()
                .groupRoom(groupRoom)
                .build();

        Schedule newSchedule = Schedule.builder()
                .title(requestSchedule.title())
                .content(requestSchedule.content())
                .scheduleDate(requestSchedule.scheduleDate())
                .startTime(requestSchedule.startTime())
                .endTime(requestSchedule.endTime())
                .isComplete(false)
                .groupSchedule(groupSchedule)
                .type(ScheduleType.GROUP)
                .build();

        groupSchedule.getSchedules().add(newSchedule);
        groupScheduleRepository.save(groupSchedule);
        Schedule savedSchedule = scheduleQueryService.save(newSchedule);

        notifyUsers(groupRoom, savedSchedule);

        return ScheduleResponse.from(savedSchedule);
    }

    @Transactional(readOnly = true)
    public GroupScheduleResponse getGroupScheduleById(UserInfo userInfo, Long groupRoomId, Long scheduleId) {
        checkUserAccessToGroupRoom(groupRoomId, userInfo.getUserCode());

        GroupRoom groupRoom = groupQueryService.getGroupById(groupRoomId);
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);

        List<UserScheduleAttendance> attendances = getUserScheduleAttendances(groupRoom, scheduleId);

        return GroupScheduleResponse.from(schedule, groupRoom.getName(), attendances);
    }

    @Transactional(readOnly = true)
    public List<GroupScheduleResponse> getSchedulesByGroupRoom(Long groupRoomId, UserInfo userInfo) {
        checkUserAccessToGroupRoom(groupRoomId, userInfo.getUserCode());
        List<Schedule> schedules = scheduleQueryService.findByGroupRoomId(groupRoomId);
        GroupRoom groupRoom = groupQueryService.getGroupById(groupRoomId);

        return schedules.stream()
                .map(schedule -> GroupScheduleResponse.from(schedule, groupRoom.getName(), getUserScheduleAttendances(groupRoom, schedule.getId())))
                .collect(Collectors.toList());
    }

    public ScheduleResponse updateScheduleByGroupRoom(Long groupRoomId, Long scheduleId, GroupScheduleRequest groupScheduleRequest, UserInfo userInfo) {
        checkUserAccessToGroupRoom(groupRoomId, userInfo.getUserCode());

        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);
        schedule.update(groupScheduleRequest.title(), groupScheduleRequest.content(), groupScheduleRequest.startTime(), groupScheduleRequest.endTime());

        return ScheduleResponse.from(schedule);
    }


    public void deleteScheduleByGroupRoom(Long groupRoomId, Long scheduleId, UserInfo userInfo) {
        checkUserAccessToGroupRoom(groupRoomId, userInfo.getUserCode());
        scheduleQueryService.deleteById(scheduleId);
    }

    // 유저가 그룹룸에 접근할 권리가있는지 확인
    private void checkUserAccessToGroupRoom(Long groupRoomId, String userCode) {
        if (!userGroupQueryService.checkUserAccessToGroupRoom(groupRoomId, userCode)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }

    private List<UserScheduleAttendance> getUserScheduleAttendances(GroupRoom groupRoom, Long scheduleId) {
        return groupRoom.getUserGroups().stream()
                .map(userGroup -> {
                    Optional<GroupScheduleAttendance> attendance = groupScheduleAttendanceRepository.findByUserIdAndScheduleIdAndStatusNot(userGroup.getUser().getId(), scheduleId, ScheduleStatus.UNDECIDED);
                    ScheduleStatus status = attendance.map(GroupScheduleAttendance::getStatus).orElse(ScheduleStatus.UNDECIDED);
                    return new UserScheduleAttendance(userGroup.getUser().getUserCode(), userGroup.getUser().getUsername(), status);
                })
                .filter(attendance -> attendance.status() != ScheduleStatus.UNDECIDED)
                .collect(Collectors.toList());
    }

    private void notifyUsers(GroupRoom groupRoom, Schedule savedSchedule) {
        List<User> notificationUsers = userGroupQueryService.findUserByIsConnectionFalse(groupRoom.getId());
        notificationUsers.forEach(member -> {
            GroupScheduleCreatedEvent event = new GroupScheduleCreatedEvent(this,
                    member.getUserCode(),
                    groupRoom.getName(),
                    savedSchedule.getTitle(),
                    "/groupRoom/" + groupRoom.getId() + "/" + savedSchedule.getId());
            eventPublisher.publishEvent(event);
        });
    }
}
