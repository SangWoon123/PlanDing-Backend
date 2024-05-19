package com.tukorea.planding.domain.schedule.dto.response;

import com.tukorea.planding.domain.schedule.entity.ScheduleStatus;

public record UserScheduleAttendance(
        String userCode,
        String userName,
        ScheduleStatus status
) {

}
