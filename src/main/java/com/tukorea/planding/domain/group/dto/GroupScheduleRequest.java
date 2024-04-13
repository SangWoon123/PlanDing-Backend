package com.tukorea.planding.domain.group.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;


@Builder
public record GroupScheduleRequest(
         Long userId,
         String title,
         String content,
         LocalDate scheduleDate,
         LocalTime startTime,
         LocalTime endTime) {

}
