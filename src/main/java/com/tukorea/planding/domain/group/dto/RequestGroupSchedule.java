package com.tukorea.planding.domain.group.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Builder
public class RequestGroupSchedule {
    private Long userId;
    private String title;
    private String content;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
