package com.tukorea.planding.domain.group.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GroupInviteRequest{
    private Long groupId;
    private String userCode;
}
