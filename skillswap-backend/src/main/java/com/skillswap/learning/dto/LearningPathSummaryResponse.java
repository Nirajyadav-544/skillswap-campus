package com.skillswap.learning.dto;

import com.skillswap.learning.entity.LearningPath;
import com.skillswap.learning.entity.LearningPathStatus;

import java.time.LocalDateTime;

public record LearningPathSummaryResponse(
        Long id,
        Long userId,
        String title,
        String skillName,
        String targetLevel,
        Integer currentStep,
        Integer totalSteps,
        Integer completionPercentage,
        LearningPathStatus status,
        LocalDateTime startDate,
        LocalDateTime targetCompletionDate,
        LocalDateTime createdAt
) {

    public static LearningPathSummaryResponse from(LearningPath path) {

        if (path == null) {
            return null;
        }

        Long userId = path.getUser() != null
                ? path.getUser().getId()
                : null;

        return new LearningPathSummaryResponse(
                path.getId(),
                userId,
                path.getTitle(),
                path.getSkillName(),
                path.getTargetLevel(),
                path.getCurrentStep(),
                path.getTotalSteps(),
                path.getCompletionPercentage(),
                path.getStatus(),
                path.getStartDate(),
                path.getTargetCompletionDate(),
                path.getCreatedAt()
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
}