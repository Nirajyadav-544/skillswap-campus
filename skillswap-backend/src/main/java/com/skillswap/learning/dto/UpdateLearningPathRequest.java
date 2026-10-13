package com.skillswap.learning.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class UpdateLearningPathRequest {

    @Size(max = 200)
    private String title;

    @Size(max = 3000)
    private String description;

    @Size(max = 150)
    private String skillName;

    @Size(max = 50)
    private String targetLevel;

    @Min(0)
    @Max(100000)
    private Integer estimatedHours;

    @Min(0)
    @Max(1000)
    private Integer totalSteps;

    @Min(0)
    @Max(1000)
    private Integer currentStep;

    private LocalDateTime startDate;

    private LocalDateTime targetCompletionDate;

    public UpdateLearningPathRequest() {
    }

    public void normalize() {
        if (title != null) {
            title = title.trim();
        }

        if (description != null) {
            description = description.trim();
        }

        if (skillName != null) {
            skillName = skillName.trim();
        }

        if (targetLevel != null) {
            targetLevel = targetLevel.trim();
        }

        if (estimatedHours != null && estimatedHours < 0) {
            estimatedHours = 0;
        }

        if (totalSteps != null && totalSteps < 0) {
            totalSteps = 0;
        }

        if (currentStep != null && currentStep < 0) {
            currentStep = 0;
        }
    }

    public boolean isEmpty() {
        return title == null
                && description == null
                && skillName == null
                && targetLevel == null
                && estimatedHours == null
                && totalSteps == null
                && currentStep == null
                && startDate == null
                && targetCompletionDate == null;
    }

    public boolean hasBasicInformationUpdate() {
        return title != null
                || description != null
                || skillName != null
                || targetLevel != null;
    }

    public boolean hasStepUpdate() {
        return totalSteps != null || currentStep != null;
    }

    public boolean hasScheduleUpdate() {
        return startDate != null || targetCompletionDate != null;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSkillName() {
        return skillName;
    }

    public String getTargetLevel() {
        return targetLevel;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public Integer getTotalSteps() {
        return totalSteps;
    }

    public Integer getCurrentStep() {
        return currentStep;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setTargetLevel(String targetLevel) {
        this.targetLevel = targetLevel;
    }

    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
    }

    public void setCurrentStep(Integer currentStep) {
        this.currentStep = currentStep;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setTargetCompletionDate(LocalDateTime targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }
}