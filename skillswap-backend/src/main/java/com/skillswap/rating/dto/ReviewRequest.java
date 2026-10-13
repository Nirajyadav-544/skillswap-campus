package com.skillswap.rating.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReviewRequest {

    private Long ratingId;

    private Long sessionId;

    private Long targetUserId;

    @NotBlank(message = "Review content is required")
    @Size(
            max = 2000,
            message = "Review content must not exceed 2000 characters"
    )
    private String content;

    private boolean anonymous = false;

    public ReviewRequest() {
    }

    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public void normalize() {
        if (content != null) {
            content = content.trim();

            if (content.isBlank()) {
                content = null;
            }
        }
    }

    public boolean hasRatingId() {
        return ratingId != null;
    }

    public boolean hasSessionId() {
        return sessionId != null;
    }

    public boolean hasTargetUserId() {
        return targetUserId != null;
    }

    public boolean hasContent() {
        return content != null && !content.isBlank();
    }
}