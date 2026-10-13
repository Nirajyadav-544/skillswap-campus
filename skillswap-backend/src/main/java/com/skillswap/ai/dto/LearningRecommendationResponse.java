package com.skillswap.ai.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LearningRecommendationResponse {

    private boolean success;
    private String message;
    private String learningGoal;
    private List<String> recommendedSkills;
    private List<String> recommendedTopics;
    private List<String> recommendedResources;
    private List<String> recommendedSteps;
    private LocalDateTime createdAt;

    public LearningRecommendationResponse() {
        this.recommendedSkills = new ArrayList<>();
        this.recommendedTopics = new ArrayList<>();
        this.recommendedResources = new ArrayList<>();
        this.recommendedSteps = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public static LearningRecommendationResponse success(
            String learningGoal,
            List<String> recommendedSkills,
            List<String> recommendedTopics,
            List<String> recommendedResources,
            List<String> recommendedSteps
    ) {
        LearningRecommendationResponse response =
                new LearningRecommendationResponse();

        response.success = true;
        response.message =
                "Learning recommendations generated successfully";

        response.learningGoal = learningGoal;

        response.recommendedSkills =
                recommendedSkills == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedSkills);

        response.recommendedTopics =
                recommendedTopics == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedTopics);

        response.recommendedResources =
                recommendedResources == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedResources);

        response.recommendedSteps =
                recommendedSteps == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedSteps);

        return response;
    }

    public static LearningRecommendationResponse failure(
            String message
    ) {
        LearningRecommendationResponse response =
                new LearningRecommendationResponse();

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

    public String getLearningGoal() {
        return learningGoal;
    }

    public void setLearningGoal(String learningGoal) {
        this.learningGoal = learningGoal;
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

    public List<String> getRecommendedTopics() {
        return recommendedTopics;
    }

    public void setRecommendedTopics(
            List<String> recommendedTopics
    ) {
        this.recommendedTopics =
                recommendedTopics == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedTopics);
    }

    public List<String> getRecommendedResources() {
        return recommendedResources;
    }

    public void setRecommendedResources(
            List<String> recommendedResources
    ) {
        this.recommendedResources =
                recommendedResources == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedResources);
    }

    public List<String> getRecommendedSteps() {
        return recommendedSteps;
    }

    public void setRecommendedSteps(
            List<String> recommendedSteps
    ) {
        this.recommendedSteps =
                recommendedSteps == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedSteps);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}