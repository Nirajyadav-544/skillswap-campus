package com.skillswap.learning.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "learning_paths",
    indexes = {
        @Index(name = "idx_learning_path_user", columnList = "user_id"),
        @Index(name = "idx_learning_path_status", columnList = "status"),
        @Index(name = "idx_learning_path_skill", columnList = "skill_name"),
        @Index(name = "idx_learning_path_created", columnList = "created_at")
    }
)
public class LearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", length = 3000)
    private String description;

    @Column(name = "skill_name", nullable = false, length = 150)
    private String skillName;

    @Column(name = "target_level", length = 50)
    private String targetLevel;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(name = "current_step")
    private Integer currentStep = 0;

    @Column(name = "total_steps")
    private Integer totalSteps = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private LearningPathStatus status = LearningPathStatus.DRAFT;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "target_completion_date")
    private LocalDateTime targetCompletionDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public LearningPath() {
    }

    public LearningPath(User user, String title, String skillName) {
        this.user = user;
        this.title = title;
        this.skillName = skillName;
        this.status = LearningPathStatus.DRAFT;
        this.currentStep = 0;
        this.totalSteps = 0;
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
        if (title != null) {
            title = title.trim();
        }

        if (description != null) {
            description = description.trim();
        }

        if (skillName != null) {
            skillName = skillName.trim();
        }

        if (targetLevel != null) {
            targetLevel = targetLevel.trim();
        }

        if (currentStep == null || currentStep < 0) {
            currentStep = 0;
        }

        if (totalSteps == null || totalSteps < 0) {
            totalSteps = 0;
        }

        if (estimatedHours != null && estimatedHours < 0) {
            estimatedHours = 0;
        }

        if (status == null) {
            status = LearningPathStatus.DRAFT;
        }
    }

    public void activate() {
        status = LearningPathStatus.ACTIVE;

        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
    }

    public void pause() {
        status = LearningPathStatus.PAUSED;
    }

    public void resume() {
        status = LearningPathStatus.ACTIVE;

        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
    }

    public void complete() {
        status = LearningPathStatus.COMPLETED;
        completedAt = LocalDateTime.now();

        if (totalSteps != null && totalSteps > 0) {
            currentStep = totalSteps;
        }
    }

    public void archive() {
        status = LearningPathStatus.ARCHIVED;
    }

    public void advanceStep() {
        if (currentStep == null) {
            currentStep = 0;
        }

        if (totalSteps != null && totalSteps > 0) {
            currentStep = Math.min(currentStep + 1, totalSteps);
        } else {
            currentStep++;
        }

        if (totalSteps != null
                && totalSteps > 0
                && currentStep >= totalSteps) {
            complete();
        }
    }

    public boolean isDraft() {
        return status == LearningPathStatus.DRAFT;
    }

    public boolean isActive() {
        return status == LearningPathStatus.ACTIVE;
    }

    public boolean isPaused() {
        return status == LearningPathStatus.PAUSED;
    }

    public boolean isCompleted() {
        return status == LearningPathStatus.COMPLETED;
    }

    public boolean isArchived() {
        return status == LearningPathStatus.ARCHIVED;
    }

    public boolean isClosed() {
        return isCompleted() || isArchived();
    }

    public int getCompletionPercentage() {
        if (totalSteps == null || totalSteps <= 0) {
            return 0;
        }

        int step = currentStep == null ? 0 : currentStep;

        return Math.min(
            100,
            Math.max(0, (int) Math.round((step * 100.0) / totalSteps))
        );
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
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

    public String getTargetLevel() {
        return targetLevel;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public Integer getCurrentStep() {
        return currentStep;
    }

    public Integer getTotalSteps() {
        return totalSteps;
    }

    public LearningPathStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
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

    public void setUser(User user) {
        this.user = user;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setTargetLevel(String targetLevel) {
        this.targetLevel = targetLevel;
    }

    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public void setCurrentStep(Integer currentStep) {
        this.currentStep = currentStep;
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
    }

    public void setStatus(LearningPathStatus status) {
        this.status = status;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setTargetCompletionDate(LocalDateTime targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}