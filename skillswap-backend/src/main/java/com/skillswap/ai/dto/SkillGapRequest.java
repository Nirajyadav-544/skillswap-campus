package com.skillswap.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SkillGapRequest {

    @NotBlank(message = "Target career is required")
    @Size(
            max = 200,
            message = "Target career must not exceed 200 characters"
    )
    private String targetCareer;

    @Size(
            max = 10000,
            message = "Current skills must not exceed 10000 characters"
    )
    private String currentSkills;

    @Size(
            max = 10000,
            message = "Experience information must not exceed 10000 characters"
    )
    private String experience;

    private Long userId;

    public SkillGapRequest() {
    }

    public SkillGapRequest(
            String targetCareer,
            String currentSkills
    ) {
        this.targetCareer = targetCareer;
        this.currentSkills = currentSkills;
    }

    public SkillGapRequest(
            String targetCareer,
            String currentSkills,
            String experience,
            Long userId
    ) {
        this.targetCareer = targetCareer;
        this.currentSkills = currentSkills;
        this.experience = experience;
        this.userId = userId;
    }

    public String getTargetCareer() {
        return targetCareer;
    }

    public void setTargetCareer(String targetCareer) {
        this.targetCareer = targetCareer;
    }

    public String getCurrentSkills() {
        return currentSkills;
    }

    public void setCurrentSkills(String currentSkills) {
        this.currentSkills = currentSkills;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void normalize() {
        if (targetCareer != null) {
            targetCareer = targetCareer.trim();
        }

        if (currentSkills != null) {
            currentSkills = currentSkills.trim();
        }

        if (experience != null) {
            experience = experience.trim();
        }
    }
}