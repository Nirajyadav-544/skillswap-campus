package com.skillswap.rating.dto;

import com.skillswap.rating.entity.Rating;
import com.skillswap.rating.entity.RatingCategory;

import java.time.LocalDateTime;

public record RatingSummaryResponse(
        Long id,
        Long sessionId,
        Long ratedUserId,
        RatingCategory category,
        Integer score,
        String comment,
        boolean verified,
        boolean edited,
        boolean anonymous,
        LocalDateTime createdAt
) {

    public static RatingSummaryResponse from(Rating rating) {

        if (rating == null) {
            return null;
        }

        return new RatingSummaryResponse(
                rating.getId(),
                rating.getSession() != null
                        ? rating.getSession().getId()
                        : null,
                rating.getRatedUser() != null
                        ? rating.getRatedUser().getId()
                        : null,
                rating.getCategory(),
                rating.getScore(),
                rating.getComment(),
                rating.isVerified(),
                rating.isEdited(),
                rating.isAnonymous(),
                rating.getCreatedAt()
        );
    }

    public boolean hasComment() {
        return comment != null && !comment.isBlank();
    }

    public boolean isPositive() {
        return score != null && score >= 4;
    }

    public boolean isNegative() {
        return score != null && score <= 2;
    }
}