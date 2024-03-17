package com.tukorea.planding.schedule.dao;

import com.tukorea.planding.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
}
