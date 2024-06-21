package com.tukorea.planding.domain.notify.controller;

import com.tukorea.planding.domain.notify.service.UserNotificationSettingService;
import com.tukorea.planding.domain.user.dto.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Setting", description = "사용자 알림 설정 관련")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-setting")
public class UserNotificationSettingController {

    private final UserNotificationSettingService userNotificationSettingService;

    @Operation(description = "개인 스케줄 알림 설정을 업데이트한다")
    @PutMapping("/schedule-notifications")
    public void updateScheduleNotificationSetting(@AuthenticationPrincipal UserInfo userInfo,
                                                  @RequestParam boolean enabled,
                                                  @RequestParam int minutesBefore) {
        userNotificationSettingService.updateScheduleNotificationSetting(userInfo.getUserCode(), enabled, minutesBefore);
    }

    @Operation(description = "그룹 스케줄 알림 설정을 업데이트한다")
    @PutMapping("/group-schedule-notifications")
    public void updateGroupScheduleNotificationSetting(@AuthenticationPrincipal UserInfo userInfo,
                                                       @RequestParam boolean enabled) {
        userNotificationSettingService.updateGroupScheduleNotificationSetting(userInfo.getUserCode(), enabled);
    }
}
