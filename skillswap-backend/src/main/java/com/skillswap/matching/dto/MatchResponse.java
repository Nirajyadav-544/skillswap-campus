package com.skillswap.matching.dto;

import com.skillswap.identity.entity.User;
import com.skillswap.matching.entity.MatchAlgorithm;
import com.skillswap.matching.entity.MatchResult;
import com.skillswap.matching.entity.MatchStatus;

import java.time.LocalDateTime;

/**
 * API representation of a matching result.
 *
 * This DTO keeps the JPA MatchResult entity away from
 * the controller response layer.
 *
 * This response is based only on fields actually available
 * in MatchResult.
 */
public record MatchResponse(

        Long id,

        Long requesterUserId,

        Long candidateUserId,

        String candidateFirstName,

        String candidateLastName,

        String candidateEmail,

        MatchStatus status,

        MatchAlgorithm algorithm,

        double overallScore,

        int matchingSkillCount,

        boolean viewed,

        boolean interested,

        boolean dismissed,

        String explanation,

        LocalDateTime generatedAt,

        LocalDateTime updatedAt,

        LocalDateTime expiresAt
) {

    /**
     * Canonical constructor.
     *
     * Normalizes score, matching skill count and explanation.
     */
    public MatchResponse {

        overallScore = clampScore(overallScore);

        matchingSkillCount = Math.max(0, matchingSkillCount);

        if (explanation != null) {
            explanation = explanation.trim();

            if (explanation.isBlank()) {
                explanation = null;
            }
        }
    }

    /**
     * Keeps score inside the supported 0-100 range.
     */
    private static double clampScore(double score) {

        if (Double.isNaN(score) || Double.isInfinite(score)) {
            return 0.0;
        }

        return Math.max(0.0, Math.min(100.0, score));
    }

    /**
     * Converts a MatchResult entity into MatchResponse.
     *
     * Only properties that actually exist in MatchResult
     * are used here.
     */
    public static MatchResponse from(MatchResult matchResult) {

        if (matchResult == null) {
            throw new IllegalArgumentException(
                    "Match result cannot be null"
            );
        }

        User requester = matchResult.getRequesterUser();
        User candidate = matchResult.getCandidateUser();

        return new MatchResponse(

                matchResult.getId(),

                requester != null
                        ? requester.getId()
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

                candidate != null
                        ? candidate.getEmail()
                        : null,

                matchResult.getStatus(),

                matchResult.getAlgorithm(),

                matchResult.getOverallScore(),

                matchResult.getMatchingSkillCount(),

                matchResult.isViewed(),

                matchResult.getInterestedAt() != null,

                matchResult.getDismissedAt() != null,

                matchResult.getMatchReason(),

                matchResult.getGeneratedAt(),

                matchResult.getUpdatedAt(),

                matchResult.getExpiresAt()
        );
    }

    /**
     * Returns true when the match has a high overall score.
     */
    public boolean isHighQualityMatch() {
        return overallScore >= 80.0;
    }

    /**
     * Returns true when the match has a strong overall score.
     */
    public boolean isStrongMatch() {
        return overallScore >= 70.0;
    }

    /**
     * Returns true when at least one matching skill exists.
     */
    public boolean hasMatchingSkills() {
        return matchingSkillCount > 0;
    }

    /**
     * Returns true when this match can still be displayed
     * as an active recommendation.
     *
     * MatchStatus does not contain an ACTIVE value.
     * MatchResult.isDisplayable() is the source of truth.
     */
    public boolean isActiveRecommendation() {

        if (status == null) {
            return false;
        }

        return status != MatchStatus.DISMISSED
                && status != MatchStatus.EXPIRED
                && status != MatchStatus.ARCHIVED;
    }

    /**
     * Returns true when the match has not been viewed
     * and has not been dismissed.
     */
    public boolean requiresAttention() {
        return !viewed && !dismissed;
    }

    /**
     * Returns the candidate's full name.
     */
    public String getCandidateFullName() {

        if (candidateFirstName == null
                && candidateLastName == null) {
            return null;
        }

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
     * Returns true when the match has been explicitly
     * marked as interested.
     */
    public boolean isInterestedMatch() {
        return interested;
    }

    /**
     * Returns true when the match has been explicitly
     * dismissed.
     */
    public boolean isDismissedMatch() {
        return dismissed;
    }

    /**
     * Returns true when the match is still in the
     * calculated state and has not been viewed.
     */
    public boolean isNewMatch() {
        return status == MatchStatus.CALCULATED && !viewed;
    }

    /**
     * Returns true when the match has been viewed.
     */
    public boolean isViewedMatch() {
        return viewed;
    }

    /**
     * Returns true when an explanation/reason is available.
     */
    public boolean hasExplanation() {
        return explanation != null && !explanation.isBlank();
    }

    /**
     * Returns true when this match has expired.
     *
     * This checks both the explicit EXPIRED status and
     * the expiration timestamp.
     */
    public boolean isExpiredMatch() {

        if (status == MatchStatus.EXPIRED) {
            return true;
        }

        return expiresAt != null
                && expiresAt.isBefore(LocalDateTime.now());
    }

    /**
     * Returns true when the match has been archived.
     */
    public boolean isArchivedMatch() {
        return status == MatchStatus.ARCHIVED;
    }

    /**
     * Returns true when the match has been dismissed,
     * expired or archived.
     */
    public boolean isClosedMatch() {

        return status == MatchStatus.DISMISSED
                || status == MatchStatus.EXPIRED
                || status == MatchStatus.ARCHIVED;
    }

    /**
     * Returns a simple score category for API consumers.
     */
    public String getScoreLabel() {

        if (overallScore >= 80.0) {
            return "HIGH";
        }

        if (overallScore >= 70.0) {
            return "STRONG";
        }

        if (overallScore >= 50.0) {
            return "MODERATE";
        }

        return "LOW";
    }
}