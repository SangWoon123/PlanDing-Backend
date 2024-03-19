package com.tukorea.planding.schedule.domain;

import com.tukorea.planding.global.audit.BaseEntityTime;
import com.tukorea.planding.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @JoinColumn(name = "user_id")
    private User user;

//    @ManyToOne
//    @JoinColumn(name = "grouproom_id", nullable = false)
//    private GroupRoom groupRoom; // 해당 일정이 속한 그룹방

    public void updateTitleAndContent(Optional<String> title, Optional<String> content) {
        title.ifPresent(value -> this.title = value);
        content.ifPresent(value -> this.content = value);
    }

    public void toggleComplete() {
        if (complete) {
            this.complete = false;
            return;
        }
        this.complete = true;
    }
}
