package com.tukorea.planding.domain.notify.service;

import com.tukorea.planding.domain.notify.dto.NotificationSettingResponse;
import com.tukorea.planding.domain.notify.entity.UserNotificationSetting;
import com.tukorea.planding.domain.notify.repository.UserNotificationSettingRepository;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserNotificationSettingService {

    private final UserNotificationSettingRepository userNotificationSettingRepository;
    private final UserQueryService userQueryService;

    public NotificationSettingResponse getNotificationSetting(UserInfo userInfo) {
        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());

        UserNotificationSetting setting = userNotificationSettingRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.SETTING_NOT_FOUND));

        return NotificationSettingResponse.builder()
                .personalSchedule(setting.isScheduleNotificationEnabled())
                .groupSchedule(setting.isGroupScheduleNotificationEnabled())
                .build();
    }


    public void updateScheduleNotificationSetting(String userCode, boolean enabled, int minutesBefore) {
        User user = userQueryService.getUserByUserCode(userCode);

        UserNotificationSetting setting = userNotificationSettingRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.SETTING_NOT_FOUND));

        setting.updateScheduleNotificationEnabled(enabled);
    }

    public void updateGroupScheduleNotificationSetting(String userCode, boolean enabled) {
        User user = userQueryService.getUserByUserCode(userCode);

        UserNotificationSetting setting = userNotificationSettingRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.SETTING_NOT_FOUND));

        setting.updateGroupScheduleNotificationEnabled(enabled);
    }
}
