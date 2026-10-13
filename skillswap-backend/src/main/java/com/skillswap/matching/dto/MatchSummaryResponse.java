package com.skillswap.matching.dto;

import com.skillswap.matching.entity.MatchResult;
import com.skillswap.matching.entity.MatchStatus;

/**
 * Lightweight representation of a match.
 *
 * This DTO is intended for:
 *
 * - recommendation cards
 * - dashboard lists
 * - notification previews
 * - search results
 * - mobile responses
 *
 * The fields are based only on data actually available
 * in MatchResult.
 */
public record MatchSummaryResponse(

        Long id,

        Long candidateUserId,

        String candidateFirstName,

        String candidateLastName,

        MatchStatus status,

        double overallScore,

        int matchingSkillCount,

        boolean viewed,

        boolean interested,

        boolean dismissed
) {

    /**
     * Canonical constructor.
     *
     * Normalizes the overall score and matching skill count.
     */
    public MatchSummaryResponse {

        overallScore = normalizeScore(overallScore);

        matchingSkillCount = Math.max(
                0,
                matchingSkillCount
        );
    }

    /**
     * Keeps the score between 0 and 100.
     */
    private static double normalizeScore(double score) {

        if (Double.isNaN(score) || Double.isInfinite(score)) {
            return 0.0;
        }

        return Math.max(
                0.0,
                Math.min(100.0, score)
        );
    }

    /**
     * Creates a summary response from MatchResult.
     *
     * Only properties that actually exist in MatchResult
     * are used here.
     */
    public static MatchSummaryResponse from(
            MatchResult matchResult
    ) {

        if (matchResult == null) {
            throw new IllegalArgumentException(
                    "Match result cannot be null"
            );
        }

        var candidate = matchResult.getCandidateUser();

        return new MatchSummaryResponse(

                matchResult.getId(),

                candidate != null
                        ? candidate.getId()
                        : null,

                candidate != null
                        ? candidate.getFirstName()
                        : null,

                candidate != null
                        ? candidate.getLastName()
                        : null,

                matchResult.getStatus(),

                matchResult.getOverallScore(),

                matchResult.getMatchingSkillCount(),

                matchResult.isViewed(),

                matchResult.getInterestedAt() != null,

                matchResult.getDismissedAt() != null
        );
    }

    /**
     * Returns true when the match has a high score.
     */
    public boolean isHighQuality() {
        return overallScore >= 80.0;
    }

    /**
     * Returns true when the match has a strong score.
     */
    public boolean isStrong() {
        return overallScore >= 70.0;
    }

    /**
     * Returns true when at least one matching skill exists.
     */
    public boolean hasSkills() {
        return matchingSkillCount > 0;
    }

    /**
     * Returns true when the recommendation has not been viewed.
     */
    public boolean isUnread() {
        return !viewed;
    }

    /**
     * Returns true when the match is still displayable.
     *
     * MatchStatus does not have an ACTIVE value.
     * MatchResult.isDisplayable() is therefore used as
     * the source of truth.
     */
    public boolean isAvailable() {
        return status != null
                && status != MatchStatus.DISMISSED
                && status != MatchStatus.EXPIRED
                && status != MatchStatus.ARCHIVED;
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
     * Returns true when the match is still new.
     */
    public boolean isNewMatch() {
        return status == MatchStatus.CALCULATED
                && !viewed;
    }

    /**
     * Returns true when the match has been viewed.
     */
    public boolean isViewedMatch() {
        return viewed;
    }

    /**
     * Returns true when the match is closed and should
     * not normally appear in active recommendations.
     */
    public boolean isClosedMatch() {

        return status == MatchStatus.DISMISSED
                || status == MatchStatus.EXPIRED
                || status == MatchStatus.ARCHIVED;
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
}