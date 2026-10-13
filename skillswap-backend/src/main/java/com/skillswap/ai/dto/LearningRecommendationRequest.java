package com.skillswap.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LearningRecommendationRequest {

    @NotBlank(message = "Learning goal is required")
    @Size(
            max = 500,
            message = "Learning goal must not exceed 500 characters"
    )
    private String learningGoal;

    @Size(
            max = 10000,
            message = "Current skills must not exceed 10000 characters"
    )
    private String currentSkills;

    @Size(
            max = 10000,
            message = "Current knowledge must not exceed 10000 characters"
    )
    private String currentKnowledge;

    private Long userId;

    public LearningRecommendationRequest() {
    }

    public LearningRecommendationRequest(
            String learningGoal,
            String currentSkills
    ) {
        this.learningGoal = learningGoal;
        this.currentSkills = currentSkills;
    }

    public LearningRecommendationRequest(
            String learningGoal,
            String currentSkills,
            String currentKnowledge,
            Long userId
    ) {
        this.learningGoal = learningGoal;
        this.currentSkills = currentSkills;
        this.currentKnowledge = currentKnowledge;
        this.userId = userId;
    }

    public String getLearningGoal() {
        return learningGoal;
    }

    public void setLearningGoal(String learningGoal) {
        this.learningGoal = learningGoal;
    }

    public String getCurrentSkills() {
        return currentSkills;
    }

    public void setCurrentSkills(String currentSkills) {
        this.currentSkills = currentSkills;
    }

    public String getCurrentKnowledge() {
        return currentKnowledge;
    }

    public void setCurrentKnowledge(String currentKnowledge) {
        this.currentKnowledge = currentKnowledge;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void normalize() {
        if (learningGoal != null) {
            learningGoal = learningGoal.trim();
        }

        if (currentSkills != null) {
            currentSkills = currentSkills.trim();
        }

        if (currentKnowledge != null) {
            currentKnowledge = currentKnowledge.trim();
        }
    }
}