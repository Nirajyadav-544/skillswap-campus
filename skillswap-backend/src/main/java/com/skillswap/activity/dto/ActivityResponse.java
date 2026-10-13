package com.skillswap.activity.dto;

import com.skillswap.activity.entity.Activity;
import com.skillswap.activity.entity.ActivityType;

import java.time.LocalDateTime;

public class ActivityResponse {

    private Long id;

    private ActivityType type;

    private String message;

    private Long referenceId;

    private String referenceType;

    private LocalDateTime createdAt;

    public ActivityResponse() {
    }

    public ActivityResponse(
            Long id,
            ActivityType type,
            String message,
            Long referenceId,
            String referenceType,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.createdAt = createdAt;
    }

    public static ActivityResponse from(
            Activity activity
    ) {
        return new ActivityResponse(
                activity.getId(),
                activity.getType(),
                activity.getMessage(),
                activity.getReferenceId(),
                activity.getReferenceType(),
                activity.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public ActivityType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}