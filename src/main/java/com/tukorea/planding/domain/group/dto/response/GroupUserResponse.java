package com.tukorea.planding.domain.group.dto.response;

import com.tukorea.planding.domain.group.entity.UserGroup;
import com.tukorea.planding.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
public record GroupUserResponse(
        String userCode,
        String userName,
        Boolean owner
) {
    public static GroupUserResponse toGroupUserResponse(User user) {
        return GroupUserResponse.builder()
                .userCode(user.getUserCode())
                .userName(user.getUsername())
                .build();
    }
}
