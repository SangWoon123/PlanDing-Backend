package com.tukorea.planding.schedule.dto;

import com.tukorea.planding.schedule.domain.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class ResponseSchedule {

    private Long id;
    private String title;
    private String content;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean complete;

    public static ResponseSchedule from(Schedule schedule) {
        return ResponseSchedule.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .date(schedule.getDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .complete(schedule.isComplete())
                .build();
    }
}
