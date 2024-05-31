package com.tukorea.planding.domain.schedule.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.schedule.dto.request.GroupScheduleRequest;
import com.tukorea.planding.domain.schedule.dto.response.GroupScheduleResponse;
import com.tukorea.planding.domain.schedule.service.GroupScheduleService;
import com.tukorea.planding.domain.schedule.dto.request.ScheduleRequest;
import com.tukorea.planding.domain.schedule.dto.response.ScheduleResponse;
import com.tukorea.planding.domain.user.dto.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "GroupSchedule", description = "그룹 스케줄")
@RestController
@RequiredArgsConstructor
public class GroupScheduleController {

    private final GroupScheduleService groupScheduleService;


    @MessageMapping("/schedule/{groupCode}") // schedule 경로로 메시지를 보내면
    @SendTo("/sub/schedule/{groupCode}")    // /sub/schedule/{group_code} 을 구독한 유저에게 메시지를 뿌림
    public CommonResponse<ScheduleResponse> createGroupSchedule(@DestinationVariable String groupCode, ScheduleRequest requestSchedule) {
        return CommonUtils.success(groupScheduleService.createGroupSchedule(groupCode, requestSchedule));
    }


    @Operation(summary = "그룹 스케줄: 작성 목록 조회")
    @GetMapping("/api/v1/group-rooms/{groupRoomId}")
    public CommonResponse<List<GroupScheduleResponse>> getSchedulesByGroupRoom(@PathVariable Long groupRoomId, @AuthenticationPrincipal UserInfo userInfo) {
        List<GroupScheduleResponse> scheduleResponses = groupScheduleService.getSchedulesByGroupRoom(groupRoomId, userInfo);
        return CommonUtils.success(scheduleResponses);
    }

    @Operation(summary = "그룹 스케줄: 조회")
    @GetMapping("/api/v1/group-rooms/{groupRoomId}/{scheduleId}")
    public CommonResponse<GroupScheduleResponse> getGroupSchedule(@PathVariable Long groupRoomId, @PathVariable Long scheduleId, @AuthenticationPrincipal UserInfo userInfo) {
        GroupScheduleResponse scheduleResponses = groupScheduleService.getGroupScheduleById(userInfo, groupRoomId, scheduleId);
        return CommonUtils.success(scheduleResponses);
    }

    @Operation(summary = "그룹 스케줄: 스케줄 수정")
    @PatchMapping("/api/v1/group-rooms/{groupRoomId}/{scheduleId}")
    public CommonResponse<ScheduleResponse> updateScheduleByGroupRoom(@PathVariable Long groupRoomId, @PathVariable Long scheduleId, @RequestBody GroupScheduleRequest groupScheduleRequest, @AuthenticationPrincipal UserInfo userInfo) {
        ScheduleResponse scheduleResponse = groupScheduleService.updateScheduleByGroupRoom(groupRoomId, scheduleId, groupScheduleRequest, userInfo);
        return CommonUtils.success(scheduleResponse);
    }

    @Operation(summary = "그룹 스케줄: 스케줄 삭제")
    @DeleteMapping("/api/v1/group-rooms/{groupRoomId}/{scheduleId}")
    public ResponseEntity<ScheduleResponse> deleteScheduleByGroupRoom(@PathVariable Long groupRoomId, @PathVariable Long scheduleId, @AuthenticationPrincipal UserInfo userInfo) {
        groupScheduleService.deleteScheduleByGroupRoom(groupRoomId, scheduleId, userInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
