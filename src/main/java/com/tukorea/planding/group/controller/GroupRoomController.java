package com.tukorea.planding.group.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.group.dto.RequestGroupRoom;
import com.tukorea.planding.group.dto.ResponseGroupRoom;
import com.tukorea.planding.group.service.GroupRoomService;
import com.tukorea.planding.user.dto.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "GroupRoom", description = "다른 유저와 함께 스케줄을 같이 관리할 그룹 만들기")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
public class GroupRoomController {
    private final GroupRoomService groupRoomService;
    @Operation(summary = "스케줄 그룹 생성")
    @PostMapping()
    public CommonResponse<ResponseGroupRoom> createGroupRoom(@AuthenticationPrincipal UserInfo userInfo){
        ResponseGroupRoom responseGroupRoom=groupRoomService.createGroupRoom(userInfo);
        return CommonUtils.success(responseGroupRoom);
    }

    @Operation(summary = "다른유저 그룹으로 초대")
    @PostMapping("/invite")
    public CommonResponse<ResponseGroupRoom> inviteGroupRoom(@AuthenticationPrincipal UserInfo userInfo, @RequestBody RequestGroupRoom invitedUser){
        ResponseGroupRoom responseGroupRoom = groupRoomService.inviteGroupRoom(userInfo, invitedUser);
        return CommonUtils.success(responseGroupRoom);
    }
}
