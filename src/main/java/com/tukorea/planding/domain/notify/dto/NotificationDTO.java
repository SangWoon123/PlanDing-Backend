package com.tukorea.planding.domain.notify.dto;

import com.tukorea.planding.domain.notify.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationDTO {

    private String userCode;
    private String message;
    private String url;
    private NotificationType notificationType;
    private LocalDateTime createdAt;
    private boolean isRead;
    // 그룹 스케줄에만 해당하는 필드
    private String groupName;

    public static NotificationDTO createPersonalSchedule(String receiverCode, String message, String url, LocalDateTime createdAt) {
        return NotificationDTO.builder()
                .userCode(receiverCode)
                .message(message)
                .url(url)
                .notificationType(NotificationType.PERSONAL_SCHEDULE)
                .createdAt(createdAt)
                .isRead(false)
                .build();
    }

    // 그룹 스케줄 생성 시 필요한 빌더 메서드
    public static NotificationDTO createGroupSchedule(String receiverCode, String message, String url, LocalDateTime createdAt, String groupName) {
        return NotificationDTO.builder()
                .userCode(receiverCode)
                .message(message)
                .url(url)
                .notificationType(NotificationType.GROUP_SCHEDULE)
                .createdAt(createdAt)
                .isRead(false)
                .groupName(groupName)
                .build();
    }

}
