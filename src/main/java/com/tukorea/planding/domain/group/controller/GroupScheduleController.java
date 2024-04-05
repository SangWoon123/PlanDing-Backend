package com.tukorea.planding.domain.group.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.group.service.GroupScheduleService;
import com.tukorea.planding.domain.schedule.dto.RequestSchedule;
import com.tukorea.planding.domain.schedule.dto.ResponseSchedule;
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
    public CommonResponse<ResponseSchedule> createGroupSchedule(@DestinationVariable String groupCode, RequestSchedule requestSchedule) {
        return CommonUtils.success(groupScheduleService.createGroupSchedule(groupCode, requestSchedule));
    }

}
