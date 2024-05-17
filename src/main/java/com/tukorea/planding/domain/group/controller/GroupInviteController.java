package com.tukorea.planding.domain.group.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.group.dto.request.GroupInviteRequest;
import com.tukorea.planding.domain.group.service.GroupInviteService;
import com.tukorea.planding.domain.group.dto.response.GroupInviteMessageResponse;
import com.tukorea.planding.domain.user.dto.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/invitation")
public class GroupInviteController {
    private final GroupInviteService groupInviteService;

    @PostMapping()
    public CommonResponse<GroupInviteMessageResponse> invite(@AuthenticationPrincipal UserInfo userInfo, @RequestBody GroupInviteRequest groupInviteRequest) {
        return CommonUtils.success(groupInviteService.inviteGroupRoom(userInfo, groupInviteRequest));
    }

    @GetMapping("/accept/{groupId}/{code}")
    public CommonResponse<?> accept(@AuthenticationPrincipal UserInfo userInfo, @PathVariable(name = "groupId") Long groupId, @PathVariable(name = "code") String code) {
        groupInviteService.acceptInvitation(userInfo, code, groupId);
        return CommonUtils.success("수락완료");
    }

    @Operation(summary = "초대를 받은 목록", description = "아직 초대의 상태를 바꾸지 않은 경우만")
    @GetMapping()
    public CommonResponse<List<GroupInviteMessageResponse>> getInvitations(@AuthenticationPrincipal UserInfo userInfo) {
        List<GroupInviteMessageResponse> groupInviteResponse = groupInviteService.getInvitations(userInfo);
        return CommonUtils.success(groupInviteResponse);
    }

    @DeleteMapping("/decline/{code}")
    public CommonResponse<?> declineInvitation(@AuthenticationPrincipal UserInfo userInfo, @PathVariable(name = "code") String code) {
        groupInviteService.declineInvitation(userInfo, code);
        return CommonUtils.success("거절하였습니다.");
    }
}
