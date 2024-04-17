package com.tukorea.planding.domain.invitation.dto;

import com.tukorea.planding.domain.invitation.entity.InviteStatus;
import lombok.Builder;

@Builder
public record InvitationResponse(
        String inviteCode,
        String invitingUser,
        String groupName,
        InviteStatus inviteStatus
) {
}
