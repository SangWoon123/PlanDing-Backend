package com.tukorea.planding.domain.schedule.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.schedule.entity.ScheduleStatus;
import com.tukorea.planding.domain.schedule.service.ScheduleService;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.schedule.dto.RequestSchedule;
import com.tukorea.planding.domain.schedule.dto.ResponseSchedule;
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

    @Operation(summary = "개인 스케줄: 생성")
    @PostMapping()
    public CommonResponse<ResponseSchedule> createSchedule(@AuthenticationPrincipal UserInfo userInfo, @RequestBody RequestSchedule requestSchedule) {
        ResponseSchedule response = scheduleService.createSchedule(userInfo, requestSchedule);
        return CommonUtils.success(response);
    }

    @Operation(summary = "개인 스케줄: 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseSchedule> deleteSchedule(@AuthenticationPrincipal UserInfo userInfo, @PathVariable Long id) {
        scheduleService.deleteSchedule(userInfo, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "개인 스케줄: 주간으로 가져오기")
    @GetMapping("/week/{startDate}/{endDate}")
    public CommonResponse<List<ResponseSchedule>> getWeekSchedule(@PathVariable(name = "startDate") LocalDate startDate,
                                                                  @PathVariable(name = "endDate") LocalDate endDate
            , @AuthenticationPrincipal UserInfo userInfo) {
        List<ResponseSchedule> responseSchedule = scheduleService.getWeekSchedule(startDate, endDate, userInfo);
        return CommonUtils.success(responseSchedule);
    }

    @Operation(summary = "개인 스케줄: 제목,내용,시작시간,끝낼시간 항목 수정 (* 수정필요)")
    @PatchMapping("/{schedule_id}")
    public CommonResponse<ResponseSchedule> updateSchedule(@PathVariable(name = "schedule_id") Long id, @RequestBody RequestSchedule requestSchedule, @AuthenticationPrincipal UserInfo userInfo) {
        ResponseSchedule responseSchedule = scheduleService.updateSchedule(id, requestSchedule, userInfo);
        return CommonUtils.success(responseSchedule);
    }

    @Operation(summary = "개인 스케줄: 스케줄 상태 변환")
    @PatchMapping("/{schedule_id}/status")
    public CommonResponse<ResponseSchedule> updateScheduleStatus(@PathVariable(name = "schedule_id") Long id, @RequestBody ScheduleStatus scheduleStatus) {
        ResponseSchedule responseSchedule = scheduleService.updateScheduleStatus(id, scheduleStatus);
        return CommonUtils.success(responseSchedule);
    }

    /*
       그룹룸 스케줄관련 코드
    */
    @Operation(summary = "그룹 스케줄: 작성 목록 조회")
    @GetMapping("/groupRoom/{groupRoomId}")
    public CommonResponse<List<ResponseSchedule>> getSchedulesByGroupRoom(@PathVariable Long groupRoomId, @AuthenticationPrincipal UserInfo userInfo) {
        List<ResponseSchedule> responseSchedules = scheduleService.getSchedulesByGroupRoom(groupRoomId, userInfo);
        return CommonUtils.success(responseSchedules);
    }

    @Operation(summary = "그룹 스케줄: 스케줄 수정")
    @PatchMapping("/groupRoom/{groupRoomId}/{scheduleId}")
    public CommonResponse<ResponseSchedule> updateScheduleByGroupRoom(@PathVariable Long groupRoomId, @PathVariable Long scheduleId, @RequestBody RequestSchedule requestSchedule, @AuthenticationPrincipal UserInfo userInfo) {
        ResponseSchedule responseSchedule = scheduleService.updateScheduleByGroupRoom(groupRoomId, scheduleId, requestSchedule, userInfo);
        return CommonUtils.success(responseSchedule);
    }

    @Operation(summary = "그룹 스케줄: 스케줄 삭제")
    @DeleteMapping("/groupRoom/{groupRoomId}/{scheduleId}")
    public ResponseEntity<ResponseSchedule> deleteScheduleByGroupRoom(@PathVariable Long groupRoomId, @PathVariable Long scheduleId, @AuthenticationPrincipal UserInfo userInfo) {
        scheduleService.deleteScheduleByGroupRoom(groupRoomId, scheduleId, userInfo);
        return new ResponseEntity<>(HttpStatus.OK);

    }
}
