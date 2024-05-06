package com.tukorea.planding.domain.schedule.service;

import com.tukorea.planding.domain.schedule.dto.ScheduleRequest;
import com.tukorea.planding.domain.schedule.dto.ScheduleResponse;
import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.schedule.repository.ScheduleRepository;
import com.tukorea.planding.domain.schedule.repository.ScheduleRepositoryCustom;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleQueryService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleRepositoryCustom scheduleRepositoryCustom;


    public List<ScheduleResponse> findOverlapSchedule(Long userId, ScheduleRequest scheduleRequest) {
        List<Schedule> overlapSchedules = scheduleRepositoryCustom.findOverlapSchedules(userId, scheduleRequest.scheduleDate(), scheduleRequest.startTime(), scheduleRequest.endTime());
        return overlapSchedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    public Schedule findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));
    }

    public List<Schedule> findByGroupRoomId(Long groupRoomId) {
        return scheduleRepository.findByGroupRoomId(groupRoomId);
    }

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public void delete(Schedule schedule) {
        scheduleRepository.delete(schedule);
    }

    public void deleteById(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}
