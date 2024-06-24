package com.tukorea.planding.domain.notify.service;

import com.tukorea.planding.domain.notify.dto.NotificationDTO;
import com.tukorea.planding.domain.notify.service.setting.UserNotificationSettingQueryService;
import com.tukorea.planding.domain.notify.service.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SseEmitterService sseEmitterService;
    private final RedisMessageService redisMessageService;
    private final UserNotificationSettingQueryService userNotificationSettingQueryService;


    public SseEmitter subscribe(String userCode) {
        SseEmitter sseEmitter = sseEmitterService.createEmitter(userCode);
        sseEmitterService.send("연결되었습니다. [userCode=" + userCode + "]", userCode, sseEmitter);

        sseEmitter.onTimeout(sseEmitter::complete);
        sseEmitter.onError((e) -> sseEmitter.complete());
        sseEmitter.onCompletion(() -> {
            sseEmitterService.deleteEmitter(userCode);
            redisMessageService.removeSubscribe(userCode); // 구독한 채널 삭제
        });
        return sseEmitter;
    }

    // 개인 스케줄 알림 코드
    public void sendPersonalNotification(String userCode, NotificationDTO request) {
        if (!userNotificationSettingQueryService.getSettingValue(userCode).isScheduleNotificationEnabled()) {
            return;
        }

        String channel = request.getUserCode();
        redisMessageService.publish(channel, request);
    }
}
