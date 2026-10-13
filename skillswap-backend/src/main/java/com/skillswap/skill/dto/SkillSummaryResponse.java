package com.skillswap.skill.dto;

import com.skillswap.skill.entity.Skill;
import com.skillswap.skill.entity.SkillCategory;

/**
 * Lightweight Skill representation.
 *
 * Intended for:
 *
 * - Search results
 * - Dropdowns
 * - Autocomplete
 * - Discovery pages
 * - Skill selection
 * - UserSkill creation forms
 *
 * Keeping this response smaller prevents unnecessary data
 * from being transferred for high-volume discovery requests.
 */
public record SkillSummaryResponse(

        Long id,

        String name,

        SkillCategory category,

        boolean active,

        long userCount,

        long popularityScore

) {

    /**
     * Ensures counters remain safe at the API boundary.
     */
    public SkillSummaryResponse {

        if (userCount < 0) {
            userCount = 0;
        }

        if (popularityScore < 0) {
            popularityScore = 0;
        }
    }

    /**
     * Converts a Skill entity into a lightweight
     * SkillSummaryResponse.
     */
    public static SkillSummaryResponse from(
            Skill skill
    ) {

        if (skill == null) {
            return null;
        }

        return new SkillSummaryResponse(
                skill.getId(),
                skill.getName(),
                skill.getCategory(),
                skill.isActive(),
                skill.getUserCount(),
                skill.getPopularityScore()
        );
    }
}