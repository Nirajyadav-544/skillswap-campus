package com.skillswap.matching.dto;

import com.skillswap.skill.entity.ProficiencyLevel;
import com.skillswap.skill.entity.SkillType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Request object used when searching for compatible users.
 *
 * This DTO is intentionally more flexible than the basic
 * recommendation endpoint so the frontend can later build:
 *
 * - skill filters
 * - score filters
 * - proficiency filters
 * - verification filters
 * - experience filters
 * - pagination
 * - sorting
 *
 * without requiring a new endpoint for every filter combination.
 */
public record MatchSearchRequest(

        /**
         * Optional skill ID to match against.
         *
         * If null, the matching service can consider all compatible
         * skills belonging to the requesting user.
         */
        Long skillId,

        /**
         * Optional desired skill type.
         *
         * Typical values:
         *
         *     LEARN
         *     TEACH
         *     BOTH
         */
        SkillType skillType,

        /**
         * Optional minimum proficiency level expected.
         */
        ProficiencyLevel minimumProficiency,

        /**
         * Optional minimum years of experience.
         */
        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Minimum experience cannot be negative"
        )
        @DecimalMax(
                value = "80.0",
                inclusive = true,
                message = "Minimum experience cannot exceed 80 years"
        )
        Double minimumExperience,

        /**
         * Optional minimum overall match score.
         *
         * Range: 0 - 100.
         */
        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Minimum match score cannot be negative"
        )
        @DecimalMax(
                value = "100.0",
                inclusive = true,
                message = "Minimum match score cannot exceed 100"
        )
        Double minimumMatchScore,

        /**
         * Return only verified candidates.
         */
        Boolean verifiedOnly,

        /**
         * Return only candidates with completed profiles.
         */
        Boolean completedProfilesOnly,

        /**
         * Return only candidates who have experience.
         */
        Boolean experiencedOnly,

        /**
         * Optional college filter.
         */
        @Size(
                max = 200,
                message = "College filter cannot exceed 200 characters"
        )
        String college,

        /**
         * Optional department filter.
         */
        @Size(
                max = 150,
                message = "Department filter cannot exceed 150 characters"
        )
        String department,

        /**
         * Optional course filter.
         */
        @Size(
                max = 150,
                message = "Course filter cannot exceed 150 characters"
        )
        String course,

        /**
         * Optional location filter.
         */
        @Size(
                max = 200,
                message = "Location filter cannot exceed 200 characters"
        )
        String location,

        /**
         * Page number.
         */
        @Min(
                value = 0,
                message = "Page number cannot be negative"
        )
        Integer page,

        /**
         * Page size.
         */
        @Min(
                value = 1,
                message = "Page size must be at least 1"
        )
        @Max(
                value = 100,
                message = "Page size cannot exceed 100"
        )
        Integer size,

        /**
         * Sorting field.
         *
         * Supported logical values will be interpreted by the
         * matching service rather than passed directly into SQL.
         */
        String sortBy,

        /**
         * Sort direction.
         */
        String sortDirection
) {

    /**
     * Compact constructor used for safe normalization.
     */
    public MatchSearchRequest {

        /*
         * Normalize experience.
         */
        if (minimumExperience != null) {

            if (minimumExperience < 0.0) {
                minimumExperience = 0.0;
            }

            if (minimumExperience > 80.0) {
                minimumExperience = 80.0;
            }
        }

        /*
         * Normalize match score.
         */
        if (minimumMatchScore != null) {

            if (minimumMatchScore < 0.0) {
                minimumMatchScore = 0.0;
            }

            if (minimumMatchScore > 100.0) {
                minimumMatchScore = 100.0;
            }
        }

        /*
         * Normalize text filters.
         */
        college = normalizeText(college);
        department = normalizeText(department);
        course = normalizeText(course);
        location = normalizeText(location);

        /*
         * Normalize pagination.
         */
        if (page == null || page < 0) {
            page = 0;
        }

        if (size == null || size < 1) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }

        /*
         * Normalize sorting.
         */
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "score";
        } else {
            sortBy = sortBy.trim().toLowerCase();
        }

        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "desc";
        } else {
            sortDirection = sortDirection.trim().toLowerCase();
        }

        if (!sortDirection.equals("asc")
                && !sortDirection.equals("desc")) {

            sortDirection = "desc";
        }
    }

    /**
     * Trims optional text and converts blank strings to null.
     */
    private static String normalizeText(String value) {

        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isBlank()
                ? null
                : normalized;
    }

    /**
     * Returns true when a specific skill is requested.
     */
    public boolean hasSkillId() {
        return skillId != null && skillId > 0;
    }

    /**
     * Returns true when a skill type filter exists.
     */
    public boolean hasSkillType() {
        return skillType != null;
    }

    /**
     * Returns true when a minimum proficiency filter exists.
     */
    public boolean hasMinimumProficiency() {
        return minimumProficiency != null;
    }

    /**
     * Returns true when experience filtering is requested.
     */
    public boolean hasMinimumExperience() {
        return minimumExperience != null
                && minimumExperience > 0.0;
    }

    /**
     * Returns true when score filtering is requested.
     */
    public boolean hasMinimumMatchScore() {
        return minimumMatchScore != null
                && minimumMatchScore > 0.0;
    }

    /**
     * Returns true when verified candidates are required.
     */
    public boolean isVerifiedOnly() {
        return Boolean.TRUE.equals(verifiedOnly);
    }

    /**
     * Returns true when completed profiles are required.
     */
    public boolean isCompletedProfilesOnly() {
        return Boolean.TRUE.equals(completedProfilesOnly);
    }

    /**
     * Returns true when experienced candidates are required.
     */
    public boolean isExperiencedOnly() {
        return Boolean.TRUE.equals(experiencedOnly);
    }

    /**
     * Returns true when college filtering is requested.
     */
    public boolean hasCollege() {
        return college != null;
    }

    /**
     * Returns true when department filtering is requested.
     */
    public boolean hasDepartment() {
        return department != null;
    }

    /**
     * Returns true when course filtering is requested.
     */
    public boolean hasCourse() {
        return course != null;
    }

    /**
     * Returns true when location filtering is requested.
     */
    public boolean hasLocation() {
        return location != null;
    }

    /**
     * Returns true when sorting should be ascending.
     */
    public boolean isAscending() {
        return "asc".equalsIgnoreCase(sortDirection);
    }

    /**
     * Returns true when sorting should be descending.
     */
    public boolean isDescending() {
        return !isAscending();
    }

    /**
     * Returns a normalized safe page number.
     */
    public int getSafePage() {
        return page == null || page < 0
                ? 0
                : page;
    }

    /**
     * Returns a normalized safe page size.
     */
    public int getSafeSize() {

        if (size == null || size < 1) {
            return 20;
        }

        return Math.min(size, 100);
    }

    /**
     * Returns the requested minimum score or the default
     * recommendation threshold.
     */
    public double getEffectiveMinimumMatchScore() {

        if (minimumMatchScore == null) {
            return 0.0;
        }

        return Math.max(
                0.0,
                Math.min(100.0, minimumMatchScore)
        );
    }

    /**
     * Returns the requested minimum experience or zero.
     */
    public double getEffectiveMinimumExperience() {

        if (minimumExperience == null) {
            return 0.0;
        }

        return Math.max(
                0.0,
                Math.min(80.0, minimumExperience)
        );
    }

    /**
     * Returns the effective sorting field.
     *
     * The service can use this value to choose a safe
     * predefined Sort object.
     */
    public String getEffectiveSortBy() {

        if (sortBy == null || sortBy.isBlank()) {
            return "score";
        }

        return sortBy.trim().toLowerCase();
    }

    /**
     * Returns whether this request contains any explicit
     * filtering criteria.
     */
    public boolean hasFilters() {

        return hasSkillId()
                || hasSkillType()
                || hasMinimumProficiency()
                || hasMinimumExperience()
                || hasMinimumMatchScore()
                || isVerifiedOnly()
                || isCompletedProfilesOnly()
                || isExperiencedOnly()
                || hasCollege()
                || hasDepartment()
                || hasCourse()
                || hasLocation();
    }

    /**
     * Returns a human-readable summary useful for logging.
     *
     * No sensitive information is included.
     */
    public String summary() {

        return "MatchSearchRequest{" +
                "skillId=" + skillId +
                ", skillType=" + skillType +
                ", minimumProficiency=" + minimumProficiency +
                ", minimumExperience=" + minimumExperience +
                ", minimumMatchScore=" + minimumMatchScore +
                ", verifiedOnly=" + verifiedOnly +
                ", completedProfilesOnly=" + completedProfilesOnly +
                ", experiencedOnly=" + experiencedOnly +
                ", college='" + college + '\'' +
                ", department='" + department + '\'' +
                ", course='" + course + '\'' +
                ", location='" + location + '\'' +
                ", page=" + getSafePage() +
                ", size=" + getSafeSize() +
                ", sortBy='" + getEffectiveSortBy() + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}