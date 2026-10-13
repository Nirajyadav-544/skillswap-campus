package com.skillswap.matching.dto;

import com.skillswap.matching.entity.MatchScore;

/**
 * Detailed score breakdown for a MatchResult.
 *
 * This DTO intentionally exposes only fields that actually exist
 * in MatchScore.
 */
public record MatchScoreBreakdownResponse(

        Long matchScoreId,

        Long matchResultId,

        double overallScore,

        double skillScore,

        double skillTypeScore,

        double proficiencyScore,

        double experienceScore,

        double profileScore,

        double availabilityScore,

        double locationScore,

        double verificationScore,

        double ratingScore,

        double collegeScore,

        double departmentScore,

        double courseScore,

        int matchingSkillCount,

        int strongMatchingSkillCount,

        String explanation
) {

    /**
     * Canonical constructor.
     */
    public MatchScoreBreakdownResponse {

        overallScore = normalizeScore(overallScore);
        skillScore = normalizeScore(skillScore);
        skillTypeScore = normalizeScore(skillTypeScore);
        proficiencyScore = normalizeScore(proficiencyScore);
        experienceScore = normalizeScore(experienceScore);
        profileScore = normalizeScore(profileScore);
        availabilityScore = normalizeScore(availabilityScore);
        locationScore = normalizeScore(locationScore);
        verificationScore = normalizeScore(verificationScore);
        ratingScore = normalizeScore(ratingScore);
        collegeScore = normalizeScore(collegeScore);
        departmentScore = normalizeScore(departmentScore);
        courseScore = normalizeScore(courseScore);

        matchingSkillCount = Math.max(
                0,
                matchingSkillCount
        );

        strongMatchingSkillCount = Math.max(
                0,
                Math.min(
                        strongMatchingSkillCount,
                        matchingSkillCount
                )
        );

        if (explanation != null) {
            explanation = explanation.trim();

            if (explanation.isBlank()) {
                explanation = null;
            }
        }
    }

    /**
     * Keeps a score between 0 and 100.
     */
    private static double normalizeScore(double value) {

        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }

        return Math.max(
                0.0,
                Math.min(100.0, value)
        );
    }

    /**
     * Creates this DTO from the actual MatchScore entity.
     */
    public static MatchScoreBreakdownResponse from(
            MatchScore score
    ) {

        if (score == null) {
            throw new IllegalArgumentException(
                    "Match score cannot be null"
            );
        }

        return new MatchScoreBreakdownResponse(

                score.getId(),

                score.getMatchResult() != null
                        ? score.getMatchResult().getId()
                        : null,

                score.getOverallScore(),

                score.getSkillScore(),

                score.getSkillTypeScore(),

                score.getProficiencyScore(),

                score.getExperienceScore(),

                score.getProfileScore(),

                score.getAvailabilityScore(),

                score.getLocationScore(),

                score.getVerificationScore(),

                score.getRatingScore(),

                score.getCollegeScore(),

                score.getDepartmentScore(),

                score.getCourseScore(),

                score.getMatchingSkillCount(),

                score.getStrongMatchingSkillCount(),

                score.getScoreExplanation()
        );
    }

    /**
     * Returns true when skill score is the highest
     * among all available score components.
     */
    public boolean skillIsDominant() {

        return skillScore >= skillTypeScore
                && skillScore >= proficiencyScore
                && skillScore >= experienceScore
                && skillScore >= profileScore
                && skillScore >= availabilityScore
                && skillScore >= locationScore
                && skillScore >= verificationScore
                && skillScore >= ratingScore
                && skillScore >= collegeScore
                && skillScore >= departmentScore
                && skillScore >= courseScore;
    }

    /**
     * Returns true when at least two skills match.
     */
    public boolean hasMultipleMatchingSkills() {
        return matchingSkillCount >= 2;
    }

    /**
     * Returns true when one or more strong skill matches exist.
     */
    public boolean hasStrongMatchingSkills() {
        return strongMatchingSkillCount > 0;
    }

    /**
     * Returns true when matching skills include at least
     * one strong match.
     */
    public boolean hasStrongSkillCompatibility() {

        return matchingSkillCount > 0
                && strongMatchingSkillCount > 0;
    }

    /**
     * Calculates the average of all available score components.
     */
    public double componentAverage() {

        double total =
                skillScore
                        + skillTypeScore
                        + proficiencyScore
                        + experienceScore
                        + profileScore
                        + availabilityScore
                        + locationScore
                        + verificationScore
                        + ratingScore
                        + collegeScore
                        + departmentScore
                        + courseScore;

        return total / 12.0;
    }

    /**
     * Returns true for an overall score of 80 or higher.
     */
    public boolean isStrongMatch() {
        return overallScore >= 80.0;
    }

    /**
     * Returns true for an overall score from 60 to below 80.
     */
    public boolean isModerateMatch() {

        return overallScore >= 60.0
                && overallScore < 80.0;
    }

    /**
     * Returns true for an overall score below 60.
     */
    public boolean isWeakMatch() {
        return overallScore < 60.0;
    }

    /**
     * Returns true when at least one skill matches.
     */
    public boolean hasMatchingSkills() {
        return matchingSkillCount > 0;
    }

    /**
     * Returns true when an explanation exists.
     */
    public boolean hasExplanation() {

        return explanation != null
                && !explanation.isBlank();
    }

    /**
     * Human-readable strength label.
     */
    public String getStrengthLabel() {

        if (overallScore >= 90.0) {
            return "EXCELLENT";
        }

        if (overallScore >= 80.0) {
            return "VERY_STRONG";
        }

        if (overallScore >= 70.0) {
            return "STRONG";
        }

        if (overallScore >= 60.0) {
            return "GOOD";
        }

        if (overallScore >= 40.0) {
            return "MODERATE";
        }

        return "LOW";
    }

    /**
     * Returns the highest individual component score.
     */
    public double getHighestComponentScore() {

        return Math.max(
                Math.max(
                        Math.max(
                                Math.max(
                                        Math.max(
                                                Math.max(
                                                        skillScore,
                                                        skillTypeScore
                                                ),
                                                proficiencyScore
                                        ),
                                        experienceScore
                                ),
                                profileScore
                        ),
                        availabilityScore
                ),
                Math.max(
                        Math.max(
                                Math.max(
                                        Math.max(
                                                Math.max(
                                                        locationScore,
                                                        verificationScore
                                                ),
                                                ratingScore
                                        ),
                                        collegeScore
                                ),
                                departmentScore
                        ),
                        courseScore
                )
        );
    }
}