package com.skillswap.skill.dto;

import com.skillswap.skill.entity.ProficiencyLevel;
import com.skillswap.skill.entity.SkillCategory;
import com.skillswap.skill.entity.SkillType;

import java.time.LocalDateTime;

public record UserSkillResponse(
        Long id,

        Long userId,

        Long skillId,
        String skillName,
        String skillDescription,
        SkillCategory skillCategory,

        SkillType skillType,
        ProficiencyLevel proficiencyLevel,

        double yearsOfExperience,
        String description,

        boolean active,
        boolean verified,

        long exchangeCount,
        long successfulExchangeCount,

        double averageRating,
        long ratingCount,

        boolean canTeach,
        boolean wantsToLearn,
        boolean experienced,
        boolean hasRatings,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastUsedAt
) {

    public UserSkillResponse {
        yearsOfExperience = Math.max(0.0, yearsOfExperience);
        exchangeCount = Math.max(0, exchangeCount);
        successfulExchangeCount = Math.max(0, successfulExchangeCount);
        averageRating = Math.max(0.0, Math.min(5.0, averageRating));
        ratingCount = Math.max(0, ratingCount);
    }

    public static UserSkillResponse from(
            com.skillswap.skill.entity.UserSkill userSkill
    ) {
        if (userSkill == null) {
            throw new IllegalArgumentException("User skill cannot be null");
        }

        var user = userSkill.getUser();
        var skill = userSkill.getSkill();

        return new UserSkillResponse(
                userSkill.getId(),

                user != null ? user.getId() : null,

                skill != null ? skill.getId() : null,
                skill != null ? skill.getName() : null,
                skill != null ? skill.getDescription() : null,
                skill != null ? skill.getCategory() : null,

                userSkill.getSkillType(),
                userSkill.getProficiencyLevel(),

                userSkill.getYearsOfExperience(),
                userSkill.getDescription(),

                userSkill.isActive(),
                userSkill.isVerified(),

                userSkill.getExchangeCount(),
                userSkill.getSuccessfulExchangeCount(),

                userSkill.getAverageRating(),
                userSkill.getRatingCount(),

                userSkill.canTeach(),
                userSkill.wantsToLearn(),
                userSkill.isExperienced(),
                userSkill.hasRatings(),

                userSkill.getCreatedAt(),
                userSkill.getUpdatedAt(),
                userSkill.getLastUsedAt()
        );
    }
}