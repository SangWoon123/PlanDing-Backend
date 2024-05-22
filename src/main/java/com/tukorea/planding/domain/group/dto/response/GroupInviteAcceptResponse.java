package com.tukorea.planding.domain.group.dto.response;

import lombok.Builder;

@Builder
public record GroupInviteAcceptResponse(
        Long groupId
) {
}
