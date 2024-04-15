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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
