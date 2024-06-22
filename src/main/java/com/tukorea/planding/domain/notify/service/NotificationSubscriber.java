package com.tukorea.planding.domain.notify.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tukorea.planding.domain.notify.entity.Notification;
import com.tukorea.planding.domain.notify.repository.EmitterRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSubscriber {

    private final EmitterRepositoryImpl emitterRepository;
    private final ObjectMapper objectMapper;

    public void handleMessage(String message) {
        try {
            Notification notification = objectMapper.readValue(message, Notification.class);
            String userCode = notification.getUserCode();
            Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserCode(userCode);

            String eventId = userCode + "_" + System.currentTimeMillis();
            emitters.forEach(
                    (key, emitter) -> {
                        emitterRepository.saveEventCache(key, notification);
                        sendNotification(emitter, eventId, key, notification);
                    }
            );
        } catch (IOException e) {
            log.error("Failed to handle message", e);
        }
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Notification notification) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(notification));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            emitter.completeWithError(exception);
        }
    }
}
