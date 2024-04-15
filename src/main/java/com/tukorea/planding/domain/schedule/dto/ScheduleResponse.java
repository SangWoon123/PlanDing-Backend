package com.tukorea.planding.domain.schedule.dto;

import com.tukorea.planding.domain.schedule.entity.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;

@Builder
public record ScheduleResponse(
        Long id,
        String title,
        String content,
        LocalDate scheduleDate,
        LocalTime startTime,
        LocalTime endTime,
        boolean complete
) {

    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .scheduleDate(schedule.getScheduleDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .complete(schedule.isComplete())
                .build();
    }

    public static Comparator<ScheduleResponse> getComparatorByStartTime() {
        return Comparator.comparing(schedule -> schedule.startTime());
    }
}
