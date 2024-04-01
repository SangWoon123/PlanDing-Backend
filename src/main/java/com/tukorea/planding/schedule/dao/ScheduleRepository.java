package com.tukorea.planding.schedule.dao;

import com.tukorea.planding.schedule.domain.Schedule;
import com.tukorea.planding.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByDateAndUser(LocalDate date, User user);

    List<Schedule> findByGroupRoomId(Long groupRoomId);
}
