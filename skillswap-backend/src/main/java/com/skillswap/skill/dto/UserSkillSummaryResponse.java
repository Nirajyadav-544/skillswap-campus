package com.skillswap.skill.dto;

import com.skillswap.skill.entity.ProficiencyLevel;
import com.skillswap.skill.entity.SkillCategory;
import com.skillswap.skill.entity.SkillType;

public record UserSkillSummaryResponse(
        Long id,

        Long userId,

        Long skillId,
        String skillName,
        SkillCategory skillCategory,

        SkillType skillType,
        ProficiencyLevel proficiencyLevel,

        double yearsOfExperience,

        boolean active,
        boolean verified,

        long exchangeCount,
        long successfulExchangeCount,

        double averageRating,
        long ratingCount,

        String firstName,
        String lastName,

        boolean canTeach,
        boolean wantsToLearn,
        boolean experienced
) {

    public UserSkillSummaryResponse {
        yearsOfExperience = Math.max(0.0, yearsOfExperience);
        exchangeCount = Math.max(0, exchangeCount);
        successfulExchangeCount = Math.max(0, successfulExchangeCount);
        averageRating = Math.max(0.0, Math.min(5.0, averageRating));
        ratingCount = Math.max(0, ratingCount);
    }

    public static UserSkillSummaryResponse from(
            com.skillswap.skill.entity.UserSkill userSkill
    ) {
        if (userSkill == null) {
            throw new IllegalArgumentException("User skill cannot be null");
        }

        var user = userSkill.getUser();
        var skill = userSkill.getSkill();

        return new UserSkillSummaryResponse(
                userSkill.getId(),

                user != null ? user.getId() : null,

                skill != null ? skill.getId() : null,
                skill != null ? skill.getName() : null,
                skill != null ? skill.getCategory() : null,

                userSkill.getSkillType(),
                userSkill.getProficiencyLevel(),

                userSkill.getYearsOfExperience(),

                userSkill.isActive(),
                userSkill.isVerified(),

                userSkill.getExchangeCount(),
                userSkill.getSuccessfulExchangeCount(),

                userSkill.getAverageRating(),
                userSkill.getRatingCount(),

                user != null ? user.getFirstName() : null,
                user != null ? user.getLastName() : null,

                userSkill.canTeach(),
                userSkill.wantsToLearn(),
                userSkill.isExperienced()
        );
    }
}