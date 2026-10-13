package com.skillswap.project.dto;

import com.skillswap.project.entity.Project;
import com.skillswap.project.entity.ProjectStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProjectResponse {

    private Long id;
    private Long ownerUserId;
    private String ownerEmail;
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
    private LocalDateTime completedAt;

    private String requirements;
    private String goals;

    private String githubUrl;
    private String demoUrl;
    private String projectUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProjectResponse() {
    }

    public static ProjectResponse from(Project project) {
        ProjectResponse response = new ProjectResponse();

        response.id = project.getId();

        if (project.getOwnerUser() != null) {
            response.ownerUserId = project.getOwnerUser().getId();
            response.ownerEmail = project.getOwnerUser().getEmail();

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
        response.completedAt = project.getCompletedAt();

        response.requirements = project.getRequirements();
        response.goals = project.getGoals();

        response.githubUrl = project.getGithubUrl();
        response.demoUrl = project.getDemoUrl();
        response.projectUrl = project.getProjectUrl();

        response.createdAt = project.getCreatedAt();
        response.updatedAt = project.getUpdatedAt();

        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public String getOwnerEmail() {
        return ownerEmail;
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

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getRequirements() {
        return requirements;
    }

    public String getGoals() {
        return goals;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getDemoUrl() {
        return demoUrl;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}