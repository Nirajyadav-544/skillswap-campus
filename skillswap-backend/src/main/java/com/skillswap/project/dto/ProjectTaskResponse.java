package com.skillswap.project.dto;

import com.skillswap.project.entity.ProjectTask;
import com.skillswap.project.entity.ProjectTaskPriority;
import com.skillswap.project.entity.ProjectTaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProjectTaskResponse {

    private Long id;
    private Long projectId;

    private Long assigneeUserId;
    private String assigneeEmail;
    private String assigneeName;

    private String title;
    private String description;

    private ProjectTaskStatus status;
    private ProjectTaskPriority priority;

    private LocalDate dueDate;
    private LocalDateTime completedAt;

    private Double estimatedHours;
    private Double actualHours;

    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProjectTaskResponse() {
    }

    public static ProjectTaskResponse from(ProjectTask task) {
        ProjectTaskResponse response = new ProjectTaskResponse();

        response.id = task.getId();

        if (task.getProject() != null) {
            response.projectId = task.getProject().getId();
        }

        if (task.getAssigneeUser() != null) {
            response.assigneeUserId = task.getAssigneeUser().getId();
            response.assigneeEmail = task.getAssigneeUser().getEmail();

            String firstName = task.getAssigneeUser().getFirstName();
            String lastName = task.getAssigneeUser().getLastName();

            StringBuilder assigneeName = new StringBuilder();

            if (firstName != null && !firstName.isBlank()) {
                assigneeName.append(firstName.trim());
            }

            if (lastName != null && !lastName.isBlank()) {
                if (!assigneeName.isEmpty()) {
                    assigneeName.append(" ");
                }

                assigneeName.append(lastName.trim());
            }

            response.assigneeName = assigneeName.toString();
        }

        response.title = task.getTitle();
        response.description = task.getDescription();

        response.status = task.getStatus();
        response.priority = task.getPriority();

        response.dueDate = task.getDueDate();
        response.completedAt = task.getCompletedAt();

        response.estimatedHours = task.getEstimatedHours();
        response.actualHours = task.getActualHours();

        response.notes = task.getNotes();

        response.createdAt = task.getCreatedAt();
        response.updatedAt = task.getUpdatedAt();

        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getAssigneeUserId() {
        return assigneeUserId;
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ProjectTaskStatus getStatus() {
        return status;
    }

    public ProjectTaskPriority getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public Double getActualHours() {
        return actualHours;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}