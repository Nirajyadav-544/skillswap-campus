package com.skillswap.notification.dto;

import com.skillswap.notification.entity.NotificationChannel;
import com.skillswap.notification.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CreateNotificationRequest {

    @NotNull
    private Long userId;

    @NotNull
    private NotificationType type;

    private NotificationChannel channel;

    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String message;

    @Size(max = 100)
    private String referenceType;

    private Long referenceId;

    @Size(max = 500)
    private String actionUrl;

    private LocalDateTime expiresAt;

    public CreateNotificationRequest() {
    }

    public void normalize() {
        if (title != null) {
            title = title.trim();
        }

        if (message != null) {
            message = message.trim();
        }

        if (referenceType != null) {
            referenceType = referenceType.trim();
        }

        if (actionUrl != null) {
            actionUrl = actionUrl.trim();
        }

        if (channel == null) {
            channel = NotificationChannel.IN_APP;
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}