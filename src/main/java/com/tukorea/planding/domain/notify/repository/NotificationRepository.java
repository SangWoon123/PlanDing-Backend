package com.tukorea.planding.domain.notify.repository;

import com.tukorea.planding.domain.notify.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
