package com.skillswap.ai.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CareerAnalysisResponse {

    private boolean success;
    private String message;
    private String careerGoal;
    private Integer careerMatchPercentage;

    private List<String> strengths;
    private List<String> skillGaps;
    private List<String> recommendedSkills;
    private List<String> recommendedProjects;
    private List<String> recommendedCertifications;
    private List<String> recommendedActions;

    private LocalDateTime createdAt;

    public CareerAnalysisResponse() {
        this.strengths = new ArrayList<>();
        this.skillGaps = new ArrayList<>();
        this.recommendedSkills = new ArrayList<>();
        this.recommendedProjects = new ArrayList<>();
        this.recommendedCertifications = new ArrayList<>();
        this.recommendedActions = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public static CareerAnalysisResponse success(
            String careerGoal,
            Integer careerMatchPercentage,
            List<String> strengths,
            List<String> skillGaps,
            List<String> recommendedSkills,
            List<String> recommendedProjects,
            List<String> recommendedCertifications,
            List<String> recommendedActions
    ) {
        CareerAnalysisResponse response =
                new CareerAnalysisResponse();

        response.success = true;
        response.message =
                "Career analysis completed successfully";

        response.careerGoal = careerGoal;
        response.careerMatchPercentage =
                careerMatchPercentage;

        response.strengths =
                strengths == null
                        ? new ArrayList<>()
                        : new ArrayList<>(strengths);

        response.skillGaps =
                skillGaps == null
                        ? new ArrayList<>()
                        : new ArrayList<>(skillGaps);

        response.recommendedSkills =
                recommendedSkills == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedSkills);

        response.recommendedProjects =
                recommendedProjects == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedProjects);

        response.recommendedCertifications =
                recommendedCertifications == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedCertifications);

        response.recommendedActions =
                recommendedActions == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedActions);

        return response;
    }

    public static CareerAnalysisResponse failure(
            String message
    ) {
        CareerAnalysisResponse response =
                new CareerAnalysisResponse();

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

    public String getCareerGoal() {
        return careerGoal;
    }

    public void setCareerGoal(String careerGoal) {
        this.careerGoal = careerGoal;
    }

    public Integer getCareerMatchPercentage() {
        return careerMatchPercentage;
    }

    public void setCareerMatchPercentage(
            Integer careerMatchPercentage
    ) {
        this.careerMatchPercentage =
                careerMatchPercentage;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths =
                strengths == null
                        ? new ArrayList<>()
                        : new ArrayList<>(strengths);
    }

    public List<String> getSkillGaps() {
        return skillGaps;
    }

    public void setSkillGaps(List<String> skillGaps) {
        this.skillGaps =
                skillGaps == null
                        ? new ArrayList<>()
                        : new ArrayList<>(skillGaps);
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

    public List<String> getRecommendedProjects() {
        return recommendedProjects;
    }

    public void setRecommendedProjects(
            List<String> recommendedProjects
    ) {
        this.recommendedProjects =
                recommendedProjects == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedProjects);
    }

    public List<String> getRecommendedCertifications() {
        return recommendedCertifications;
    }

    public void setRecommendedCertifications(
            List<String> recommendedCertifications
    ) {
        this.recommendedCertifications =
                recommendedCertifications == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedCertifications);
    }

    public List<String> getRecommendedActions() {
        return recommendedActions;
    }

    public void setRecommendedActions(
            List<String> recommendedActions
    ) {
        this.recommendedActions =
                recommendedActions == null
                        ? new ArrayList<>()
                        : new ArrayList<>(recommendedActions);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}