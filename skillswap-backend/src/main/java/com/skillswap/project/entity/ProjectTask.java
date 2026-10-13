package com.skillswap.project.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "project_tasks",
        indexes = {
                @Index(name = "idx_project_task_project", columnList = "project_id"),
                @Index(name = "idx_project_task_assignee", columnList = "assignee_user_id"),
                @Index(name = "idx_project_task_status", columnList = "status"),
                @Index(name = "idx_project_task_priority", columnList = "priority"),
                @Index(name = "idx_project_task_due_date", columnList = "due_date")
        }
)
public class ProjectTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_user_id")
    private User assigneeUser;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 3000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectTaskStatus status = ProjectTaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectTaskPriority priority = ProjectTaskPriority.MEDIUM;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "estimated_hours")
    private Double estimatedHours;

    @Column(name = "actual_hours")
    private Double actualHours;

    @Column(length = 2000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ProjectTask() {
    }

    public ProjectTask(Project project, String title) {
        this.project = project;
        this.title = title;
        this.status = ProjectTaskStatus.TODO;
        this.priority = ProjectTaskPriority.MEDIUM;
    }

    @PrePersist
    protected void onCreate() {
        normalize();

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (status == null) {
            status = ProjectTaskStatus.TODO;
        }

        if (priority == null) {
            priority = ProjectTaskPriority.MEDIUM;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        normalize();
        updatedAt = LocalDateTime.now();
    }

    private void normalize() {
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

    public void start() {
        this.status = ProjectTaskStatus.IN_PROGRESS;
    }

    public void block() {
        this.status = ProjectTaskStatus.BLOCKED;
    }

    public void sendForReview() {
        this.status = ProjectTaskStatus.REVIEW;
    }

    public void complete() {
        this.status = ProjectTaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = ProjectTaskStatus.CANCELLED;
    }

    public void reopen() {
        this.status = ProjectTaskStatus.TODO;
        this.completedAt = null;
    }

    public boolean isTodo() {
        return status == ProjectTaskStatus.TODO;
    }

    public boolean isInProgress() {
        return status == ProjectTaskStatus.IN_PROGRESS;
    }

    public boolean isBlocked() {
        return status == ProjectTaskStatus.BLOCKED;
    }

    public boolean isInReview() {
        return status == ProjectTaskStatus.REVIEW;
    }

    public boolean isCompleted() {
        return status == ProjectTaskStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == ProjectTaskStatus.CANCELLED;
    }

    public boolean isClosed() {
        return status == ProjectTaskStatus.COMPLETED
                || status == ProjectTaskStatus.CANCELLED;
    }

    public boolean isAssigned() {
        return assigneeUser != null;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getAssigneeUser() {
        return assigneeUser;
    }

    public void setAssigneeUser(User assigneeUser) {
        this.assigneeUser = assigneeUser;
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

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
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