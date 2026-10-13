package com.skillswap.notification.dto;

import com.skillswap.notification.entity.Notification;
import com.skillswap.notification.entity.NotificationStatus;
import com.skillswap.notification.entity.NotificationType;

import java.time.LocalDateTime;

public class NotificationSummaryResponse {

    private Long id;
    private NotificationType type;
    private NotificationStatus status;
    private String title;
    private String message;
    private String referenceType;
    private Long referenceId;
    private String actionUrl;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    public NotificationSummaryResponse() {
    }

    public static NotificationSummaryResponse from(Notification notification) {
        NotificationSummaryResponse response = new NotificationSummaryResponse();

        response.id = notification.getId();
        response.type = notification.getType();
        response.status = notification.getStatus();
        response.title = notification.getTitle();
        response.message = notification.getMessage();
        response.referenceType = notification.getReferenceType();
        response.referenceId = notification.getReferenceId();
        response.actionUrl = notification.getActionUrl();
        response.readAt = notification.getReadAt();
        response.createdAt = notification.getCreatedAt();

        return response;
    }

    public Long getId() {
        return id;
    }

    public NotificationType getType() {
        return type;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}