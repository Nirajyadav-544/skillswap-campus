package com.skillswap.notification.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.notification.entity.Notification;
import com.skillswap.notification.entity.NotificationStatus;
import com.skillswap.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser(User user);

    Page<Notification> findByUser(User user, Pageable pageable);

    List<Notification> findByUserAndStatus(
            User user,
            NotificationStatus status
    );

    Page<Notification> findByUserAndStatus(
            User user,
            NotificationStatus status,
            Pageable pageable
    );

    List<Notification> findByUserAndType(
            User user,
            NotificationType type
    );

    Page<Notification> findByUserAndType(
            User user,
            NotificationType type,
            Pageable pageable
    );

    List<Notification> findByUserAndStatusOrderByCreatedAtDesc(
            User user,
            NotificationStatus status
    );

    Page<Notification> findByUserOrderByCreatedAtDesc(
            User user,
            Pageable pageable
    );

    Page<Notification> findByUserAndStatusOrderByCreatedAtDesc(
            User user,
            NotificationStatus status,
            Pageable pageable
    );

    long countByUser(User user);

    long countByUserAndStatus(
            User user,
            NotificationStatus status
    );

    long countByUserAndType(
            User user,
            NotificationType type
    );

    boolean existsByIdAndUser(
            Long id,
            User user
    );

    void deleteByUser(User user);
}