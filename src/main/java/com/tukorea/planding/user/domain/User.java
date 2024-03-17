package com.tukorea.planding.user.domain;

import com.tukorea.planding.global.audit.BaseEntityTime;
import com.tukorea.planding.global.oauth.details.Role;
import com.tukorea.planding.schedule.domain.Schedule;
import com.tukorea.planding.user.dto.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String profileImage;

    private String username;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    private String code;

    @OneToMany(mappedBy = "user")
    private List<Schedule> schedules=new ArrayList<>();

    public static String createCode(){
        String c= UUID.randomUUID().toString();
        return "#" + c.substring(0,4);
    }

    public static UserInfo toUserInfo(User user){
        return UserInfo.builder()
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .username(user.getUsername())
                .code(user.getCode())
                .build();
    }
}
