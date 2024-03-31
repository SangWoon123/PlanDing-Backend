package com.tukorea.planding.group.service;

import com.tukorea.planding.group.dao.GroupRoomRepository;
import com.tukorea.planding.group.domain.GroupRoom;
import com.tukorea.planding.schedule.dao.ScheduleRepository;
import com.tukorea.planding.schedule.domain.Schedule;
import com.tukorea.planding.schedule.dto.ResponseSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupRoomRepository groupRoomRepository;

    public ResponseSchedule createGroupSchedule(String groupCode, Schedule groupSchedule) {

        GroupRoom groupRoom = groupRoomRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new IllegalArgumentException("GroupRoom Not Found"));

        groupRoom.addSchedule(groupSchedule);
        // 그룹 스케줄을 데이터베이스에 저장
        Schedule save = scheduleRepository.save(groupSchedule);

        return ResponseSchedule.from(save);
    }

}
