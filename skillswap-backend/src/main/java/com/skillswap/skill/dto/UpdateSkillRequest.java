package com.skillswap.skill.dto;

import com.skillswap.skill.entity.SkillCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateSkillRequest(

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
        SkillCategory category,

        @NotNull(message = "Active status is required")
        Boolean active

) {

    public UpdateSkillRequest {

        /*
         * Keep skill names consistent throughout the system.
         */
        if (name != null) {

            name = name
                    .trim()
                    .replaceAll("\\s+", " ");
        }

        /*
         * Convert blank descriptions to null.
         */
        if (description != null) {

            description = description.trim();

            if (description.isBlank()) {
                description = null;
            }
        }

        /*
         * The active value is intentionally not defaulted here.
         *
         * An update request must explicitly specify whether
         * the skill should remain active or become inactive.
         */
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    public boolean shouldBeActive() {
        return Boolean.TRUE.equals(active);
    }

    public boolean shouldBeInactive() {
        return Boolean.FALSE.equals(active);
    }
}