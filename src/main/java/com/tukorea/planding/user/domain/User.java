package com.tukorea.planding.user.domain;

import com.tukorea.planding.global.audit.BaseEntity;
import com.tukorea.planding.global.oauth.details.Role;
//import com.tukorea.planding.group.domain.GroupRoom;
import com.tukorea.planding.group.domain.GroupRoom;
import com.tukorea.planding.group.domain.UserGroupMembership;
import com.tukorea.planding.schedule.domain.Schedule;
import com.tukorea.planding.user.dto.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

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
    private final List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private final Set<UserGroupMembership> groupMemberships = new HashSet<>();

    // 연관 관계 편의 메서드
    public void joinGroupRoom(GroupRoom groupRoom) {
        UserGroupMembership membership = UserGroupMembership.builder()
                .user(this)
                .groupRoom(groupRoom)
                .build();
        this.groupMemberships.add(membership);
        groupRoom.getGroupMemberships().add(membership);
    }





    public static String createCode() {
        String c = UUID.randomUUID().toString();
        return "#" + c.substring(0, 4);
    }

    public static UserInfo toUserInfo(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .username(user.getUsername())
                .code(user.getCode())
                .build();
    }
}
