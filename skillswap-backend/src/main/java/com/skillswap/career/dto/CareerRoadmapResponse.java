package com.skillswap.career.dto;

import com.skillswap.career.entity.CareerRoadmap;

import java.time.LocalDateTime;

public class CareerRoadmapResponse {

    private Long id;
    private Long userId;
    private Long careerGoalId;
    private String title;
    private String description;
    private String targetRole;
    private Integer totalItems;
    private Integer completedItems;
    private Double progressPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CareerRoadmapResponse() {
    }

    public CareerRoadmapResponse(
            Long id,
            Long userId,
            Long careerGoalId,
            String title,
            String description,
            String targetRole,
            Integer totalItems,
            Integer completedItems,
            Double progressPercentage,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.careerGoalId = careerGoalId;
        this.title = title;
        this.description = description;
        this.targetRole = targetRole;
        this.totalItems = totalItems;
        this.completedItems = completedItems;
        this.progressPercentage = progressPercentage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CareerRoadmapResponse from(CareerRoadmap roadmap) {
        int total = roadmap.getTotalItems() == null
                ? 0
                : roadmap.getTotalItems();

        int completed = roadmap.getCompletedItems() == null
                ? 0
                : roadmap.getCompletedItems();

        double progress = total == 0
                ? 0.0
                : (completed * 100.0) / total;

        return new CareerRoadmapResponse(
                roadmap.getId(),
                roadmap.getUser() != null
                        ? roadmap.getUser().getId()
                        : null,
                roadmap.getCareerGoal() != null
                        ? roadmap.getCareerGoal().getId()
                        : null,
                roadmap.getTitle(),
                roadmap.getDescription(),
                roadmap.getTargetRole(),
                total,
                completed,
                progress,
                roadmap.getCreatedAt(),
                roadmap.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCareerGoalId() {
        return careerGoalId;
    }

    public void setCareerGoalId(Long careerGoalId) {
        this.careerGoalId = careerGoalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getCompletedItems() {
        return completedItems;
    }

    public void setCompletedItems(Integer completedItems) {
        this.completedItems = completedItems;
    }

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}