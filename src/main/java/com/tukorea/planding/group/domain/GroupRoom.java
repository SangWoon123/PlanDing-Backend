//package com.tukorea.planding.group.domain;
//
//import com.tukorea.planding.schedule.domain.Schedule;
//import com.tukorea.planding.user.domain.User;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.UUID;
//
//@Entity
//@Getter
//@Builder
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//public class GroupRoom {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    private String owner; // 그룹룸의 소유자
//
//    @Column(nullable = false, unique = true)
//    private String groupCode; // 그룹방 고유 식별값
//
////    @OneToMany(mappedBy = "groupRoom", cascade = CascadeType.ALL)
////    private Set<User> users = new HashSet<>(); // 그룹 멤버들
//
//    @OneToMany(cascade = CascadeType.ALL)
//    private Set<Schedule> schedules = new HashSet<>(); // 그룹 일정들
//
//    @PrePersist
//    public void generateRoomCode() {
//        this.groupCode = "G" + UUID.randomUUID().toString();
//    }
//}
