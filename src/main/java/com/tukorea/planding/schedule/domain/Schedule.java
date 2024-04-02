package com.tukorea.planding.schedule.domain;

import com.tukorea.planding.global.audit.BaseEntityTime;
import com.tukorea.planding.group.domain.GroupRoom;
import com.tukorea.planding.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    private boolean complete;

    @ManyToOne
    @JoinColumn(name = "group_room_id")
    private GroupRoom groupRoom;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void update(String title, String content, LocalTime startTime, LocalTime endTime) {
        Optional.ofNullable(title).ifPresent(value -> this.title = value);
        Optional.ofNullable(content).ifPresent(value -> this.content = value);

        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("startTime은 endTime보다 빨라야 합니다.");
        }
        Optional.ofNullable(startTime).ifPresent(value -> this.startTime = value);
        Optional.ofNullable(endTime).ifPresent(value -> this.endTime = value);
    }

    public void toggleComplete() {
        if (complete) {
            this.complete = false;
            return;
        }
        this.complete = true;
    }

    public void setUser(User user) {
        this.user = user;
        user.getSchedules().add(this);
    }
}
