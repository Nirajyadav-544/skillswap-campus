package com.skillswap.matching.dto;

import com.skillswap.skill.entity.ProficiencyLevel;
import com.skillswap.skill.entity.SkillCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Request DTO used to create or update matching preferences.
 *
 * All fields are optional so the same DTO can be used for
 * partial preference updates.
 */
public record MatchPreferenceRequest(

        /**
         * Whether this preference configuration is active.
         */
        Boolean active,

        /**
         * Minimum acceptable match score.
         */
        @DecimalMin(
                value = "0.0",
                message = "Minimum match score cannot be less than 0"
        )
        @DecimalMax(
                value = "100.0",
                message = "Minimum match score cannot be greater than 100"
        )
        Double minimumMatchScore,

        /**
         * Minimum proficiency expected from a teacher.
         */
        ProficiencyLevel minimumProficiency,

        /**
         * Preferred skill category.
         */
        SkillCategory preferredCategory,

        /**
         * Minimum teaching experience required.
         */
        @DecimalMin(
                value = "0.0",
                message = "Minimum teacher experience cannot be negative"
        )
        @DecimalMax(
                value = "80.0",
                message = "Minimum teacher experience cannot be greater than 80"
        )
        Double minimumTeacherExperience,

        /**
         * Whether verified users should receive a preference boost.
         */
        Boolean preferVerifiedUsers,

        /**
         * Whether experienced teachers should receive a preference boost.
         */
        Boolean preferExperiencedTeachers,

        /**
         * Whether rated users should receive a preference boost.
         */
        Boolean preferRatedUsers,

        /**
         * Whether profile completion should be considered.
         */
        Boolean preferCompleteProfiles,

        /**
         * Whether online matches are allowed.
         */
        Boolean allowOnline,

        /**
         * Whether offline matches are allowed.
         */
        Boolean allowOffline,

        /**
         * Maximum number of recommendations.
         */
        @Min(
                value = 1,
                message = "Maximum recommendations must be at least 1"
        )
        @Max(
                value = 100,
                message = "Maximum recommendations cannot exceed 100"
        )
        Integer maximumRecommendations,

        /**
         * Whether same-college users should receive a preference boost.
         */
        Boolean preferSameCollege,

        /**
         * Whether same-department users should receive a preference boost.
         */
        Boolean preferSameDepartment,

        /**
         * Whether same-course users should receive a preference boost.
         */
        Boolean preferSameCourse,

        /**
         * Optional preferred location.
         */
        @Size(
                max = 200,
                message = "Preferred location cannot exceed 200 characters"
        )
        String preferredLocation,

        /**
         * Optional preferred availability.
         */
        @Size(
                max = 500,
                message = "Preferred availability cannot exceed 500 characters"
        )
        String preferredAvailability
) {

    /**
     * Returns a request with string fields normalized.
     */
    public MatchPreferenceRequest normalized() {

        String normalizedLocation =
                preferredLocation == null
                        ? null
                        : preferredLocation.trim();

        if (normalizedLocation != null
                && normalizedLocation.isBlank()) {
            normalizedLocation = null;
        }

        String normalizedAvailability =
                preferredAvailability == null
                        ? null
                        : preferredAvailability.trim();

        if (normalizedAvailability != null
                && normalizedAvailability.isBlank()) {
            normalizedAvailability = null;
        }

        return new MatchPreferenceRequest(
                active,
                minimumMatchScore,
                minimumProficiency,
                preferredCategory,
                minimumTeacherExperience,
                preferVerifiedUsers,
                preferExperiencedTeachers,
                preferRatedUsers,
                preferCompleteProfiles,
                allowOnline,
                allowOffline,
                maximumRecommendations,
                preferSameCollege,
                preferSameDepartment,
                preferSameCourse,
                normalizedLocation,
                normalizedAvailability
        );
    }

    /**
     * Returns true when at least one interaction mode is enabled.
     *
     * Null values are treated as "not changing the existing value",
     * therefore this helper is mainly useful when both values are
     * explicitly supplied.
     */
    public boolean hasExplicitInteractionMode() {
        return allowOnline != null
                || allowOffline != null;
    }

    /**
     * Returns true when the request contains no preference values.
     */
    public boolean isEmpty() {

        return active == null
                && minimumMatchScore == null
                && minimumProficiency == null
                && preferredCategory == null
                && minimumTeacherExperience == null
                && preferVerifiedUsers == null
                && preferExperiencedTeachers == null
                && preferRatedUsers == null
                && preferCompleteProfiles == null
                && allowOnline == null
                && allowOffline == null
                && maximumRecommendations == null
                && preferSameCollege == null
                && preferSameDepartment == null
                && preferSameCourse == null
                && preferredLocation == null
                && preferredAvailability == null;
    }
}