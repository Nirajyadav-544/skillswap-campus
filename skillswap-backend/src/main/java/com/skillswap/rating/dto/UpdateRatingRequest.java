package com.skillswap.rating.dto;

import com.skillswap.rating.entity.RatingCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class UpdateRatingRequest {

    private RatingCategory category;

    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 5, message = "Score must not exceed 5")
    private Integer score;

    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    private String comment;

    private Boolean anonymous;

    public UpdateRatingRequest() {
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

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public void normalize() {
        if (comment != null) {
            comment = comment.trim();

            if (comment.isBlank()) {
                comment = null;
            }
        }
    }

    public boolean isEmpty() {
        return category == null
                && score == null
                && comment == null
                && anonymous == null;
    }

    public boolean hasScoreUpdate() {
        return score != null;
    }

    public boolean hasCategoryUpdate() {
        return category != null;
    }

    public boolean hasCommentUpdate() {
        return comment != null;
    }

    public boolean hasAnonymousUpdate() {
        return anonymous != null;
    }
}