package com.tukorea.planding.domain.group.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestInviteGroupRoom {
    private String userCode;
    private String inviteGroupCode;
}
