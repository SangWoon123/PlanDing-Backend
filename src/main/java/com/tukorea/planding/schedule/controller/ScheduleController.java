package com.tukorea.planding.schedule.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.schedule.dto.RequestSchedule;
import com.tukorea.planding.schedule.dto.ResponseSchedule;
import com.tukorea.planding.schedule.service.ScheduleService;
import com.tukorea.planding.user.dto.UserInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Schedule", description = "스케줄 CRUD")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping()
    public CommonResponse<ResponseSchedule> createSchedule(@AuthenticationPrincipal UserInfo userInfo, @RequestBody RequestSchedule requestSchedule) {
        ResponseSchedule response = scheduleService.createSchedule(userInfo, requestSchedule);
        return CommonUtils.success(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseSchedule> deleteSchedule(@AuthenticationPrincipal UserInfo userInfo,@PathVariable Long id){
        scheduleService.deleteSchedule(userInfo,id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
