package com.tukorea.planding.domain.group.dto.response;

import com.tukorea.planding.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
public record GroupUserResponse(
        String userCode,
        String userName
) {
    public static GroupUserResponse toGroupUserResponse(User user) {
        return GroupUserResponse.builder()
                .userCode(user.getUserCode())
                .userName(user.getUsername())
                .build();
    }
}
