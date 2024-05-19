package com.tukorea.planding.domain.schedule.service;

import com.tukorea.planding.domain.group.service.UserGroupService;
import com.tukorea.planding.domain.group.service.query.UserGroupQueryService;
import com.tukorea.planding.domain.schedule.dto.response.GroupScheduleResponse;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.entity.UserGroup;
import com.tukorea.planding.domain.group.repository.usergroup.UserGroupRepository;
import com.tukorea.planding.domain.group.service.query.GroupQueryService;
import com.tukorea.planding.domain.notify.dto.NotificationScheduleRequest;
import com.tukorea.planding.domain.notify.entity.NotificationType;
import com.tukorea.planding.domain.notify.service.NotificationService;
import com.tukorea.planding.domain.schedule.dto.request.ScheduleRequest;
import com.tukorea.planding.domain.schedule.dto.response.UserScheduleAttendance;
import com.tukorea.planding.domain.schedule.entity.*;
import com.tukorea.planding.domain.schedule.repository.GroupScheduleAttendanceRepository;
import com.tukorea.planding.domain.schedule.repository.GroupScheduleRepository;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import com.tukorea.planding.domain.schedule.dto.response.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tukorea.planding.domain.group.entity.QGroupRoom.groupRoom;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupScheduleService {

    private final ScheduleQueryService scheduleQueryService;
    private final GroupQueryService groupQueryService;
    private final UserQueryService userQueryService;
    private final NotificationService notificationService;
    private final UserGroupRepository userGroupRepository;

    private final GroupScheduleRepository groupScheduleRepository;
    private final GroupScheduleAttendanceRepository groupScheduleAttendanceRepository;
    private final UserGroupQueryService userGroupQueryService;

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
        String createUrl = groupRoom.getGroupCode() + "/schedule/" + savedSchedule.getId();

        for (User member : notificationUsers) {
            NotificationScheduleRequest request = NotificationScheduleRequest.builder()
                    .receiverCode(member.getUserCode())
                    .createdAt(LocalDateTime.now())
                    .message(savedSchedule.getTitle())
                    .groupName(groupRoom.getName())
                    .type(NotificationType.GROUP_SCHEDULE)
                    .url(createUrl)
                    .build();
            notificationService.send(request);
        }

        return ScheduleResponse.from(savedSchedule);
    }

    /*
    그룹룸 스케줄관련 코드

    */

    // 그룹룸에서 스케줄을 찾아
    // 스케줄과 유저의 참여여부를 비교
    // 현재 쿼리 8번
    // 유저 , 스케줄, 그룹, 그룹유저, 참여여부 2명 조회
    public GroupScheduleResponse getGroupScheduleById(UserInfo userInfo, Long groupRoomId, Long scheduleId) {
        // 유저 프로필을 조회합니다.
        User user = userQueryService.getUserProfile(userInfo.getId());

        // 해당 스케줄의 그룹룸을 가져옵니다.
        GroupRoom groupRoom = groupQueryService.getGroupById(groupRoomId);

        // 스케줄을 조회합니다.
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);

        // 그룹룸에 속한 유저 그룹들의 목록을 가져옵니다.
        Set<UserGroup> userGroups = groupRoom.getUserGroups();

        // 유저가 해당 그룹에 속해 있는지 확인합니다.
        boolean isUserInGroup = userGroups.stream()
                .anyMatch(userGroup -> userGroup.getUser().getId().equals(userInfo.getId()));

        if (!isUserInGroup) {
            // 유저가 그룹에 속해 있지 않다면, 예외 처리를 합니다.
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 유저가 그룹에 속해 있다면, 필요한 스케줄 정보를 반환합니다.
        // GroupScheduleResponse 구성 로직은 여기에 추가합니다.
        return GroupScheduleResponse.from(schedule, groupRoom.getName(), getUserScheduleAttendances(groupRoom, userInfo.getId(), scheduleId));
    }

    private List<UserScheduleAttendance> getUserScheduleAttendances(GroupRoom groupRoom, Long userId, Long scheduleId) {
        return groupRoom.getUserGroups().stream()
                .map(userGroup -> {
                    // 실제 출석 상태를 데이터베이스에서 조회하거나 다른 로직을 통해 결정합니다.
                    Optional<GroupScheduleAttendance> attendance = groupScheduleAttendanceRepository.findByUserIdAndScheduleIdAndStatusNot(userId, scheduleId, ScheduleStatus.UNDECIDED);

                    ScheduleStatus status = attendance
                            .map(GroupScheduleAttendance::getStatus)
                            .orElse(ScheduleStatus.UNDECIDED);

                    return new UserScheduleAttendance(
                            userGroup.getUser().getUserCode(),
                            userGroup.getUser().getUsername(),
                            status
                    );
                })
                // UNDECIDED인 상태 제외
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<GroupScheduleResponse> getSchedulesByGroupRoom(Long groupRoomId, UserInfo userInfo) {
        // [1] 유저가 그룹룸에 접근할 권리가있는지 확인
        checkUserAccessToGroupRoom(groupRoomId, userInfo.getId());
        // [2] 그룹룸 ID를 기반으로 스케줄을 조회
        List<Schedule> schedules = scheduleQueryService.findByGroupRoomId(groupRoomId);
        GroupRoom groupRoom = groupQueryService.getGroupById(groupRoomId);
        // [3] dto 반환
        return scheduleQueryService.findByGroupRoomId(groupRoomId).stream()
                .map(schedule -> {
                    List<UserScheduleAttendance> attendances = getUserScheduleAttendances(groupRoom, userInfo.getId(), schedule.getId());
                    return GroupScheduleResponse.from(schedule, groupRoom.getName(), attendances);
                })
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

    private void checkUserAccessToGroupRoom(Long groupRoomId, Long userId) {
        userGroupQueryService.checkUserAccessToGroupRoom(groupRoomId, userId);
    }


}
