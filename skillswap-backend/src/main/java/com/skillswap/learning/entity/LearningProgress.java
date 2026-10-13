package com.skillswap.learning.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "learning_progress",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_learning_progress_user_resource",
            columnNames = {"user_id", "learning_resource_id"}
        )
    },
    indexes = {
        @Index(name = "idx_learning_progress_user", columnList = "user_id"),
        @Index(name = "idx_learning_progress_path", columnList = "learning_path_id"),
        @Index(name = "idx_learning_progress_resource", columnList = "learning_resource_id"),
        @Index(name = "idx_learning_progress_status", columnList = "status")
    }
)
public class LearningProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "learning_resource_id", nullable = false)
    private LearningResource learningResource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private LearningProgressStatus status = LearningProgressStatus.NOT_STARTED;

    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage = 0;

    @Column(name = "time_spent_minutes", nullable = false)
    private Integer timeSpentMinutes = 0;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "notes", length = 3000)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public LearningProgress() {
    }

    public LearningProgress(
            LearningPath learningPath,
            LearningResource learningResource,
            User user) {

        this.learningPath = learningPath;
        this.learningResource = learningResource;
        this.user = user;
        this.status = LearningProgressStatus.NOT_STARTED;
        this.progressPercentage = 0;
        this.timeSpentMinutes = 0;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        normalize();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        normalize();
    }

    public void normalize() {
        if (status == null) {
            status = LearningProgressStatus.NOT_STARTED;
        }

        if (progressPercentage == null) {
            progressPercentage = 0;
        }

        progressPercentage = Math.max(0, Math.min(100, progressPercentage));

        if (timeSpentMinutes == null || timeSpentMinutes < 0) {
            timeSpentMinutes = 0;
        }

        if (notes != null) {
            notes = notes.trim();
        }

        if (status == LearningProgressStatus.COMPLETED) {
            progressPercentage = 100;

            if (completedAt == null) {
                completedAt = LocalDateTime.now();
            }
        }
    }

    public void start() {
        status = LearningProgressStatus.IN_PROGRESS;

        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }

        lastAccessedAt = LocalDateTime.now();
    }

    public void updateProgress(Integer percentage) {
        if (percentage == null) {
            return;
        }

        progressPercentage = Math.max(0, Math.min(100, percentage));

        lastAccessedAt = LocalDateTime.now();

        if (progressPercentage >= 100) {
            complete();
        } else if (progressPercentage > 0) {
            start();
        }
    }

    public void addTimeSpent(Integer minutes) {
        if (minutes == null || minutes <= 0) {
            return;
        }

        if (timeSpentMinutes == null) {
            timeSpentMinutes = 0;
        }

        timeSpentMinutes += minutes;
        lastAccessedAt = LocalDateTime.now();
    }

    public void complete() {
        status = LearningProgressStatus.COMPLETED;
        progressPercentage = 100;

        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }

        completedAt = LocalDateTime.now();
        lastAccessedAt = LocalDateTime.now();
    }

    public void reset() {
        status = LearningProgressStatus.NOT_STARTED;
        progressPercentage = 0;
        timeSpentMinutes = 0;
        startedAt = null;
        completedAt = null;
        lastAccessedAt = null;
    }

    public void skip() {
        status = LearningProgressStatus.SKIPPED;
        lastAccessedAt = LocalDateTime.now();
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

    public Long getId() {
        return id;
    }

    public LearningPath getLearningPath() {
        return learningPath;
    }

    public LearningResource getLearningResource() {
        return learningResource;
    }

    public User getUser() {
        return user;
    }

    public LearningProgressStatus getStatus() {
        return status;
    }

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public Integer getTimeSpentMinutes() {
        return timeSpentMinutes;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setLearningPath(LearningPath learningPath) {
        this.learningPath = learningPath;
    }

    public void setLearningResource(LearningResource learningResource) {
        this.learningResource = learningResource;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStatus(LearningProgressStatus status) {
        this.status = status;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public void setTimeSpentMinutes(Integer timeSpentMinutes) {
        this.timeSpentMinutes = timeSpentMinutes;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}