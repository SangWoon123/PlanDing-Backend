package com.tukorea.planding.domain.notify.service;

import com.tukorea.planding.domain.notify.dto.NotificationDTO;
import com.tukorea.planding.domain.notify.dto.NotificationScheduleRequest;
import com.tukorea.planding.domain.notify.entity.Notification;
import com.tukorea.planding.domain.notify.repository.EmitterRepositoryImpl;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepositoryImpl emitterRepository;

    public SseEmitter createEmitter(String userCode) {
        return emitterRepository.save(userCode, new SseEmitter(DEFAULT_TIMEOUT));
    }

    public void deleteEmitter(String userCode) {
        emitterRepository.deleteById(userCode);
    }

    public void sendNotificationToClient(String emitterKey, NotificationDTO notificationDto) {
        emitterRepository.findById(emitterKey)
                .ifPresent(emitter -> send(notificationDto, emitterKey, emitter));
    }

    public void send(Object data, String emitterKey, SseEmitter sseEmitter) {
        try {
            log.info("send to client {}:[{}]", emitterKey, data);
            sseEmitter.send(SseEmitter.event()
                    .id(emitterKey)
                    .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException | IllegalStateException e) {
            log.error("IOException | IllegalStateException is occurred. ", e);
            emitterRepository.deleteById(emitterKey);
        }
    }

}
