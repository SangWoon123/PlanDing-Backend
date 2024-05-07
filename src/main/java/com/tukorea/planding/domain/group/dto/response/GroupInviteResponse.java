package com.tukorea.planding.domain.group.dto.response;

import com.tukorea.planding.domain.group.entity.InviteStatus;
import lombok.Builder;

@Builder
public record GroupInviteResponse(
        String inviteCode,
        String invitingUser,
        String groupName,
        InviteStatus inviteStatus
) {
}
