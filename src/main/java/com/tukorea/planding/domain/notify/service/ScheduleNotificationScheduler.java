package com.tukorea.planding.domain.notify.service;

import com.tukorea.planding.domain.notify.dto.NotificationScheduleRequest;
import com.tukorea.planding.domain.notify.entity.NotificationType;
import com.tukorea.planding.domain.schedule.entity.PersonalSchedule;
import com.tukorea.planding.domain.schedule.repository.PersonalScheduleRepository;
import com.tukorea.planding.domain.schedule.repository.PersonalScheduleRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleNotificationScheduler {

    private final PersonalScheduleRepository personalScheduleRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 60000)  // 1분마다 실행
    @Transactional
    public void sendScheduleNotifications() {
        LocalDate now = LocalDate.now();
        List<PersonalSchedule> schedules = personalScheduleRepository.findSchedulesForNextDay(now);

        for (PersonalSchedule schedule : schedules) {
            notificationService.sendPersonalNotification(schedule.getUser().getUserCode(),
                    NotificationScheduleRequest.builder()
                            .receiverCode(schedule.getUser().getUserCode())
                            .message("test")
                            .type(NotificationType.PERSONAL_SCHEDULE)
                            .build());

            log.info("Notification sent for schedule: ");
        }
    }
}
