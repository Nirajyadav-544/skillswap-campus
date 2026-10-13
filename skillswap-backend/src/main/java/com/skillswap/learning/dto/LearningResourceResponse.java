package com.skillswap.learning.dto;

import com.skillswap.learning.entity.LearningResource;
import com.skillswap.learning.entity.LearningResourceType;

import java.time.LocalDateTime;

public record LearningResourceResponse(
        Long id,
        Long learningPathId,
        Long userId,
        String title,
        String description,
        LearningResourceType resourceType,
        String url,
        String skillName,
        Integer stepNumber,
        Integer estimatedMinutes,
        boolean required,
        boolean completed,
        LocalDateTime completedAt,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static LearningResourceResponse from(LearningResource resource) {

        if (resource == null) {
            return null;
        }

        Long learningPathId = resource.getLearningPath() != null
                ? resource.getLearningPath().getId()
                : null;

        Long userId = resource.getUser() != null
                ? resource.getUser().getId()
                : null;

        return new LearningResourceResponse(
                resource.getId(),
                learningPathId,
                userId,
                resource.getTitle(),
                resource.getDescription(),
                resource.getResourceType(),
                resource.getUrl(),
                resource.getSkillName(),
                resource.getStepNumber(),
                resource.getEstimatedMinutes(),
                resource.isRequired(),
                resource.isCompleted(),
                resource.getCompletedAt(),
                resource.getNotes(),
                resource.getCreatedAt(),
                resource.getUpdatedAt()
        );
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

    public boolean isVideo() {
        return resourceType == LearningResourceType.VIDEO;
    }

    public boolean isCourse() {
        return resourceType == LearningResourceType.COURSE;
    }

    public boolean isBook() {
        return resourceType == LearningResourceType.BOOK;
    }

    public boolean isDocumentation() {
        return resourceType == LearningResourceType.DOCUMENTATION;
    }

    public boolean isProject() {
        return resourceType == LearningResourceType.PROJECT;
    }
}