package com.skillswap.ai.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SkillGapResponse {

    private boolean success;
    private String message;
    private String targetCareer;
    private List<String> currentSkills;
    private List<String> missingSkills;
    private List<String> recommendedSkills;
    private Integer matchPercentage;
    private LocalDateTime createdAt;

    public SkillGapResponse() {
        this.currentSkills = new ArrayList<>();
        this.missingSkills = new ArrayList<>();
        this.recommendedSkills = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public static SkillGapResponse success(
            String targetCareer,
            List<String> currentSkills,
            List<String> missingSkills,
            List<String> recommendedSkills,
            Integer matchPercentage
    ) {
        SkillGapResponse response = new SkillGapResponse();

        response.success = true;
        response.message = "Skill gap analysis completed successfully";
        response.targetCareer = targetCareer;

        response.currentSkills =
                currentSkills == null
                        ? new ArrayList<>()
                        : new ArrayList<>(currentSkills);

        response.missingSkills =
                missingSkills == null
                        ? new ArrayList<>()
                        : new ArrayList<>(missingSkills);

        response.recommendedSkills =
                recommendedSkills == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedSkills);

        response.matchPercentage = matchPercentage;

        return response;
    }

    public static SkillGapResponse failure(
            String message
    ) {
        SkillGapResponse response = new SkillGapResponse();
        response.success = false;
        response.message = message;
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTargetCareer() {
        return targetCareer;
    }

    public void setTargetCareer(String targetCareer) {
        this.targetCareer = targetCareer;
    }

    public List<String> getCurrentSkills() {
        return currentSkills;
    }

    public void setCurrentSkills(List<String> currentSkills) {
        this.currentSkills =
                currentSkills == null
                        ? new ArrayList<>()
                        : new ArrayList<>(currentSkills);
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills =
                missingSkills == null
                        ? new ArrayList<>()
                        : new ArrayList<>(missingSkills);
    }

    public List<String> getRecommendedSkills() {
        return recommendedSkills;
    }

    public void setRecommendedSkills(
            List<String> recommendedSkills
    ) {
        this.recommendedSkills =
                recommendedSkills == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedSkills);
    }

    public Integer getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(Integer matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}