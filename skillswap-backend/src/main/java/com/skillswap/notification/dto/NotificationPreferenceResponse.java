package com.skillswap.notification.dto;

import com.skillswap.notification.entity.NotificationChannel;
import com.skillswap.notification.entity.NotificationPreference;
import com.skillswap.notification.entity.NotificationType;

import java.time.LocalDateTime;

public class NotificationPreferenceResponse {

    private Long id;
    private Long userId;
    private NotificationType notificationType;
    private NotificationChannel channel;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NotificationPreferenceResponse() {
    }

    public static NotificationPreferenceResponse from(
            NotificationPreference preference
    ) {
        NotificationPreferenceResponse response =
                new NotificationPreferenceResponse();

        response.id = preference.getId();

        if (preference.getUser() != null) {
            response.userId = preference.getUser().getId();
        }

        response.notificationType = preference.getNotificationType();
        response.channel = preference.getChannel();
        response.enabled = preference.isEnabled();
        response.createdAt = preference.getCreatedAt();
        response.updatedAt = preference.getUpdatedAt();

        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}