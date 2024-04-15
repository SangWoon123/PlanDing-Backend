package com.tukorea.planding.domain.group.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.group.dto.GroupCreateRequest;
import com.tukorea.planding.domain.group.dto.GroupInviteRequest;
import com.tukorea.planding.domain.group.dto.GroupResponse;
import com.tukorea.planding.domain.group.service.GroupRoomService;
import com.tukorea.planding.domain.user.dto.UserInfo;
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
    public CommonResponse<GroupResponse> createGroupRoom(@AuthenticationPrincipal UserInfo userInfo, @RequestBody GroupCreateRequest createGroupRoom) {
        GroupResponse groupResponse = groupRoomService.createGroupRoom(userInfo, createGroupRoom);
        return CommonUtils.success(groupResponse);
    }

    @Operation(summary = "다른유저 그룹으로 초대")
    @PostMapping("/invite")
    public CommonResponse<GroupResponse> inviteGroupRoom(@AuthenticationPrincipal UserInfo userInfo, @RequestBody GroupInviteRequest invitedUser) {
        GroupResponse groupResponse = groupRoomService.handleInvitation(userInfo, invitedUser);
        return CommonUtils.success(groupResponse);
    }

    @Operation(summary = "유저가 속한 그룹 가져오기")
    @GetMapping("/myGroup")
    public CommonResponse<List<GroupResponse>> getAllGroupRoomByUser(@AuthenticationPrincipal UserInfo userInfo) {
        List<GroupResponse> groupResponses = groupRoomService.getAllGroupRoomByUser(userInfo);
        return CommonUtils.success(groupResponses);
    }
}
