package com.tukorea.planding.user.dto;

import com.tukorea.planding.global.oauth.details.Role;
import com.tukorea.planding.user.domain.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private Long id;

    private String username;

    private String email;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String code;

}
