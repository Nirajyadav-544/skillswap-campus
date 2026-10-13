package com.skillswap.notification.dto;

import com.skillswap.notification.entity.NotificationChannel;
import com.skillswap.notification.entity.NotificationType;
import jakarta.validation.constraints.NotNull;

public class NotificationPreferenceRequest {

    @NotNull
    private NotificationType notificationType;

    @NotNull
    private NotificationChannel channel;

    private boolean enabled = true;

    public NotificationPreferenceRequest() {
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}