package com.skillswap.rating.dto;

import com.skillswap.rating.entity.Rating;
import com.skillswap.rating.entity.RatingCategory;
import com.skillswap.rating.entity.RatingStatus;

import java.time.LocalDateTime;

public record RatingResponse(
        Long id,
        Long sessionId,
        Long raterUserId,
        String raterFirstName,
        String raterLastName,
        String raterEmail,
        Long ratedUserId,
        String ratedFirstName,
        String ratedLastName,
        String ratedEmail,
        RatingCategory category,
        Integer score,
        String comment,
        RatingStatus status,
        boolean verified,
        boolean edited,
        boolean anonymous,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime editedAt
) {

    public static RatingResponse from(Rating rating) {

        if (rating == null) {
            return null;
        }

        var session = rating.getSession();
        var rater = rating.getRaterUser();
        var rated = rating.getRatedUser();

        return new RatingResponse(
                rating.getId(),

                session != null
                        ? session.getId()
                        : null,

                rater != null
                        ? rater.getId()
                        : null,

                rater != null
                        ? rater.getFirstName()
                        : null,

                rater != null
                        ? rater.getLastName()
                        : null,

                rater != null
                        ? rater.getEmail()
                        : null,

                rated != null
                        ? rated.getId()
                        : null,

                rated != null
                        ? rated.getFirstName()
                        : null,

                rated != null
                        ? rated.getLastName()
                        : null,

                rated != null
                        ? rated.getEmail()
                        : null,

                rating.getCategory(),
                rating.getScore(),
                rating.getComment(),
                rating.getStatus(),
                rating.isVerified(),
                rating.isEdited(),
                rating.isAnonymous(),
                rating.getCreatedAt(),
                rating.getUpdatedAt(),
                rating.getEditedAt()
        );
    }

    public String getRaterFullName() {
        return buildFullName(raterFirstName, raterLastName);
    }

    public String getRatedFullName() {
        return buildFullName(ratedFirstName, ratedLastName);
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

    public boolean isNeutral() {
        return score != null && score == 3;
    }

    public String getScoreLabel() {
        if (score == null) {
            return "Not Rated";
        }

        return switch (score) {
            case 1 -> "Very Poor";
            case 2 -> "Poor";
            case 3 -> "Average";
            case 4 -> "Good";
            case 5 -> "Excellent";
            default -> "Unknown";
        };
    }

    private static String buildFullName(
            String firstName,
            String lastName
    ) {
        String first = firstName != null ? firstName.trim() : "";
        String last = lastName != null ? lastName.trim() : "";

        if (first.isBlank()) {
            return last;
        }

        if (last.isBlank()) {
            return first;
        }

        return first + " " + last;
    }
}