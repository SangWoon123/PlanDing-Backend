package com.tukorea.planding.domain.schedule.service;

import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.repository.usergroup.UserGroupRepository;
import com.tukorea.planding.domain.group.service.query.GroupQueryService;
import com.tukorea.planding.domain.group.service.query.UserGroupQueryService;
import com.tukorea.planding.domain.notify.dto.NotificationScheduleRequest;
import com.tukorea.planding.domain.notify.entity.NotificationType;
import com.tukorea.planding.domain.notify.service.NotificationService;
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
    private final UserQueryService userQueryService;
    private final UserGroupRepository userGroupRepository;

    private final GroupScheduleRepository groupScheduleRepository;
    private final GroupScheduleAttendanceRepository groupScheduleAttendanceRepository;
    private final UserGroupQueryService userGroupQueryService;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public ScheduleResponse createGroupSchedule(String groupCode, ScheduleRequest requestSchedule) {
        User user = userQueryService.getUserByUserCode(requestSchedule.userCode());
        GroupRoom groupRoom = groupQueryService.getGroupByCode(groupCode);

        userGroupQueryService.checkUserAccessToGroupRoom(groupRoom.getId(), user.getId());

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

        List<User> notificationUsers = userGroupRepository.findUserByIsConnectionFalse(groupRoom.getId());

        notificationUsers.forEach(member -> {
            GroupScheduleCreatedEvent event = new GroupScheduleCreatedEvent(this, member.getUserCode(), groupRoom.getName(), savedSchedule.getTitle(), "/groupRoom/" + groupRoom.getId() + "/" + savedSchedule.getId());
            eventPublisher.publishEvent(event);
        });

        return ScheduleResponse.from(savedSchedule);
    }

    /*
    그룹룸 스케줄관련 코드

    */

    // 그룹룸에서 스케줄을 찾아
    // 스케줄과 유저의 참여여부를 비교
    // 현재 쿼리 8번
    // 유저 , 스케줄, 그룹, 그룹유저, 참여여부 2명 조회
    @Transactional(readOnly = true)
    public GroupScheduleResponse getGroupScheduleById(UserInfo userInfo, Long groupRoomId, Long scheduleId) {
        checkUserAccessToGroupRoom(groupRoomId, userInfo.getId());

        GroupRoom groupRoom = groupQueryService.getGroupById(groupRoomId);
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);

        List<UserScheduleAttendance> attendances = getUserScheduleAttendances(groupRoom, scheduleId);

        return GroupScheduleResponse.from(schedule, groupRoom.getName(), attendances);
    }

    @Transactional(readOnly = true)
    public List<GroupScheduleResponse> getSchedulesByGroupRoom(Long groupRoomId, UserInfo userInfo) {
        checkUserAccessToGroupRoom(groupRoomId, userInfo.getId());
        List<Schedule> schedules = scheduleQueryService.findByGroupRoomId(groupRoomId);
        GroupRoom groupRoom = groupQueryService.getGroupById(groupRoomId);

        return schedules.stream()
                .map(schedule -> GroupScheduleResponse.from(schedule, groupRoom.getName(), getUserScheduleAttendances(groupRoom, schedule.getId())))
                .collect(Collectors.toList());
    }

    public ScheduleResponse updateScheduleByGroupRoom(Long groupRoomId, Long scheduleId, ScheduleRequest scheduleRequest, UserInfo userInfo) {
        checkUserAccessToGroupRoom(groupRoomId, userInfo.getId());

        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);
        schedule.update(scheduleRequest.title(), scheduleRequest.content(), scheduleRequest.startTime(), scheduleRequest.endTime());

        return ScheduleResponse.from(schedule);
    }


    public void deleteScheduleByGroupRoom(Long groupRoomId, Long scheduleId, UserInfo userInfo) {
        checkUserAccessToGroupRoom(groupRoomId, userInfo.getId());
        scheduleQueryService.deleteById(scheduleId);
    }

    // 유저가 그룹룸에 접근할 권리가있는지 확인
    private void checkUserAccessToGroupRoom(Long groupRoomId, Long userId) {
        userGroupQueryService.checkUserAccessToGroupRoom(groupRoomId, userId);
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


}
