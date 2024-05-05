package com.tukorea.planding.domain.group.dto;

import com.tukorea.planding.domain.group.entity.InviteStatus;
import lombok.Builder;

@Builder
public record InvitationResponse(
        String inviteCode,
        String invitingUser,
        String groupName,
        InviteStatus inviteStatus
) {
}
