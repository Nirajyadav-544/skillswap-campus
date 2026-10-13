package com.skillswap.notification.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.notification.entity.NotificationChannel;
import com.skillswap.notification.entity.NotificationPreference;
import com.skillswap.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationPreferenceRepository
        extends JpaRepository<NotificationPreference, Long> {

    List<NotificationPreference> findByUser(User user);

    List<NotificationPreference> findByUserAndNotificationType(
            User user,
            NotificationType notificationType
    );

    List<NotificationPreference> findByUserAndChannel(
            User user,
            NotificationChannel channel
    );

    Optional<NotificationPreference> findByUserAndNotificationTypeAndChannel(
            User user,
            NotificationType notificationType,
            NotificationChannel channel
    );

    boolean existsByUserAndNotificationTypeAndChannel(
            User user,
            NotificationType notificationType,
            NotificationChannel channel
    );

    boolean existsByUserAndNotificationTypeAndChannelAndEnabledTrue(
            User user,
            NotificationType notificationType,
            NotificationChannel channel
    );

    long countByUser(User user);

    long countByUserAndEnabledTrue(User user);

    void deleteByUser(User user);
}