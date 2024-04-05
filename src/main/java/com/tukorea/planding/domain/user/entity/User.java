package com.tukorea.planding.domain.user.entity;

import com.tukorea.planding.global.audit.BaseEntity;
import com.tukorea.planding.global.oauth.details.Role;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.entity.UserGroupMembership;
import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.user.dto.UserInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER")
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "username")
    private String username;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "social_type")
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "user_code", nullable = false)
    private String userCode;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<UserGroupMembership> groupMemberships = new HashSet<>();

    @Builder
    public User(String email, String profileImage, String username, Role role, SocialType socialType, String socialId, String userCode) {
        this.email = email;
        this.profileImage = profileImage;
        this.username = username;
        this.role = role;
        this.socialType = socialType;
        this.socialId = socialId;
        this.userCode = userCode;
    }

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
                .userCode(user.getUserCode())
                .build();
    }
}
