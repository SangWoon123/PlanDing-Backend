package com.tukorea.planding.domain.schedule.dto.request;

import com.tukorea.planding.global.valid.schedule.ValidScheduleTime;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@ValidScheduleTime
public record GroupScheduleRequest(
        String userCode,
        String title,
        String content,
        @NotNull(message = "스케줄 날짜를 입력해 주세요.")
        LocalDate scheduleDate,
        Integer startTime,
        Integer endTime
) {
}
