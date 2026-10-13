package com.skillswap.matching.dto;

import com.skillswap.matching.entity.MatchFeedbackType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Request used when a user submits feedback about a match.
 *
 * The matchResultId identifies the match receiving feedback.
 */
public record MatchFeedbackRequest(

        Long matchResultId,

        MatchFeedbackType feedbackType,

        @Min(
                value = 1,
                message = "Rating must be at least 1"
        )
        @Max(
                value = 5,
                message = "Rating cannot be greater than 5"
        )
        Integer rating,

        @Size(
                max = 2000,
                message = "Comment cannot exceed 2000 characters"
        )
        String comment,

        Boolean explicitFeedback,

        @Size(
                max = 50,
                message = "Source cannot exceed 50 characters"
        )
        String source
) {

    /**
     * Returns a normalized copy of this request.
     */
    public MatchFeedbackRequest normalized() {

        String normalizedComment = comment;

        if (normalizedComment != null) {
            normalizedComment = normalizedComment.trim();

            if (normalizedComment.isBlank()) {
                normalizedComment = null;
            }
        }

        String normalizedSource = source;

        if (normalizedSource != null) {
            normalizedSource = normalizedSource.trim();

            if (normalizedSource.isBlank()) {
                normalizedSource = null;
            }
        }

        return new MatchFeedbackRequest(
                matchResultId,
                feedbackType,
                rating,
                normalizedComment,
                explicitFeedback,
                normalizedSource
        );
    }

    /**
     * Returns whether the request contains a numeric rating.
     */
    public boolean hasRating() {
        return rating != null;
    }

    /**
     * Returns whether the request contains a comment.
     */
    public boolean hasComment() {
        return comment != null && !comment.isBlank();
    }

    /**
     * Returns whether the request contains a source.
     */
    public boolean hasSource() {
        return source != null && !source.isBlank();
    }

    /**
     * Returns whether the request contains a feedback type.
     */
    public boolean hasFeedbackType() {
        return feedbackType != null;
    }

    /**
     * Returns whether the request contains a match result ID.
     */
    public boolean hasMatchResultId() {
        return matchResultId != null;
    }

    /**
     * Returns whether this request is completely empty.
     */
    public boolean isEmpty() {

        return matchResultId == null
                && feedbackType == null
                && rating == null
                && (comment == null || comment.isBlank())
                && explicitFeedback == null
                && (source == null || source.isBlank());
    }
}