package com.tukorea.planding.domain.group.dto;

import lombok.Builder;

@Builder
public record InvitationRequest(
        String userCode,
        String inviteGroupCode
) {

}
