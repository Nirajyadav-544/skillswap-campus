package com.skillswap.rating.dto;

import com.skillswap.rating.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long ratingId,
        Long sessionId,
        Long authorUserId,
        String authorFirstName,
        String authorLastName,
        String authorEmail,
        Long targetUserId,
        String targetFirstName,
        String targetLastName,
        String targetEmail,
        String content,
        boolean visible,
        boolean edited,
        boolean anonymous,
        String moderationReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime editedAt,
        LocalDateTime deletedAt
) {

    public static ReviewResponse from(Review review) {

        if (review == null) {
            return null;
        }

        var rating = review.getRating();
        var session = review.getSession();
        var author = review.getAuthorUser();
        var target = review.getTargetUser();

        return new ReviewResponse(
                review.getId(),

                rating != null
                        ? rating.getId()
                        : null,

                session != null
                        ? session.getId()
                        : null,

                author != null
                        ? author.getId()
                        : null,

                author != null
                        ? author.getFirstName()
                        : null,

                author != null
                        ? author.getLastName()
                        : null,

                author != null
                        ? author.getEmail()
                        : null,

                target != null
                        ? target.getId()
                        : null,

                target != null
                        ? target.getFirstName()
                        : null,

                target != null
                        ? target.getLastName()
                        : null,

                target != null
                        ? target.getEmail()
                        : null,

                review.getContent(),
                review.isVisible(),
                review.isEdited(),
                review.isAnonymous(),
                review.getModerationReason(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                review.getEditedAt(),
                review.getDeletedAt()
        );
    }

    public String getAuthorFullName() {
        return buildFullName(authorFirstName, authorLastName);
    }

    public String getTargetFullName() {
        return buildFullName(targetFirstName, targetLastName);
    }

    public boolean hasContent() {
        return content != null && !content.isBlank();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isActive() {
        return visible && deletedAt == null;
    }

    private static String buildFullName(
            String firstName,
            String lastName
    ) {
        String first = firstName != null
                ? firstName.trim()
                : "";

        String last = lastName != null
                ? lastName.trim()
                : "";

        if (first.isBlank()) {
            return last;
        }

        if (last.isBlank()) {
            return first;
        }

        return first + " " + last;
    }
}