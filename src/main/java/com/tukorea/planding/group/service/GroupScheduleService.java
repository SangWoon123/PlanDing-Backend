package com.tukorea.planding.group.service;

import com.tukorea.planding.schedule.dao.ScheduleRepository;
import com.tukorea.planding.schedule.domain.Schedule;
import com.tukorea.planding.schedule.dto.ResponseSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ResponseSchedule createGroupSchedule(Schedule groupSchedule) {
        // 그룹 스케줄을 데이터베이스에 저장
        Schedule save = scheduleRepository.save(groupSchedule);
        return ResponseSchedule.from(save);
    }




}
