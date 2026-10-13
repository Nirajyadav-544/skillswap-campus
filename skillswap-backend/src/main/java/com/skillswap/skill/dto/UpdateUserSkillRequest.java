package com.skillswap.skill.dto;

import com.skillswap.skill.entity.ProficiencyLevel;
import com.skillswap.skill.entity.SkillType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request used to update an existing UserSkill relationship.
 *
 * The skill itself is intentionally not changeable through
 * this DTO.
 *
 * If a user wants a different skill, the recommended operation
 * is to remove the old UserSkill and add the new Skill.
 *
 * This protects the unique user + skill relationship and keeps
 * audit/history semantics clean.
 */
public record UpdateUserSkillRequest(

        /**
         * Teach / Learn / Both.
         */
        @NotNull(
                message = "Skill type is required"
        )
        SkillType skillType,

        /**
         * Current proficiency.
         */
        @NotNull(
                message = "Proficiency level is required"
        )
        ProficiencyLevel proficiencyLevel,

        /**
         * Updated years of experience.
         */
        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Years of experience cannot be negative"
        )
        @DecimalMax(
                value = "80.0",
                inclusive = true,
                message = "Years of experience is invalid"
        )
        double yearsOfExperience,

        /**
         * Optional updated description.
         */
        @Size(
                max = 3000,
                message = "Skill description must not exceed 3000 characters"
        )
        String description,

        /**
         * Allows the user to activate/deactivate the
         * relationship without deleting historical data.
         */
        @NotNull(
                message = "Active status is required"
        )
        Boolean active
) {

    /**
     * Normalizes optional text and protects experience
     * from invalid negative values.
     */
    public UpdateUserSkillRequest {

        if (description != null) {

            description =
                    description.trim();

            if (description.isBlank()) {
                description = null;
            }
        }

        if (yearsOfExperience < 0) {
            yearsOfExperience = 0;
        }
    }
}