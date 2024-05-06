package com.tukorea.planding.domain.schedule.dto;

import com.tukorea.planding.domain.schedule.entity.ScheduleStatus;
import lombok.Builder;

@Builder
public record GroupScheduleAttendanceRequest(
        ScheduleStatus status,
        Long scheduleId
) {
}
