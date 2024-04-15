package com.tukorea.planding.domain.invitation.dto;

import lombok.Builder;

@Builder
public record InvitationResponse(
        String inviteCode,
        String inviteUser,
        String groupName,
        String invitedUser
) {
}
