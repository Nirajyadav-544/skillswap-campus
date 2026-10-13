package com.skillswap.skill.dto;

import com.skillswap.skill.entity.SkillCategory;

import java.time.LocalDateTime;

/**
 * Full response representation of a Skill.
 *
 * Used when the API needs to expose complete catalog
 * information.
 */
public record SkillResponse(

        Long id,

        String name,

        String description,

        SkillCategory category,

        boolean active,

        boolean systemDefined,

        long popularityScore,

        long userCount,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {

    /**
     * Compact constructor normalizes response values so that
     * invalid negative counters cannot accidentally appear
     * in API responses.
     */
    public SkillResponse {

        if (popularityScore < 0) {
            popularityScore = 0;
        }

        if (userCount < 0) {
            userCount = 0;
        }
    }
}