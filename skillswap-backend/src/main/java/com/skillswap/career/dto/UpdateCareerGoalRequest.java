package com.skillswap.career.dto;

import com.skillswap.career.entity.CareerGoalStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UpdateCareerGoalRequest {

    @Size(
            max = 200,
            message = "Goal title cannot exceed 200 characters"
    )
    private String goalTitle;

    @Size(
            max = 2000,
            message = "Description cannot exceed 2000 characters"
    )
    private String description;

    @Size(
            max = 200,
            message = "Target role cannot exceed 200 characters"
    )
    private String targetRole;

    @Size(
            max = 200,
            message = "Target company cannot exceed 200 characters"
    )
    private String targetCompany;

    @FutureOrPresent(
            message = "Target date cannot be in the past"
    )
    private LocalDate targetDate;

    private CareerGoalStatus status;

    public UpdateCareerGoalRequest() {
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
}