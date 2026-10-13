package com.skillswap.matching.dto;

import com.skillswap.matching.entity.MatchFeedback;
import com.skillswap.matching.entity.MatchFeedbackType;

import java.time.LocalDateTime;

/**
 * Response returned after feedback is created or retrieved.
 */
public record MatchFeedbackResponse(

        Long id,

        Long userId,

        Long matchResultId,

        Long candidateUserId,

        String candidateFirstName,

        String candidateLastName,

        MatchFeedbackType feedbackType,

        Integer rating,

        String comment,

        boolean explicitFeedback,

        boolean processed,

        String source,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {

    public MatchFeedbackResponse {

        if (rating != null) {
            rating = Math.max(1, Math.min(5, rating));
        }

        if (comment != null) {

            comment = comment.trim();

            if (comment.isBlank()) {
                comment = null;
            }
        }

        if (source != null) {

            source = source.trim();

            if (source.isBlank()) {
                source = null;
            }
        }
    }

    /**
     * Converts a MatchFeedback entity into a response DTO.
     */
    public static MatchFeedbackResponse from(MatchFeedback feedback) {

        if (feedback == null) {
            throw new IllegalArgumentException(
                    "Match feedback cannot be null"
            );
        }

        var user = feedback.getUser();
        var matchResult = feedback.getMatchResult();

        var candidate = matchResult != null
                ? matchResult.getCandidateUser()
                : null;

        return new MatchFeedbackResponse(

                feedback.getId(),

                user != null
                        ? user.getId()
                        : null,

                matchResult != null
                        ? matchResult.getId()
                        : null,

                candidate != null
                        ? candidate.getId()
                        : null,

                candidate != null
                        ? candidate.getFirstName()
                        : null,

                candidate != null
                        ? candidate.getLastName()
                        : null,

                feedback.getFeedbackType(),

                feedback.getRating(),

                feedback.getComment(),

                feedback.isExplicitFeedback(),

                feedback.isProcessed(),

                feedback.getSource(),

                feedback.getCreatedAt(),

                feedback.getUpdatedAt()
        );
    }

    /**
     * Returns whether this feedback contains a rating.
     */
    public boolean hasRating() {
        return rating != null;
    }

    /**
     * Returns whether this feedback contains a comment.
     */
    public boolean hasComment() {
        return comment != null && !comment.isBlank();
    }

    /**
     * Returns whether this feedback contains a source.
     */
    public boolean hasSource() {
        return source != null && !source.isBlank();
    }

    /**
     * Returns whether this feedback represents positive behavior.
     */
    public boolean isPositiveFeedback() {

        return feedbackType == MatchFeedbackType.INTERESTED
                || feedbackType == MatchFeedbackType.HELPFUL
                || feedbackType == MatchFeedbackType.RELEVANT;
    }

    /**
     * Returns whether this feedback represents negative behavior.
     */
    public boolean isNegativeFeedback() {

        return feedbackType == MatchFeedbackType.NOT_INTERESTED
                || feedbackType == MatchFeedbackType.NOT_HELPFUL
                || feedbackType == MatchFeedbackType.NOT_RELEVANT
                || feedbackType == MatchFeedbackType.DISMISSED;
    }

    /**
     * Returns the candidate's full name.
     */
    public String getCandidateFullName() {

        String first = candidateFirstName == null
                ? ""
                : candidateFirstName.trim();

        String last = candidateLastName == null
                ? ""
                : candidateLastName.trim();

        String fullName = (first + " " + last).trim();

        return fullName.isBlank()
                ? null
                : fullName;
    }

    /**
     * Returns whether the feedback has already been processed.
     */
    public boolean isProcessedFeedback() {
        return processed;
    }

    /**
     * Returns whether the feedback is still waiting
     * for recommendation-learning processing.
     */
    public boolean isPendingProcessing() {
        return !processed;
    }
}