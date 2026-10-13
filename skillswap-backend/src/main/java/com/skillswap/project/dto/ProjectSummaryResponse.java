package com.skillswap.project.dto;

import com.skillswap.project.entity.Project;
import com.skillswap.project.entity.ProjectStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProjectSummaryResponse {

    private Long id;
    private Long ownerUserId;
    private String ownerName;

    private String title;
    private String description;
    private String skillName;
    private String category;
    private ProjectStatus status;

    private Integer maxMembers;
    private Integer currentMembers;

    private LocalDate startDate;
    private LocalDate targetEndDate;

    private LocalDateTime createdAt;

    public ProjectSummaryResponse() {
    }

    public static ProjectSummaryResponse from(Project project) {
        ProjectSummaryResponse response = new ProjectSummaryResponse();

        response.id = project.getId();

        if (project.getOwnerUser() != null) {
            response.ownerUserId = project.getOwnerUser().getId();

            String firstName = project.getOwnerUser().getFirstName();
            String lastName = project.getOwnerUser().getLastName();

            StringBuilder ownerName = new StringBuilder();

            if (firstName != null && !firstName.isBlank()) {
                ownerName.append(firstName.trim());
            }

            if (lastName != null && !lastName.isBlank()) {
                if (!ownerName.isEmpty()) {
                    ownerName.append(" ");
                }

                ownerName.append(lastName.trim());
            }

            response.ownerName = ownerName.toString();
        }

        response.title = project.getTitle();
        response.description = project.getDescription();
        response.skillName = project.getSkillName();
        response.category = project.getCategory();
        response.status = project.getStatus();

        response.maxMembers = project.getMaxMembers();
        response.currentMembers = project.getCurrentMembers();

        response.startDate = project.getStartDate();
        response.targetEndDate = project.getTargetEndDate();

        response.createdAt = project.getCreatedAt();

        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSkillName() {
        return skillName;
    }

    public String getCategory() {
        return category;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public Integer getCurrentMembers() {
        return currentMembers;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getTargetEndDate() {
        return targetEndDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}