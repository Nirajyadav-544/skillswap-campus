package com.skillswap.matching.dto;

import com.skillswap.matching.entity.MatchAlgorithm;
import com.skillswap.matching.entity.MatchResult;
import com.skillswap.matching.entity.MatchStatus;
import com.skillswap.identity.entity.User;

import java.time.LocalDateTime;

/**
 * Represents a recommendation returned to the user.
 */
public record MatchRecommendationResponse(

        Long matchId,

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

        LocalDateTime expiresAt
) {

    public MatchRecommendationResponse {

        overallScore = normalizeScore(overallScore);

        matchingSkillCount =
                Math.max(0, matchingSkillCount);

        if (explanation != null) {
            explanation = explanation.trim();

            if (explanation.isBlank()) {
                explanation = null;
            }
        }
    }

    private static double normalizeScore(double score) {

        if (Double.isNaN(score)
                || Double.isInfinite(score)) {

            return 0.0;
        }

        return Math.max(
                0.0,
                Math.min(100.0, score)
        );
    }

    /**
     * Converts a MatchResult to recommendation response.
     */
    public static MatchRecommendationResponse from(
            MatchResult matchResult
    ) {

        if (matchResult == null) {
            throw new IllegalArgumentException(
                    "Match result cannot be null"
            );
        }

        var candidate =
                matchResult.getCandidateUser();

        return new MatchRecommendationResponse(

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

                matchResult.getExpiresAt()
        );
    }

    public boolean hasMatchingSkills() {
        return matchingSkillCount > 0;
    }

    public boolean hasExplanation() {
        return explanation != null
                && !explanation.isBlank();
    }

    public boolean isNewRecommendation() {
        return status == MatchStatus.CALCULATED
                && !viewed;
    }

    public boolean isViewedRecommendation() {
        return viewed;
    }

    public boolean isInterestedRecommendation() {
        return interested;
    }

    public boolean isDismissedRecommendation() {
        return dismissed;
    }

    public boolean isClosedRecommendation() {

        return status == MatchStatus.DISMISSED
                || status == MatchStatus.EXPIRED
                || status == MatchStatus.ARCHIVED;
    }

    public boolean isAvailableRecommendation() {

        if (isClosedRecommendation()) {
            return false;
        }

        return expiresAt == null
                || expiresAt.isAfter(LocalDateTime.now());
    }

    public boolean isExpiredRecommendation() {

        if (status == MatchStatus.EXPIRED) {
            return true;
        }

        return expiresAt != null
                && expiresAt.isBefore(LocalDateTime.now());
    }

    public String getCandidateFullName() {

        String first =
                candidateFirstName == null
                        ? ""
                        : candidateFirstName.trim();

        String last =
                candidateLastName == null
                        ? ""
                        : candidateLastName.trim();

        String fullName =
                (first + " " + last).trim();

        return fullName.isBlank()
                ? null
                : fullName;
    }

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