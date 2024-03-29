package com.tukorea.planding.schedule.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.schedule.dto.RequestSchedule;
import com.tukorea.planding.schedule.dto.ResponseSchedule;
import com.tukorea.planding.schedule.service.ScheduleService;
import com.tukorea.planding.user.dto.UserInfo;
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
    @Operation(summary = "스케줄 생성")
    @PostMapping()
    public CommonResponse<ResponseSchedule> createSchedule(@AuthenticationPrincipal UserInfo userInfo, @RequestBody RequestSchedule requestSchedule) {
        ResponseSchedule response = scheduleService.createSchedule(userInfo, requestSchedule);
        return CommonUtils.success(response);
    }
    @Operation(summary = "스케줄 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseSchedule> deleteSchedule(@AuthenticationPrincipal UserInfo userInfo, @PathVariable Long id) {
        scheduleService.deleteSchedule(userInfo, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Operation(summary = "개인 스케줄 하루치 날짜로 가져오기")
    @GetMapping("/{date}")
    public CommonResponse<List<ResponseSchedule>> getSchedule(@PathVariable(name = "date") LocalDate date, @AuthenticationPrincipal UserInfo userInfo) {
        List<ResponseSchedule> responseSchedule = scheduleService.getSchedule(date, userInfo);
        return CommonUtils.success(responseSchedule);
    }
    @Operation(summary = "스케줄 title,content,startTime,endTime 수정 (* 수정필요)")
    @PatchMapping("/{schedule_id}")
    public CommonResponse<ResponseSchedule> updateSchedule(@PathVariable(name = "schedule_id")Long id, @RequestBody RequestSchedule requestSchedule, @AuthenticationPrincipal UserInfo userInfo){
        ResponseSchedule responseSchedule=scheduleService.updateSchedule(id,requestSchedule,userInfo);
        return CommonUtils.success(responseSchedule);
    }

}
