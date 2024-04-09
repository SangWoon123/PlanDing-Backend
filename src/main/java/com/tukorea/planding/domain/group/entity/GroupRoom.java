package com.tukorea.planding.domain.group.entity;

import com.tukorea.planding.domain.user.entity.User;import com.tukorea.planding.global.audit.BaseEntity;
import com.tukorea.planding.domain.schedule.entity.Schedule;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "GROUP_ROOM")
public class GroupRoom extends BaseEntity {

    @Id
    @Column(name = "group_room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "owner", nullable = false)
    private String owner; // 그룹룸의 소유자

    @Column(name = "group_code", nullable = false, unique = true)
    private String groupCode; // 그룹방 고유 식별값

    @OneToMany(mappedBy = "groupRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<UserGroupMembership> groupMemberships = new HashSet<>();

    @OneToMany(mappedBy = "groupRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Schedule> schedules = new ArrayList<>();

    @Builder
    public GroupRoom(String name, String owner, String groupCode) {
        this.name = name;
        this.owner = owner;
        this.groupCode = groupCode;
    }

    @PrePersist
    public void generateRoomCode() {
        this.groupCode = "G" + UUID.randomUUID().toString();
    }

    // 연관 관계 편의 메서드
    public void addUser(User user) {
        UserGroupMembership membership = UserGroupMembership.builder()
                .user(user)
                .groupRoom(this)
                .build();
        this.groupMemberships.add(membership);
        user.getGroupMemberships().add(membership);
    }

    // 스케줄을 그룹룸에 추가하는 메서드
    public void addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
    }


}
