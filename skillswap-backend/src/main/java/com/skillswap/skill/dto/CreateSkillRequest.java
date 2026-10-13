package com.skillswap.skill.dto;

import com.skillswap.skill.entity.SkillCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSkillRequest(

        @NotBlank(message = "Skill name is required")
        @Size(
                min = 2,
                max = 150,
                message = "Skill name must be between 2 and 150 characters"
        )
        String name,

        @Size(
                max = 3000,
                message = "Skill description must not exceed 3000 characters"
        )
        String description,

        @NotNull(message = "Skill category is required")
        SkillCategory category

) {

    public CreateSkillRequest {

        /*
         * Normalize the skill name before it reaches the
         * service layer.
         *
         * Example:
         *
         * "   Java   Programming   "
         *
         * becomes:
         *
         * "Java Programming"
         */
        if (name != null) {
            name = name
                    .trim()
                    .replaceAll("\\s+", " ");
        }

        /*
         * Normalize description.
         *
         * Empty descriptions are converted to null so that
         * the database does not receive meaningless whitespace.
         */
        if (description != null) {

            description = description.trim();

            if (description.isBlank()) {
                description = null;
            }
        }
    }
}