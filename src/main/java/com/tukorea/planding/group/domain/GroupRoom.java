package com.tukorea.planding.group.domain;

import com.tukorea.planding.schedule.domain.Schedule;
import com.tukorea.planding.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GroupRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String owner; // 그룹룸의 소유자

    @Column(nullable = false, unique = true)
    private String groupCode; // 그룹방 고유 식별값

    @OneToMany(mappedBy = "groupRoom")
    private final Set<UserGroupMembership> groupMemberships = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    private final Set<Schedule> schedules = new HashSet<>(); // 그룹 일정들

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
