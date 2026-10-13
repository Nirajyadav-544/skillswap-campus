package com.skillswap.learning.dto;

import com.skillswap.learning.entity.LearningProgress;
import com.skillswap.learning.entity.LearningProgressStatus;

import java.time.LocalDateTime;

public record LearningProgressResponse(
        Long id,
        Long learningPathId,
        Long learningResourceId,
        Long userId,
        String resourceTitle,
        String pathTitle,
        LearningProgressStatus status,
        Integer progressPercentage,
        Integer timeSpentMinutes,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        LocalDateTime lastAccessedAt,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static LearningProgressResponse from(
            LearningProgress progress) {

        if (progress == null) {
            return null;
        }

        Long learningPathId = progress.getLearningPath() != null
                ? progress.getLearningPath().getId()
                : null;

        Long learningResourceId =
                progress.getLearningResource() != null
                        ? progress.getLearningResource().getId()
                        : null;

        Long userId = progress.getUser() != null
                ? progress.getUser().getId()
                : null;

        String resourceTitle =
                progress.getLearningResource() != null
                        ? progress.getLearningResource().getTitle()
                        : null;

        String pathTitle =
                progress.getLearningPath() != null
                        ? progress.getLearningPath().getTitle()
                        : null;

        return new LearningProgressResponse(
                progress.getId(),
                learningPathId,
                learningResourceId,
                userId,
                resourceTitle,
                pathTitle,
                progress.getStatus(),
                progress.getProgressPercentage(),
                progress.getTimeSpentMinutes(),
                progress.getStartedAt(),
                progress.getCompletedAt(),
                progress.getLastAccessedAt(),
                progress.getNotes(),
                progress.getCreatedAt(),
                progress.getUpdatedAt()
        );
    }

    public boolean isNotStarted() {
        return status == LearningProgressStatus.NOT_STARTED;
    }

    public boolean isInProgress() {
        return status == LearningProgressStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return status == LearningProgressStatus.COMPLETED;
    }

    public boolean isSkipped() {
        return status == LearningProgressStatus.SKIPPED;
    }

    public boolean hasStarted() {
        return startedAt != null;
    }

    public boolean hasCompleted() {
        return completedAt != null;
    }

    public boolean hasNotes() {
        return notes != null && !notes.isBlank();
    }
}