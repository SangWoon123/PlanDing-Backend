package com.tukorea.planding.domain.schedule.entity;

import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.global.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SCHEDULE")
public class Schedule extends BaseEntity {

    @Id
    @Column(name = "schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "complete", nullable = false)
    private boolean isComplete;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ScheduleStatus status = ScheduleStatus.UNDECIDED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_room_id")
    private GroupRoom groupRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Schedule(String title, String content, LocalDate scheduleDate, LocalTime startTime, LocalTime endTime, boolean isComplete, GroupRoom groupRoom, User user) {
        this.title = title;
        this.content = content;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isComplete = isComplete;
        this.groupRoom = groupRoom;
        this.user = user;
    }

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
        if (isComplete) {
            this.isComplete = false;
            return;
        }
        this.isComplete = true;
    }

    public void addUser(User user) {
        this.user = user;
        user.getSchedules().add(this);
    }

    /*
    스케줄 상태변환 메서드
     */
    public void markAsPossible() {
        this.status = ScheduleStatus.POSSIBLE;
    }

    public void markAsImpossible() {
        this.status = ScheduleStatus.IMPOSSIBLE;
    }

    public void markAsUndecided() {
        this.status = ScheduleStatus.UNDECIDED;
    }

}
