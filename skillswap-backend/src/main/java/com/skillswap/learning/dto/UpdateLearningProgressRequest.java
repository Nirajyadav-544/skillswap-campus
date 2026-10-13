package com.skillswap.learning.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class UpdateLearningProgressRequest {

    @Min(0)
    @Max(100)
    private Integer progressPercentage;

    @Min(0)
    @Max(1000000)
    private Integer timeSpentMinutes;

    @Size(max = 3000)
    private String notes;

    public UpdateLearningProgressRequest() {
    }

    public void normalize() {

        if (progressPercentage != null) {
            progressPercentage = Math.max(
                    0,
                    Math.min(100, progressPercentage)
            );
        }

        if (timeSpentMinutes != null && timeSpentMinutes < 0) {
            timeSpentMinutes = 0;
        }

        if (notes != null) {
            notes = notes.trim();
        }
    }

    public boolean isEmpty() {
        return progressPercentage == null
                && timeSpentMinutes == null
                && notes == null;
    }

    public boolean hasProgressUpdate() {
        return progressPercentage != null;
    }

    public boolean hasTimeUpdate() {
        return timeSpentMinutes != null;
    }

    public boolean hasNotesUpdate() {
        return notes != null;
    }

    public boolean marksCompleted() {
        return progressPercentage != null
                && progressPercentage >= 100;
    }

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public Integer getTimeSpentMinutes() {
        return timeSpentMinutes;
    }

    public String getNotes() {
        return notes;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public void setTimeSpentMinutes(Integer timeSpentMinutes) {
        this.timeSpentMinutes = timeSpentMinutes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}