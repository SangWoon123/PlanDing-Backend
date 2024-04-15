package com.tukorea.planding.domain.invitation.dto;

import lombok.Builder;

@Builder
public record InvitationRequest(
        String userCode,
        String inviteGroupCode
) {

}
