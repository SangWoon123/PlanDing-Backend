package com.tukorea.planding.domain.schedule.service;

import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.repository.GroupRoomRepository;
import com.tukorea.planding.domain.group.repository.UserGroupMembershipRepositoryCustomImpl;
import com.tukorea.planding.domain.notify.dto.NotificationScheduleRequest;
import com.tukorea.planding.domain.notify.entity.NotificationType;
import com.tukorea.planding.domain.notify.service.NotificationService;
import com.tukorea.planding.domain.schedule.dto.ScheduleRequest;
import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import com.tukorea.planding.domain.schedule.repository.ScheduleRepository;
import com.tukorea.planding.domain.schedule.dto.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupRoomRepository groupRoomRepository;
    private final UserQueryService userQueryService;
    private final NotificationService notificationService;
    private final UserGroupMembershipRepositoryCustomImpl userGroupMembershipRepositoryCustom;

    @Transactional
    public ScheduleResponse createGroupSchedule(String groupCode, ScheduleRequest requestSchedule) {

        // [1] 작성한 유저
        User user = userQueryService.getByUserInfo(requestSchedule.userCode());

        // [2] 그룹방
        GroupRoom groupRoom = groupRoomRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_ROOM_NOT_FOUND));

        // [3] 스케줄 생성
        Schedule schedule = Schedule.builder()
                .title(requestSchedule.title())
                .content(requestSchedule.content())
                .scheduleDate(requestSchedule.scheduleDate())
                .startTime(requestSchedule.startTime())
                .endTime(requestSchedule.endTime())
                .isComplete(false)
                .groupRoom(groupRoom)
                .build();

        schedule.addUser(user);
        groupRoom.addSchedule(schedule);

        Schedule save = scheduleRepository.save(schedule);

        // [4] 그룹에 속해있는 유저 중 접속상태가 false인 유저
        List<User> notificationUser = userGroupMembershipRepositoryCustom.findUserByIsConnectionFalse(groupRoom.getId());

        // [5] 그룹 스케줄 생성
        String createUrl = groupRoom.getGroupCode() + "/schedule/" + save.getId(); //TODO 추후 수정


        // [6] 그룹 구성원들에게 스케줄 생성 알림 전송
        for (User member : notificationUser) {
            NotificationScheduleRequest request = NotificationScheduleRequest.builder()
                    .receiverCode(member.getUserCode())
                    .createdAt(LocalDateTime.now())
                    .message(save.getTitle())
                    .groupName(groupRoom.getName())
                    .type(NotificationType.GROUP_SCHEDULE)
                    .url(createUrl)
                    .build();
            notificationService.send(request);
        }

        return ScheduleResponse.from(save);
    }

}
