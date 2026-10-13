package com.skillswap.learning.dto;

import com.skillswap.learning.entity.LearningPath;
import com.skillswap.learning.entity.LearningPathStatus;

import java.time.LocalDateTime;

public record LearningPathResponse(
        Long id,
        Long userId,
        String userName,
        String title,
        String description,
        String skillName,
        String targetLevel,
        Integer estimatedHours,
        Integer currentStep,
        Integer totalSteps,
        Integer completionPercentage,
        LearningPathStatus status,
        LocalDateTime startDate,
        LocalDateTime targetCompletionDate,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static LearningPathResponse from(LearningPath path) {

        if (path == null) {
            return null;
        }

        Long userId = null;
        String userName = null;

        if (path.getUser() != null) {
            userId = path.getUser().getId();

            String firstName = path.getUser().getFirstName();
            String lastName = path.getUser().getLastName();

            if (firstName != null && lastName != null) {
                userName = firstName + " " + lastName;
            } else if (firstName != null) {
                userName = firstName;
            } else if (lastName != null) {
                userName = lastName;
            } else {
                userName = path.getUser().getEmail();
            }
        }

        return new LearningPathResponse(
                path.getId(),
                userId,
                userName,
                path.getTitle(),
                path.getDescription(),
                path.getSkillName(),
                path.getTargetLevel(),
                path.getEstimatedHours(),
                path.getCurrentStep(),
                path.getTotalSteps(),
                path.getCompletionPercentage(),
                path.getStatus(),
                path.getStartDate(),
                path.getTargetCompletionDate(),
                path.getCompletedAt(),
                path.getCreatedAt(),
                path.getUpdatedAt()
        );
    }

    public boolean isActive() {
        return status == LearningPathStatus.ACTIVE;
    }

    public boolean isCompleted() {
        return status == LearningPathStatus.COMPLETED;
    }

    public boolean isPaused() {
        return status == LearningPathStatus.PAUSED;
    }

    public boolean isArchived() {
        return status == LearningPathStatus.ARCHIVED;
    }

    public boolean isDraft() {
        return status == LearningPathStatus.DRAFT;
    }

    public boolean hasStarted() {
        return startDate != null;
    }

    public boolean hasTargetCompletionDate() {
        return targetCompletionDate != null;
    }
}