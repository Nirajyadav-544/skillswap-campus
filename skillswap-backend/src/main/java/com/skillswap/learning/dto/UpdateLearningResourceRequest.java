package com.skillswap.learning.dto;

import com.skillswap.learning.entity.LearningResourceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class UpdateLearningResourceRequest {

    @Size(max = 250)
    private String title;

    @Size(max = 3000)
    private String description;

    private LearningResourceType resourceType;

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

    private Boolean required;

    private Boolean completed;

    @Size(max = 3000)
    private String notes;

    public UpdateLearningResourceRequest() {
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
    }

    public boolean isEmpty() {
        return title == null
                && description == null
                && resourceType == null
                && url == null
                && skillName == null
                && stepNumber == null
                && estimatedMinutes == null
                && required == null
                && completed == null
                && notes == null;
    }

    public boolean hasBasicInformationUpdate() {
        return title != null
                || description != null
                || resourceType != null
                || url != null
                || skillName != null;
    }

    public boolean hasScheduleUpdate() {
        return stepNumber != null
                || estimatedMinutes != null;
    }

    public boolean hasCompletionUpdate() {
        return completed != null;
    }

    public boolean hasNotesUpdate() {
        return notes != null;
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

    public Boolean getRequired() {
        return required;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public String getNotes() {
        return notes;
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

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}