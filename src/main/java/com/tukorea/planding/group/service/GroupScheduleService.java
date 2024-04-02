package com.tukorea.planding.group.service;

import com.tukorea.planding.group.dao.GroupRoomRepository;
import com.tukorea.planding.group.domain.GroupRoom;
import com.tukorea.planding.schedule.dao.ScheduleRepository;
import com.tukorea.planding.schedule.domain.Schedule;
import com.tukorea.planding.schedule.dto.RequestSchedule;
import com.tukorea.planding.schedule.dto.ResponseSchedule;
import com.tukorea.planding.user.dao.UserRepository;
import com.tukorea.planding.user.domain.User;
import com.tukorea.planding.user.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupRoomRepository groupRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseSchedule createGroupSchedule(String groupCode, RequestSchedule requestSchedule) {

        GroupRoom groupRoom = groupRoomRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new IllegalArgumentException("GroupRoom Not Found"));

        Schedule schedule = Schedule.builder()
                .title(requestSchedule.getTitle())
                .content(requestSchedule.getContent())
                .date(requestSchedule.getDate())
                .startTime(requestSchedule.getStartTime())
                .endTime(requestSchedule.getEndTime())
                .complete(false)
                .groupRoom(groupRoom)
                .build();

        groupRoom.addSchedule(schedule);
        // 그룹 스케줄을 데이터베이스에 저장
        Schedule save = scheduleRepository.save(schedule);

        return ResponseSchedule.from(save);
    }
}
