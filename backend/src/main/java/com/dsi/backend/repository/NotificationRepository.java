package com.dsi.backend.repository;

import com.dsi.backend.model.AppUser;
import com.dsi.backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByReceiverOrderByNotificationTimeDesc(AppUser receiver);
}
