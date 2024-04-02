package com.tukorea.planding.group.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.group.dto.RequestCreateGroupRoom;
import com.tukorea.planding.group.dto.RequestInviteGroupRoom;
import com.tukorea.planding.group.dto.ResponseGroupRoom;
import com.tukorea.planding.group.service.GroupRoomService;
import com.tukorea.planding.user.dto.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "GroupRoom", description = "다른 유저와 함께 스케줄을 같이 관리할 그룹 만들기")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
public class GroupRoomController {
    private final GroupRoomService groupRoomService;

    @Operation(summary = "스케줄 그룹 생성")
    @PostMapping()
    public CommonResponse<ResponseGroupRoom> createGroupRoom(@AuthenticationPrincipal UserInfo userInfo, @RequestBody RequestCreateGroupRoom createGroupRoom) {
        ResponseGroupRoom responseGroupRoom = groupRoomService.createGroupRoom(userInfo, createGroupRoom);
        return CommonUtils.success(responseGroupRoom);
    }

    @Operation(summary = "다른유저 그룹으로 초대")
    @PostMapping("/invite")
    public CommonResponse<ResponseGroupRoom> inviteGroupRoom(@AuthenticationPrincipal UserInfo userInfo, @RequestBody RequestInviteGroupRoom invitedUser) {
        ResponseGroupRoom responseGroupRoom = groupRoomService.inviteGroupRoom(userInfo, invitedUser);
        return CommonUtils.success(responseGroupRoom);
    }

    @Operation(summary = "유저가 속한 그룹 가져오기")
    @GetMapping("/myGroup")
    public CommonResponse<List<ResponseGroupRoom>> getAllGroupRoomByUser(@AuthenticationPrincipal UserInfo userInfo) {
        List<ResponseGroupRoom> responseGroupRooms = groupRoomService.getAllGroupRoomByUser(userInfo);
        return CommonUtils.success(responseGroupRooms);
    }
}
