package com.tukorea.planding.schedule.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalTime;

@Getter
@Builder
public class RequestSchedule {
    private String title;
    private String content;
    private LocalTime startTime;
    private LocalTime endTime;
}
