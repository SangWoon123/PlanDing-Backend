package com.tukorea.planding.domain.notify.service;

import com.tukorea.planding.domain.notify.dto.NotificationDTO;
import com.tukorea.planding.domain.notify.entity.UserNotificationSetting;
import com.tukorea.planding.domain.notify.repository.UserNotificationSettingRepository;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SseEmitterService sseEmitterService;
    private final RedisMessageService redisMessageService;
    private final UserQueryService userQueryService;
    private final UserNotificationSettingRepository userNotificationSettingRepository;


    public SseEmitter subscribe(String userCode) {
        SseEmitter sseEmitter = sseEmitterService.createEmitter(userCode);
        sseEmitterService.send("연결되었습니다. [userCode=" + userCode + "]", userCode, sseEmitter);
        redisMessageService.subscribe(userCode);

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
        UserNotificationSetting setting = userNotificationSettingRepository.findByUserCode(userCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.SETTING_NOT_FOUND));

        if (!setting.isScheduleNotificationEnabled()) {
            return;
        }

        String channel = request.getUserCode();
        redisMessageService.publish(channel, request);
    }
}
