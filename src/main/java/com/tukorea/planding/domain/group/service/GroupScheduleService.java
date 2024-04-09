package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.dto.RequestGroupSchedule;
import com.tukorea.planding.domain.group.entity.GroupRoom;import com.tukorea.planding.domain.group.repository.GroupRoomRepository;import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.repository.UserRepository;import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import com.tukorea.planding.domain.schedule.repository.ScheduleRepository;
import com.tukorea.planding.domain.schedule.dto.ResponseSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupRoomRepository groupRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseSchedule createGroupSchedule(String groupCode, RequestGroupSchedule requestSchedule) {

        User user = userRepository.findById(requestSchedule.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        GroupRoom groupRoom = groupRoomRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_ROOM_NOT_FOUND));

        Schedule schedule = Schedule.builder()
                .title(requestSchedule.getTitle())
                .content(requestSchedule.getContent())
                .scheduleDate(requestSchedule.getScheduleDate())
                .startTime(requestSchedule.getStartTime())
                .endTime(requestSchedule.getEndTime())
                .isComplete(false)
                .groupRoom(groupRoom)
                .build();

        schedule.addUser(user);

        groupRoom.addSchedule(schedule);
        // 그룹 스케줄을 데이터베이스에 저장
        Schedule save = scheduleRepository.save(schedule);

        return ResponseSchedule.from(save);
    }
}
