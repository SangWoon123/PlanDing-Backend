package com.tukorea.planding.domain.schedule.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.tukorea.planding.domain.schedule.entity.QSchedule.schedule;

@Repository
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
}
