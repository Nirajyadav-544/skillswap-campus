package com.skillswap.skill.dto;

import com.skillswap.skill.entity.ProficiencyLevel;
import com.skillswap.skill.entity.SkillType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request used when a user adds a skill to their profile.
 *
 * Example:
 *
 * {
 *   "skillId": 1,
 *   "skillType": "TEACH",
 *   "proficiencyLevel": "ADVANCED",
 *   "yearsOfExperience": 3.5,
 *   "description": "I use Java and Spring Boot for backend projects."
 * }
 */
public record AddUserSkillRequest(

        /**
         * ID of the skill from the global Skill catalog.
         */
        @NotNull(
                message = "Skill ID is required"
        )
        Long skillId,

        /**
         * Whether the user wants to teach, learn,
         * or both.
         */
        @NotNull(
                message = "Skill type is required"
        )
        SkillType skillType,

        /**
         * User's current proficiency level.
         */
        @NotNull(
                message = "Proficiency level is required"
        )
        ProficiencyLevel proficiencyLevel,

        /**
         * Approximate practical experience.
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
         * Optional explanation of the user's experience.
         */
        @Size(
                max = 3000,
                message = "Skill description must not exceed 3000 characters"
        )
        String description
) {

    /**
     * Compact constructor provides safe normalization.
     */
    public AddUserSkillRequest {

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