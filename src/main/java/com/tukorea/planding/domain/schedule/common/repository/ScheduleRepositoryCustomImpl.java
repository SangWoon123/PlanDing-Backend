package com.tukorea.planding.domain.schedule.common.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.tukorea.planding.domain.schedule.entity.QSchedule.schedule;

@RequiredArgsConstructor
public class ScheduleRepositoryCustomImpl implements ScheduleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Schedule> findWeeklyScheduleByUser(LocalDate startDate, LocalDate endDate, User user) {
        return queryFactory.selectFrom(schedule)
                .where(schedule.scheduleDate.between(startDate, endDate)
                        .and(schedule.user.eq(user)))
                .fetch();
    }

    @Override
    public List<Schedule> findOverlapSchedules(Long userId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return queryFactory.selectFrom(schedule)
                .where(schedule.user.id.eq(userId)
                        .and(schedule.scheduleDate.eq(date)
                                .and(schedule.startTime.before(endTime))
                                .and(schedule.endTime.after(startTime)))
                        .or(schedule.startTime.between(startTime, endTime))
                        .or(schedule.endTime.between(startTime, endTime)))
                .fetch();
    }

    @Override
    public List<Schedule> showTodaySchedule(Long userId) {
        LocalDate today = LocalDate.now();

        return queryFactory
                .selectFrom(schedule)
                .where(schedule.user.id.eq(userId)
                        .and(schedule.scheduleDate.eq(today)))
                .fetch();
    }
}
