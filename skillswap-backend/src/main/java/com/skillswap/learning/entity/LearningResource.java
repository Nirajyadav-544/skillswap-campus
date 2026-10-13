package com.skillswap.learning.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "learning_resources",
    indexes = {
        @Index(name = "idx_learning_resource_path", columnList = "learning_path_id"),
        @Index(name = "idx_learning_resource_user", columnList = "user_id"),
        @Index(name = "idx_learning_resource_type", columnList = "resource_type"),
        @Index(name = "idx_learning_resource_skill", columnList = "skill_name"),
        @Index(name = "idx_learning_resource_created", columnList = "created_at")
    }
)
public class LearningResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 250)
    private String title;

    @Column(name = "description", length = 3000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 40)
    private LearningResourceType resourceType;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    @Column(name = "skill_name", length = 150)
    private String skillName;

    @Column(name = "step_number")
    private Integer stepNumber;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(name = "is_required", nullable = false)
    private boolean required = true;

    @Column(name = "is_completed", nullable = false)
    private boolean completed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "notes", length = 3000)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public LearningResource() {
    }

    public LearningResource(
            LearningPath learningPath,
            User user,
            String title,
            LearningResourceType resourceType,
            String url) {

        this.learningPath = learningPath;
        this.user = user;
        this.title = title;
        this.resourceType = resourceType;
        this.url = url;
        this.required = true;
        this.completed = false;
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

        if (url != null) {
            url = url.trim();
        }

        if (skillName != null) {
            skillName = skillName.trim();
        }

        if (notes != null) {
            notes = notes.trim();
        }

        if (resourceType == null) {
            resourceType = LearningResourceType.OTHER;
        }

        if (stepNumber != null && stepNumber < 0) {
            stepNumber = 0;
        }

        if (estimatedMinutes != null && estimatedMinutes < 0) {
            estimatedMinutes = 0;
        }
    }

    public void markCompleted() {
        completed = true;
        completedAt = LocalDateTime.now();
    }

    public void markIncomplete() {
        completed = false;
        completedAt = null;
    }

    public void toggleCompleted() {
        if (completed) {
            markIncomplete();
        } else {
            markCompleted();
        }
    }

    public boolean isVideo() {
        return resourceType == LearningResourceType.VIDEO;
    }

    public boolean isCourse() {
        return resourceType == LearningResourceType.COURSE;
    }

    public boolean isBook() {
        return resourceType == LearningResourceType.BOOK;
    }

    public boolean isDocumentation() {
        return resourceType == LearningResourceType.DOCUMENTATION;
    }

    public boolean isProject() {
        return resourceType == LearningResourceType.PROJECT;
    }

    public boolean hasNotes() {
        return notes != null && !notes.isBlank();
    }

    public Long getId() {
        return id;
    }

    public LearningPath getLearningPath() {
        return learningPath;
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

    public LearningResourceType getResourceType() {
        return resourceType;
    }

    public String getUrl() {
        return url;
    }

    public String getSkillName() {
        return skillName;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
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

    public void setUser(User user) {
        this.user = user;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setResourceType(LearningResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;

        if (completed && completedAt == null) {
            completedAt = LocalDateTime.now();
        }

        if (!completed) {
            completedAt = null;
        }
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
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