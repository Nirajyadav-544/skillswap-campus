package com.skillswap.project.dto;

import com.skillswap.project.entity.ProjectTaskPriority;
import com.skillswap.project.entity.ProjectTaskStatus;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UpdateProjectTaskRequest {

    @Size(max = 200, message = "Task title must not exceed 200 characters")
    private String title;

    @Size(max = 3000, message = "Task description must not exceed 3000 characters")
    private String description;

    private Long assigneeUserId;

    private ProjectTaskStatus status;

    private ProjectTaskPriority priority;

    private LocalDate dueDate;

    private Double estimatedHours;

    private Double actualHours;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;

    public UpdateProjectTaskRequest() {
    }

    public boolean isEmpty() {
        return title == null
                && description == null
                && assigneeUserId == null
                && status == null
                && priority == null
                && dueDate == null
                && estimatedHours == null
                && actualHours == null
                && notes == null;
    }

    public void normalize() {
        if (title != null) {
            title = title.trim();
        }

        if (description != null) {
            description = description.trim();
        }

        if (notes != null) {
            notes = notes.trim();
        }
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

    public Long getAssigneeUserId() {
        return assigneeUserId;
    }

    public void setAssigneeUserId(Long assigneeUserId) {
        this.assigneeUserId = assigneeUserId;
    }

    public ProjectTaskStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectTaskStatus status) {
        this.status = status;
    }

    public ProjectTaskPriority getPriority() {
        return priority;
    }

    public void setPriority(ProjectTaskPriority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Double getActualHours() {
        return actualHours;
    }

    public void setActualHours(Double actualHours) {
        this.actualHours = actualHours;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}