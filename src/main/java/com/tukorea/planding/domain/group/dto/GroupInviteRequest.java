package com.tukorea.planding.domain.group.dto;

import lombok.Builder;

@Builder
public record GroupInviteRequest(
        String userCode,
        String inviteGroupCode
) {

}
