package com.tukorea.planding.domain.schedule.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record RequestSchedule(
        String title,
        String content,
        LocalDate scheduleDate,
        LocalTime startTime,
        LocalTime endTime
) {

}
