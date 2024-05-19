package com.tukorea.planding.domain.schedule.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.schedule.dto.response.PersonalScheduleResponse;
import com.tukorea.planding.domain.schedule.service.PersonalScheduleService;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.schedule.dto.request.ScheduleRequest;
import com.tukorea.planding.domain.schedule.dto.response.ScheduleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "PersonalSchedule", description = "개인 스케줄")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class PersonalScheduleController {

    private final PersonalScheduleService personalScheduleService;

    @Operation(summary = "개인 스케줄: 생성")
    @PostMapping()
    public CommonResponse<PersonalScheduleResponse> createSchedule(@AuthenticationPrincipal UserInfo userInfo, @RequestBody ScheduleRequest scheduleRequest) {
        PersonalScheduleResponse response = personalScheduleService.createSchedule(userInfo, scheduleRequest);
        return CommonUtils.success(response);
    }

    @Operation(summary = "개인 스케줄: 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<PersonalScheduleResponse> deleteSchedule(@AuthenticationPrincipal UserInfo userInfo, @PathVariable Long id) {
        personalScheduleService.deleteSchedule(userInfo, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "개인 스케줄: 조회")
    @GetMapping("/{schedule_id}")
    public CommonResponse<PersonalScheduleResponse> getScheduleById(@PathVariable(name = "schedule_id") Long id, @AuthenticationPrincipal UserInfo userInfo) {
        PersonalScheduleResponse scheduleResponse = personalScheduleService.getSchedule(id, userInfo);
        return CommonUtils.success(scheduleResponse);
    }

    //TODO 아직 요구사항 미정
    @Operation(summary = "개인 스케줄: 제목,내용,시작시간,끝낼시간 항목 수정")
    @PatchMapping("/{schedule_id}")
    public CommonResponse<PersonalScheduleResponse> updateSchedule(@PathVariable(name = "schedule_id") Long id, @RequestBody ScheduleRequest scheduleRequest, @AuthenticationPrincipal UserInfo userInfo) {
        PersonalScheduleResponse scheduleResponse = personalScheduleService.updateSchedule(id, scheduleRequest, userInfo);
        return CommonUtils.success(scheduleResponse);
    }
}