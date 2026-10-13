package com.skillswap.skill.dto;

import com.skillswap.skill.entity.ProficiencyLevel;
import com.skillswap.skill.entity.SkillCategory;
import com.skillswap.skill.entity.SkillType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record SkillSearchRequest(

        @Size(
                max = 150,
                message = "Search query must not exceed 150 characters"
        )
        String query,

        SkillCategory category,

        SkillType skillType,

        ProficiencyLevel proficiencyLevel,

        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Minimum experience cannot be negative"
        )
        @DecimalMax(
                value = "80.0",
                inclusive = true,
                message = "Minimum experience is invalid"
        )
        Double minExperience,

        Boolean verifiedOnly,

        Boolean activeOnly,

        @Min(
                value = 0,
                message = "Page number cannot be negative"
        )
        Integer page,

        @Min(
                value = 1,
                message = "Page size must be at least 1"
        )
        @Max(
                value = 100,
                message = "Page size cannot exceed 100"
        )
        Integer size,

        String sortBy,

        String sortDirection
) {

    public SkillSearchRequest {
        if (query != null) {
            query = query.trim();

            if (query.isBlank()) {
                query = null;
            }
        }

        if (minExperience != null && minExperience < 0) {
            minExperience = 0.0;
        }

        if (page == null || page < 0) {
            page = 0;
        }

        if (size == null || size < 1) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "relevance";
        } else {
            sortBy = sortBy.trim().toLowerCase();
        }

        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "desc";
        } else {
            sortDirection = sortDirection.trim().toLowerCase();
        }

        if (!sortDirection.equals("asc") && !sortDirection.equals("desc")) {
            sortDirection = "desc";
        }
    }

    public boolean hasQuery() {
        return query != null && !query.isBlank();
    }

    public boolean hasCategory() {
        return category != null;
    }

    public boolean hasSkillType() {
        return skillType != null;
    }

    public boolean hasProficiencyLevel() {
        return proficiencyLevel != null;
    }

    public boolean hasMinimumExperience() {
        return minExperience != null && minExperience > 0;
    }

    public boolean isVerifiedOnly() {
        return Boolean.TRUE.equals(verifiedOnly);
    }

    public boolean isActiveOnly() {
        return activeOnly == null || activeOnly;
    }

    public boolean isAscending() {
        return "asc".equalsIgnoreCase(sortDirection);
    }

    public boolean isDescending() {
        return !isAscending();
    }
}