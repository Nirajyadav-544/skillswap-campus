package com.skillswap.learning.dto;

import com.skillswap.learning.entity.LearningResourceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateLearningResourceRequest {

    @NotNull
    private Long learningPathId;

    @NotBlank
    @Size(max = 250)
    private String title;

    @Size(max = 3000)
    private String description;

    @NotNull
    private LearningResourceType resourceType;

    @NotBlank
    @Size(max = 1000)
    private String url;

    @Size(max = 150)
    private String skillName;

    @Min(0)
    @Max(1000)
    private Integer stepNumber;

    @Min(0)
    @Max(100000)
    private Integer estimatedMinutes;

    private boolean required = true;

    @Size(max = 3000)
    private String notes;

    public CreateLearningResourceRequest() {
    }

    public void normalize() {

        if (title != null) {
            title = title.trim();
        }

        if (description != null) {
            description = description.trim();
        }

        if (url != null) {
            url = url.trim();
        }

        if (skillName != null) {
            skillName = skillName.trim();
        }

        if (notes != null) {
            notes = notes.trim();
        }

        if (stepNumber != null && stepNumber < 0) {
            stepNumber = 0;
        }

        if (estimatedMinutes != null && estimatedMinutes < 0) {
            estimatedMinutes = 0;
        }

        if (resourceType == null) {
            resourceType = LearningResourceType.OTHER;
        }
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasSkillName() {
        return skillName != null && !skillName.isBlank();
    }

    public boolean hasNotes() {
        return notes != null && !notes.isBlank();
    }

    public Long getLearningPathId() {
        return learningPathId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LearningResourceType getResourceType() {
        return resourceType;
    }

    public String getUrl() {
        return url;
    }

    public String getSkillName() {
        return skillName;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public boolean isRequired() {
        return required;
    }

    public String getNotes() {
        return notes;
    }

    public void setLearningPathId(Long learningPathId) {
        this.learningPathId = learningPathId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setResourceType(LearningResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}