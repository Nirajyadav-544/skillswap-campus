package com.skillswap.notification.dto;

import com.skillswap.notification.entity.Notification;
import com.skillswap.notification.entity.NotificationChannel;
import com.skillswap.notification.entity.NotificationStatus;
import com.skillswap.notification.entity.NotificationType;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private Long userId;
    private String userEmail;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String title;
    private String message;
    private String referenceType;
    private Long referenceId;
    private String actionUrl;
    private LocalDateTime readAt;
    private LocalDateTime archivedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NotificationResponse() {
    }

    public static NotificationResponse from(Notification notification) {
        NotificationResponse response = new NotificationResponse();

        response.id = notification.getId();

        if (notification.getUser() != null) {
            response.userId = notification.getUser().getId();
            response.userEmail = notification.getUser().getEmail();
        }

        response.type = notification.getType();
        response.channel = notification.getChannel();
        response.status = notification.getStatus();
        response.title = notification.getTitle();
        response.message = notification.getMessage();
        response.referenceType = notification.getReferenceType();
        response.referenceId = notification.getReferenceId();
        response.actionUrl = notification.getActionUrl();
        response.readAt = notification.getReadAt();
        response.archivedAt = notification.getArchivedAt();
        response.expiresAt = notification.getExpiresAt();
        response.createdAt = notification.getCreatedAt();
        response.updatedAt = notification.getUpdatedAt();

        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public NotificationType getType() {
        return type;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}