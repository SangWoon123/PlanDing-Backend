package com.tukorea.planding.group.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.group.service.GroupScheduleService;
import com.tukorea.planding.schedule.domain.Schedule;
import com.tukorea.planding.schedule.dto.ResponseSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupScheduleController {

    private final GroupScheduleService groupScheduleService;

    @MessageMapping("/schedule") // schedule 경로로 메시지를 보내면
    @SendTo("/sub/topic/group")    // topic/group 을 구독한 유저에게 메시지를 뿌림
    public CommonResponse<ResponseSchedule> createGroupSchedule(Schedule groupSchedule){
        return CommonUtils.success(groupScheduleService.createGroupSchedule(groupSchedule));
    }

}
