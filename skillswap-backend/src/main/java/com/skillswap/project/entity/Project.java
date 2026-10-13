package com.skillswap.project.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "projects",
        indexes = {
                @Index(name = "idx_project_owner", columnList = "owner_user_id"),
                @Index(name = "idx_project_status", columnList = "status"),
                @Index(name = "idx_project_skill", columnList = "skill_name"),
                @Index(name = "idx_project_created", columnList = "created_at")
        }
)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User ownerUser;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 3000)
    private String description;

    @Column(name = "skill_name", length = 150)
    private String skillName;

    @Column(length = 150)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus status = ProjectStatus.DRAFT;

    @Column(name = "max_members", nullable = false)
    private Integer maxMembers = 10;

    @Column(name = "current_members", nullable = false)
    private Integer currentMembers = 1;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "target_end_date")
    private LocalDate targetEndDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(length = 1000)
    private String requirements;

    @Column(length = 2000)
    private String goals;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "demo_url", length = 500)
    private String demoUrl;

    @Column(name = "project_url", length = 500)
    private String projectUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Project() {
    }

    public Project(User ownerUser, String title, String description) {
        this.ownerUser = ownerUser;
        this.title = title;
        this.description = description;
        this.status = ProjectStatus.DRAFT;
        this.maxMembers = 10;
        this.currentMembers = 1;
    }

    @PrePersist
    protected void onCreate() {
        normalize();

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        normalize();
        updatedAt = LocalDateTime.now();
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

        if (category != null) {
            category = category.trim();
        }

        if (requirements != null) {
            requirements = requirements.trim();
        }

        if (goals != null) {
            goals = goals.trim();
        }

        if (githubUrl != null) {
            githubUrl = githubUrl.trim();
        }

        if (demoUrl != null) {
            demoUrl = demoUrl.trim();
        }

        if (projectUrl != null) {
            projectUrl = projectUrl.trim();
        }

        if (status == null) {
            status = ProjectStatus.DRAFT;
        }

        if (maxMembers == null || maxMembers < 1) {
            maxMembers = 10;
        }

        if (currentMembers == null || currentMembers < 0) {
            currentMembers = 0;
        }

        if (currentMembers > maxMembers) {
            currentMembers = maxMembers;
        }
    }

    public void open() {
        this.status = ProjectStatus.OPEN;

        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }
    }

    public void start() {
        this.status = ProjectStatus.IN_PROGRESS;

        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }
    }

    public void putOnHold() {
        this.status = ProjectStatus.ON_HOLD;
    }

    public void resume() {
        this.status = ProjectStatus.IN_PROGRESS;
    }

    public void complete() {
        this.status = ProjectStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = ProjectStatus.CANCELLED;
    }

    public void archive() {
        this.status = ProjectStatus.ARCHIVED;
    }

    public boolean canAddMember() {
        return currentMembers != null
                && maxMembers != null
                && currentMembers < maxMembers
                && status != ProjectStatus.COMPLETED
                && status != ProjectStatus.CANCELLED
                && status != ProjectStatus.ARCHIVED;
    }

    public void incrementMemberCount() {
        if (currentMembers == null) {
            currentMembers = 0;
        }

        if (maxMembers == null || currentMembers < maxMembers) {
            currentMembers++;
        }
    }

    public void decrementMemberCount() {
        if (currentMembers != null && currentMembers > 0) {
            currentMembers--;
        }
    }

    public boolean isDraft() {
        return status == ProjectStatus.DRAFT;
    }

    public boolean isOpen() {
        return status == ProjectStatus.OPEN;
    }

    public boolean isInProgress() {
        return status == ProjectStatus.IN_PROGRESS;
    }

    public boolean isOnHold() {
        return status == ProjectStatus.ON_HOLD;
    }

    public boolean isCompleted() {
        return status == ProjectStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == ProjectStatus.CANCELLED;
    }

    public boolean isArchived() {
        return status == ProjectStatus.ARCHIVED;
    }

    public Long getId() {
        return id;
    }

    public User getOwnerUser() {
        return ownerUser;
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

    public boolean canAcceptMoreMembers() {
    int current = currentMembers == null ? 0 : currentMembers;
    int maximum = maxMembers == null ? 0 : maxMembers;

    return current < maximum;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setOwnerUser(User ownerUser) {
        this.ownerUser = ownerUser;
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

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public void setCurrentMembers(Integer currentMembers) {
        this.currentMembers = currentMembers;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setTargetEndDate(LocalDate targetEndDate) {
        this.targetEndDate = targetEndDate;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}