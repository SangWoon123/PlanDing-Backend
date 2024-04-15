package com.tukorea.planding.domain.schedule.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.schedule.entity.ScheduleStatus;
import com.tukorea.planding.domain.schedule.service.ScheduleService;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.schedule.dto.ScheduleRequest;
import com.tukorea.planding.domain.schedule.dto.ScheduleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Schedule", description = "스케줄 CRUD")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "공통: 스케쥴 생성을 할 때 내가 포함된 모든 그룹, 개인 시간때에 스케쥴을 확인")
    @PostMapping("/overlap")
    public CommonResponse<List<ScheduleResponse>> findOverlapSchedule(@AuthenticationPrincipal UserInfo userInfo, @RequestBody ScheduleRequest scheduleRequest) {
        List<ScheduleResponse> scheduleResponses = scheduleService.findOverlapSchedule(userInfo.getId(), scheduleRequest);
        return CommonUtils.success(scheduleResponses);
    }

    @Operation(summary = "개인 스케줄: 생성")
    @PostMapping()
    public CommonResponse<ScheduleResponse> createSchedule(@AuthenticationPrincipal UserInfo userInfo, @RequestBody ScheduleRequest scheduleRequest) {
        ScheduleResponse response = scheduleService.createSchedule(userInfo, scheduleRequest);
        return CommonUtils.success(response);
    }

    @Operation(summary = "개인 스케줄: 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ScheduleResponse> deleteSchedule(@AuthenticationPrincipal UserInfo userInfo, @PathVariable Long id) {
        scheduleService.deleteSchedule(userInfo, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "개인 스케줄: id 값으로 가져오기")
    @GetMapping("/{schedule_id}")
    public CommonResponse<ScheduleResponse> getScheduleById(@PathVariable(name = "schedule_id") Long id, @AuthenticationPrincipal UserInfo userInfo) {
        ScheduleResponse scheduleResponse = scheduleService.getSchedule(id, userInfo);
        return CommonUtils.success(scheduleResponse);
    }

    @Operation(summary = "개인 스케줄: 주간으로 가져오기")
    @GetMapping("/week/{startDate}/{endDate}")
    public CommonResponse<List<ScheduleResponse>> getWeekSchedule(@PathVariable(name = "startDate") LocalDate startDate,
                                                                  @PathVariable(name = "endDate") LocalDate endDate
            , @AuthenticationPrincipal UserInfo userInfo) {
        List<ScheduleResponse> scheduleResponse = scheduleService.getWeekSchedule(startDate, endDate, userInfo);
        return CommonUtils.success(scheduleResponse);
    }

    //TODO 아직 요구사항 미정
    @Operation(summary = "개인 스케줄: 제목,내용,시작시간,끝낼시간 항목 수정")
    @PatchMapping("/{schedule_id}")
    public CommonResponse<ScheduleResponse> updateSchedule(@PathVariable(name = "schedule_id") Long id, @RequestBody ScheduleRequest scheduleRequest, @AuthenticationPrincipal UserInfo userInfo) {
        ScheduleResponse scheduleResponse = scheduleService.updateSchedule(id, scheduleRequest, userInfo);
        return CommonUtils.success(scheduleResponse);
    }

    @Operation(summary = "개인 스케줄: 스케줄 상태 변환")
    @PatchMapping("/{schedule_id}/status")
    public CommonResponse<ScheduleResponse> updateScheduleStatus(@PathVariable(name = "schedule_id") Long id, @RequestBody ScheduleStatus scheduleStatus) {
        ScheduleResponse scheduleResponse = scheduleService.updateScheduleStatus(id, scheduleStatus);
        return CommonUtils.success(scheduleResponse);
    }

    /*
       그룹룸 스케줄관련 코드
    */
    @Operation(summary = "그룹 스케줄: 작성 목록 조회")
    @GetMapping("/groupRoom/{groupRoomId}")
    public CommonResponse<List<ScheduleResponse>> getSchedulesByGroupRoom(@PathVariable Long groupRoomId, @AuthenticationPrincipal UserInfo userInfo) {
        List<ScheduleResponse> scheduleResponses = scheduleService.getSchedulesByGroupRoom(groupRoomId, userInfo);
        return CommonUtils.success(scheduleResponses);
    }

    @Operation(summary = "그룹 스케줄: 스케줄 수정")
    @PatchMapping("/groupRoom/{groupRoomId}/{scheduleId}")
    public CommonResponse<ScheduleResponse> updateScheduleByGroupRoom(@PathVariable Long groupRoomId, @PathVariable Long scheduleId, @RequestBody ScheduleRequest scheduleRequest, @AuthenticationPrincipal UserInfo userInfo) {
        ScheduleResponse scheduleResponse = scheduleService.updateScheduleByGroupRoom(groupRoomId, scheduleId, scheduleRequest, userInfo);
        return CommonUtils.success(scheduleResponse);
    }

    @Operation(summary = "그룹 스케줄: 스케줄 삭제")
    @DeleteMapping("/groupRoom/{groupRoomId}/{scheduleId}")
    public ResponseEntity<ScheduleResponse> deleteScheduleByGroupRoom(@PathVariable Long groupRoomId, @PathVariable Long scheduleId, @AuthenticationPrincipal UserInfo userInfo) {
        scheduleService.deleteScheduleByGroupRoom(groupRoomId, scheduleId, userInfo);
        return new ResponseEntity<>(HttpStatus.OK);

    }
}
