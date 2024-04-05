package com.tukorea.planding.domain.schedule.repository;

import com.tukorea.planding.domain.schedule.entity.Schedule;import com.tukorea.planding.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByScheduleDateAndUser(LocalDate date, User user);

    List<Schedule> findByGroupRoomId(Long groupRoomId);
}
