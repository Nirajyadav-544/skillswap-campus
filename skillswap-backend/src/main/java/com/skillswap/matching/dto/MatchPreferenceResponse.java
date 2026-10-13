package com.skillswap.matching.dto;

import com.skillswap.matching.entity.MatchPreference;
import com.skillswap.skill.entity.ProficiencyLevel;
import com.skillswap.skill.entity.SkillCategory;

import java.time.LocalDateTime;

/**
 * Response DTO for a user's matching preferences.
 */
public record MatchPreferenceResponse(

        Long id,

        Long userId,

        boolean active,

        double minimumMatchScore,

        ProficiencyLevel minimumProficiency,

        SkillCategory preferredCategory,

        double minimumTeacherExperience,

        boolean preferVerifiedUsers,

        boolean preferExperiencedTeachers,

        boolean preferRatedUsers,

        boolean preferCompleteProfiles,

        boolean allowOnline,

        boolean allowOffline,

        int maximumRecommendations,

        boolean preferSameCollege,

        boolean preferSameDepartment,

        boolean preferSameCourse,

        String preferredLocation,

        String preferredAvailability,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {

    /**
     * Normalizes values exposed by the response.
     */
    public MatchPreferenceResponse {

        minimumMatchScore = normalizeScore(minimumMatchScore);

        minimumTeacherExperience =
                Math.max(
                        0.0,
                        Math.min(
                                80.0,
                                minimumTeacherExperience
                        )
                );

        maximumRecommendations =
                Math.max(
                        1,
                        Math.min(
                                100,
                                maximumRecommendations
                        )
                );

        if (preferredLocation != null) {
            preferredLocation = preferredLocation.trim();

            if (preferredLocation.isBlank()) {
                preferredLocation = null;
            }
        }

        if (preferredAvailability != null) {
            preferredAvailability = preferredAvailability.trim();

            if (preferredAvailability.isBlank()) {
                preferredAvailability = null;
            }
        }
    }

    /**
     * Creates a response from MatchPreference.
     */
    public static MatchPreferenceResponse from(
            MatchPreference preference
    ) {

        if (preference == null) {
            throw new IllegalArgumentException(
                    "Match preference cannot be null"
            );
        }

        return new MatchPreferenceResponse(

                preference.getId(),

                preference.getUser() != null
                        ? preference.getUser().getId()
                        : null,

                preference.isActive(),

                preference.getMinimumMatchScore(),

                preference.getMinimumProficiency(),

                preference.getPreferredCategory(),

                preference.getMinimumTeacherExperience(),

                preference.isPreferVerifiedUsers(),

                preference.isPreferExperiencedTeachers(),

                preference.isPreferRatedUsers(),

                preference.isPreferCompleteProfiles(),

                preference.isAllowOnline(),

                preference.isAllowOffline(),

                preference.getMaximumRecommendations(),

                preference.isPreferSameCollege(),

                preference.isPreferSameDepartment(),

                preference.isPreferSameCourse(),

                preference.getPreferredLocation(),

                preference.getPreferredAvailability(),

                preference.getCreatedAt(),

                preference.getUpdatedAt()
        );
    }

    /**
     * Keeps score between 0 and 100.
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
     * Returns whether this preference configuration can
     * currently be used by the matching engine.
     */
    public boolean isUsable() {

        return active
                && minimumMatchScore >= 0.0
                && minimumMatchScore <= 100.0
                && maximumRecommendations > 0;
    }

    /**
     * Returns whether at least one interaction mode is enabled.
     */
    public boolean hasAvailableMode() {

        return allowOnline || allowOffline;
    }

    /**
     * Returns whether a minimum proficiency requirement exists.
     */
    public boolean hasMinimumProficiency() {

        return minimumProficiency != null;
    }

    /**
     * Returns whether a category preference exists.
     */
    public boolean hasPreferredCategory() {

        return preferredCategory != null;
    }

    /**
     * Returns whether a location preference exists.
     */
    public boolean hasPreferredLocation() {

        return preferredLocation != null
                && !preferredLocation.isBlank();
    }

    /**
     * Returns whether an availability preference exists.
     */
    public boolean hasPreferredAvailability() {

        return preferredAvailability != null
                && !preferredAvailability.isBlank();
    }

    /**
     * Returns whether any preference boost is enabled.
     */
    public boolean hasPreferenceBoosts() {

        return preferVerifiedUsers
                || preferExperiencedTeachers
                || preferRatedUsers
                || preferCompleteProfiles
                || preferSameCollege
                || preferSameDepartment
                || preferSameCourse;
    }

    /**
     * Returns a simple human-readable summary of the
     * configured recommendation limit.
     */
    public String getRecommendationLimitLabel() {

        return maximumRecommendations
                + " recommendations";
    }
}