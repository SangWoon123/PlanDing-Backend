package com.tukorea.planding.domain.invitation.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.invitation.dto.InvitationRequest;
import com.tukorea.planding.domain.invitation.dto.InvitationResponse;
import com.tukorea.planding.domain.invitation.service.InvitationService;
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
public class InvitationController {

    private final InvitationService invitationService;

    @Operation(summary = "다른유저를 그룹으로 초대")
    @PostMapping()
    public CommonResponse<InvitationResponse> inviteGroupRoom(@AuthenticationPrincipal UserInfo userInfo, @RequestBody InvitationRequest invitedUser) {
        InvitationResponse invitationResponse = invitationService.inviteGroupRoom(userInfo, invitedUser);
        return CommonUtils.success(invitationResponse);
    }

    //TODO 쿼리스트링Url 변경 고민 ex) api/v1/invitation/accept?{code}
    @Operation(summary = "초대 승낙")
    @GetMapping("/{code}/accept")
    public CommonResponse<InvitationResponse> acceptInvitation(@AuthenticationPrincipal UserInfo userInfo, @PathVariable(name = "code") String code) {
        InvitationResponse invitationResponse = invitationService.acceptInvitation(userInfo, code);
        return CommonUtils.success(invitationResponse);
    }

    @Operation(summary = "초대 받은 목록")
    @GetMapping()
    public CommonResponse<List<InvitationResponse>> getInvitations(@AuthenticationPrincipal UserInfo userInfo) {
        List<InvitationResponse> invitationResponses = invitationService.getInvitations(userInfo);
        return CommonUtils.success(invitationResponses);
    }


}
