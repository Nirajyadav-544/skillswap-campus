package com.skillswap.rating.dto;

import com.skillswap.rating.entity.RatingCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateRatingRequest {

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Rated user ID is required")
    private Long ratedUserId;

    @NotNull(message = "Rating category is required")
    private RatingCategory category = RatingCategory.OVERALL;

    @NotNull(message = "Score is required")
    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 5, message = "Score must not exceed 5")
    private Integer score;

    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    private String comment;

    private boolean anonymous = false;

    public CreateRatingRequest() {
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getRatedUserId() {
        return ratedUserId;
    }

    public void setRatedUserId(Long ratedUserId) {
        this.ratedUserId = ratedUserId;
    }

    public RatingCategory getCategory() {
        return category;
    }

    public void setCategory(RatingCategory category) {
        this.category = category;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public void normalize() {
        if (category == null) {
            category = RatingCategory.OVERALL;
        }

        if (comment != null) {
            comment = comment.trim();

            if (comment.isBlank()) {
                comment = null;
            }
        }
    }

    public boolean hasComment() {
        return comment != null && !comment.isBlank();
    }

    public boolean hasValidScore() {
        return score != null && score >= 1 && score <= 5;
    }
}