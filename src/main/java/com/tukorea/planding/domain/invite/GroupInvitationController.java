package com.tukorea.planding.domain.invite;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.group.dto.request.GroupInviteRequest;
import com.tukorea.planding.domain.user.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/invite")
public class GroupInvitationController {
    private final GroupInviteService2 groupInviteService2;


    @PostMapping()
    public CommonResponse<GroupInviteDTO> invite(@AuthenticationPrincipal UserInfo userInfo, GroupInviteRequest groupInviteRequest) {
        return CommonUtils.success(groupInviteService2.inviteGroupRoom(userInfo, groupInviteRequest));
    }

    @GetMapping("/accept/{groupId}/{code}")
    public CommonResponse<?> accept(@AuthenticationPrincipal UserInfo userInfo, @PathVariable(name = "groupId") Long groupId, @PathVariable(name = "code") String code) {
        groupInviteService2.acceptInvitation(userInfo, code, groupId);
        return CommonUtils.success("수락완료");
    }
}
