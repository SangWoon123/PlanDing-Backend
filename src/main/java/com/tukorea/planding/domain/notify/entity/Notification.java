package com.tukorea.planding.domain.notify.entity;

import com.tukorea.planding.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String message;

    private String groupName;

    private String url;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    @Builder
    public Notification(User user, String message, String groupName, String url, NotificationType notificationType, LocalDateTime createdAt, LocalDateTime readAt) {
        this.user = user;
        this.message = message;
        this.groupName = groupName;
        this.url = url;
        this.notificationType = notificationType;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }
}
