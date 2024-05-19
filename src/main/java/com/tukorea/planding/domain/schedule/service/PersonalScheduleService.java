package com.tukorea.planding.domain.schedule.service;

import com.tukorea.planding.domain.schedule.dto.request.ScheduleRequest;
import com.tukorea.planding.domain.schedule.dto.response.PersonalScheduleResponse;
import com.tukorea.planding.domain.schedule.dto.response.ScheduleResponse;
import com.tukorea.planding.domain.schedule.entity.PersonalSchedule;
import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.schedule.entity.ScheduleType;
import com.tukorea.planding.domain.schedule.repository.PersonalScheduleRepository;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonalScheduleService {

    private final ScheduleQueryService scheduleQueryService;
    private final UserQueryService userQueryService;
    private final PersonalScheduleRepository personalScheduleRepository;

    public PersonalScheduleResponse createSchedule(UserInfo userInfo, ScheduleRequest scheduleRequest) {
        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());

        // 개인 스케줄 생성
        PersonalSchedule personalSchedule = PersonalSchedule.builder()
                .user(user)
                .build();

        Schedule newSchedule = Schedule.builder()
                .personalSchedule(personalSchedule)
                .title(scheduleRequest.title())
                .content(scheduleRequest.content())
                .scheduleDate(scheduleRequest.scheduleDate())
                .startTime(scheduleRequest.startTime())
                .endTime(scheduleRequest.endTime())
                .isComplete(false)
                .type(ScheduleType.PERSONAL)
                .build();

        // 개인 스케줄에 스케줄 추가
        personalSchedule.getSchedules().add(newSchedule);

        personalScheduleRepository.save(personalSchedule);

        Schedule save = scheduleQueryService.save(newSchedule);

        return PersonalScheduleResponse.from(save);
    }

    @Transactional(readOnly = true)
    public PersonalScheduleResponse getSchedule(Long scheduleId, UserInfo userInfo) {
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);
        schedule.getPersonalSchedule().checkOwnership(userInfo.getId());
        return PersonalScheduleResponse.from(schedule);
    }

    public void deleteSchedule(UserInfo userInfo, Long scheduleId) {
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);
        schedule.getPersonalSchedule().checkOwnership(userInfo.getId());
        scheduleQueryService.delete(schedule);
    }


    public PersonalScheduleResponse updateSchedule(Long scheduleId, ScheduleRequest scheduleRequest, UserInfo userInfo) {
        Schedule schedule = scheduleQueryService.findScheduleById(scheduleId);
        schedule.getPersonalSchedule().checkOwnership(userInfo.getId());
        schedule.update(scheduleRequest.title(), scheduleRequest.content(), scheduleRequest.startTime(), scheduleRequest.endTime());
        return PersonalScheduleResponse.from(schedule);
    }
}