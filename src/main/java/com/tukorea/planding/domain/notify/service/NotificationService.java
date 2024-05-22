package com.tukorea.planding.domain.notify.service;

import com.tukorea.planding.domain.group.dto.GroupInviteEvent;
import com.tukorea.planding.domain.notify.dto.NotificationScheduleRequest;
import com.tukorea.planding.domain.notify.dto.NotificationScheduleResponse;
import com.tukorea.planding.domain.notify.entity.Notification;
import com.tukorea.planding.domain.notify.entity.NotificationType;
import com.tukorea.planding.domain.notify.repository.EmitterRepositoryImpl;
import com.tukorea.planding.domain.notify.repository.NotificationRepository;
import com.tukorea.planding.domain.schedule.service.GroupScheduleCreatedEvent;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.repository.UserRepository;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final NotificationRepository notificationRepository;
    private final EmitterRepositoryImpl emitterRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void notifyInvitation(final String userCode, final String groupName) {
        GroupInviteEvent event = new GroupInviteEvent(this, userCode, groupName);
        eventPublisher.publishEvent(event);
    }

    @EventListener
    public void handleGroupInvitedEvent(GroupInviteEvent event) {
        NotificationScheduleRequest request = NotificationScheduleRequest.builder()
                .type(NotificationType.INVITE)
                .groupName(event.getGroupName())
                .message(event.getGroupName() + "그룹으로 부터 초대되었습니다.")
                .receiverCode(event.getUserCode())
                .build();

        send(request);
    }

    @EventListener
    public void handleGroupScheduleCreatedEvent(GroupScheduleCreatedEvent event) {
        NotificationScheduleRequest request = NotificationScheduleRequest.builder()
                .type(NotificationType.GROUP_SCHEDULE)
                .groupName(event.getGroupName())
                .message("새로운 그룹 스케줄이 생성되었습니다: " + event.getScheduleTitle())
                .receiverCode(event.getUserCode())
                .url(event.getUrl())
                .build();

        send(request);
    }

    public SseEmitter subscribe(String userCode, String lastEventId) {
        String emitterId = makeTimeIncludeId(userCode);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(userCode);
        sendNotification(emitter, eventId, emitterId, "연결되었습니다. [userCode=" + userCode + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, userCode, emitterId, emitter);
        }

        return emitter;
    }

    public void send(NotificationScheduleRequest notificationScheduleRequest) {

        User user = userRepository.findByUserCode(notificationScheduleRequest.getReceiverCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Notification notification = notificationRepository.save(createNotification(user, notificationScheduleRequest));

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserCode(user.getUserCode());

        String eventId = user.getUserCode() + "_" + System.currentTimeMillis();
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, NotificationScheduleResponse.of(notification));
                }
        );
    }


    private Notification createNotification(User user, NotificationScheduleRequest notificationScheduleRequest) {
        return Notification.builder()
                .user(user)
                .notificationType(notificationScheduleRequest.getType())
                .message(notificationScheduleRequest.getMessage())
                .createdAt(LocalDateTime.now())
                .notificationType(notificationScheduleRequest.getType())
                .url(notificationScheduleRequest.getUrl())
                .groupName(notificationScheduleRequest.getGroupName())
                .readAt(null)
                .build();
    }

    private void sendLostData(String lastEventId, String userCode, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(userCode));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(data, MediaType.APPLICATION_JSON)
            );
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            emitter.completeWithError(exception);
        }
    }

    private String makeTimeIncludeId(String userCode) {
        return userCode + "_" + System.currentTimeMillis();
    }

}
