package com.skillswap.career.dto;

import com.skillswap.career.entity.CareerGoal;
import com.skillswap.career.entity.CareerGoalStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CareerGoalResponse {

    private Long id;
    private Long userId;
    private String goalTitle;
    private String description;
    private String targetRole;
    private String targetCompany;
    private LocalDate targetDate;
    private CareerGoalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CareerGoalResponse() {
    }

    public CareerGoalResponse(
            Long id,
            Long userId,
            String goalTitle,
            String description,
            String targetRole,
            String targetCompany,
            LocalDate targetDate,
            CareerGoalStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.goalTitle = goalTitle;
        this.description = description;
        this.targetRole = targetRole;
        this.targetCompany = targetCompany;
        this.targetDate = targetDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CareerGoalResponse from(CareerGoal goal) {
        return new CareerGoalResponse(
                goal.getId(),
                goal.getUser() != null ? goal.getUser().getId() : null,
                goal.getGoalTitle(),
                goal.getDescription(),
                goal.getTargetRole(),
                goal.getTargetCompany(),
                goal.getTargetDate(),
                goal.getStatus(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
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

    public String getGoalTitle() {
        return goalTitle;
    }

    public void setGoalTitle(String goalTitle) {
        this.goalTitle = goalTitle;
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

    public String getTargetCompany() {
        return targetCompany;
    }

    public void setTargetCompany(String targetCompany) {
        this.targetCompany = targetCompany;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public CareerGoalStatus getStatus() {
        return status;
    }

    public void setStatus(CareerGoalStatus status) {
        this.status = status;
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