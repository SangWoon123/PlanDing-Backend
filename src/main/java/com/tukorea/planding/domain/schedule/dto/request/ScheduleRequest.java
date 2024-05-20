package com.tukorea.planding.domain.schedule.dto.request;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record ScheduleRequest(
        Long userId,
        String title,
        String content,
        LocalDate scheduleDate,
        LocalTime startTime,
        LocalTime endTime
) {

}
