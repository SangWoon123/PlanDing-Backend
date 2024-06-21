package com.tukorea.planding.domain.notify.entity;

import com.tukorea.planding.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserNotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean scheduleNotificationEnabled;

    private boolean groupScheduleNotificationEnabled;

    @Builder
    public UserNotificationSetting(User user, boolean scheduleNotificationEnabled, boolean groupScheduleNotificationEnabled) {
        this.user = user;
        this.scheduleNotificationEnabled = scheduleNotificationEnabled;
        this.groupScheduleNotificationEnabled = groupScheduleNotificationEnabled;
    }

    public void updateUser(User user) {
        this.user = user;
    }

    public void updateScheduleNotificationEnabled(boolean scheduleNotificationEnabled) {
        this.scheduleNotificationEnabled = scheduleNotificationEnabled;
    }

    public void updateGroupScheduleNotificationEnabled(boolean groupScheduleNotificationEnabled) {
        this.groupScheduleNotificationEnabled = groupScheduleNotificationEnabled;
    }
}
