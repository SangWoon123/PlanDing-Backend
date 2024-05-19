package com.tukorea.planding.domain.schedule.dto.response;

import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.schedule.entity.ScheduleType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;

@Builder
public record PersonalScheduleResponse(
        Long id,
        String title,
        String content,
        LocalDate scheduleDate,
        LocalTime startTime,
        LocalTime endTime,
        boolean complete,
        ScheduleType type
) {

    public static PersonalScheduleResponse from(Schedule schedule) {
        return PersonalScheduleResponse.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .scheduleDate(schedule.getScheduleDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .complete(schedule.isComplete())
                .type(ScheduleType.PERSONAL)
                .build();
    }


    public static Comparator<PersonalScheduleResponse> getComparatorByStartTime() {
        return Comparator.comparing(schedule -> schedule.startTime());
    }
}
