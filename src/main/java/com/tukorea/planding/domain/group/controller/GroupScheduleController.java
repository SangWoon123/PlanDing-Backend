package com.tukorea.planding.domain.group.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.group.dto.GroupScheduleRequest;
import com.tukorea.planding.domain.group.service.GroupScheduleService;
import com.tukorea.planding.domain.schedule.dto.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupScheduleController {

    private final GroupScheduleService groupScheduleService;

    @MessageMapping("/schedule/{groupCode}") // schedule 경로로 메시지를 보내면
    @SendTo("/sub/schedule/{groupCode}")    // /sub/schedule/{group_code} 을 구독한 유저에게 메시지를 뿌림
    public CommonResponse<ScheduleResponse> createGroupSchedule(@DestinationVariable String groupCode, GroupScheduleRequest requestSchedule) {
        return CommonUtils.success(groupScheduleService.createGroupSchedule(groupCode, requestSchedule));
    }

}
