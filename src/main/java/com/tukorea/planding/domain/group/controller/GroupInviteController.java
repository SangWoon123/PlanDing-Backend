package com.tukorea.planding.domain.group.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.group.dto.GroupInviteRequest;
import com.tukorea.planding.domain.group.dto.GroupInviteResponse;
import com.tukorea.planding.domain.group.service.GroupInviteService;
import com.tukorea.planding.domain.user.dto.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Invitation", description = "그룹 초대관련")
@RestController
@RequestMapping("/api/v1/invitation")
@RequiredArgsConstructor
public class GroupInviteController {

    private final GroupInviteService groupInviteService;

    @Operation(summary = "다른유저를 그룹으로 초대")
    @PostMapping()
    public CommonResponse<GroupInviteResponse> inviteGroupRoom(@AuthenticationPrincipal UserInfo userInfo, @RequestBody GroupInviteRequest invitedUser) {
        GroupInviteResponse groupInviteResponse = groupInviteService.inviteGroupRoom(userInfo, invitedUser);
        return CommonUtils.success(groupInviteResponse);
    }

    //TODO 쿼리스트링Url 변경 고민 ex) api/v1/invitation/accept?{code}
    @Operation(summary = "초대 승낙")
    @GetMapping("/{code}/accept")
    public CommonResponse<GroupInviteResponse> acceptInvitation(@AuthenticationPrincipal UserInfo userInfo, @PathVariable(name = "code") String code) {
        GroupInviteResponse groupInviteResponse = groupInviteService.acceptInvitation(userInfo, code);
        return CommonUtils.success(groupInviteResponse);
    }

    @Operation(summary = "초대를 받은 목록", description = "아직 초대의 상태를 바꾸지 않은 경우만")
    @GetMapping()
    public CommonResponse<List<GroupInviteResponse>> getInvitations(@AuthenticationPrincipal UserInfo userInfo) {
        List<GroupInviteResponse> groupInviteRespons = groupInviteService.getInvitations(userInfo);
        return CommonUtils.success(groupInviteRespons);
    }


}
