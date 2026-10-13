package com.skillswap.project.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "project_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_member",
                        columnNames = {"project_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_project_member_project", columnList = "project_id"),
                @Index(name = "idx_project_member_user", columnList = "user_id"),
                @Index(name = "idx_project_member_status", columnList = "status")
        }
)
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectMemberRole role = ProjectMemberRole.CONTRIBUTOR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectMemberStatus status = ProjectMemberStatus.INVITED;

    @Column(length = 1000)
    private String contribution;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ProjectMember() {
    }

    public ProjectMember(Project project, User user) {
        this.project = project;
        this.user = user;
        this.role = ProjectMemberRole.CONTRIBUTOR;
        this.status = ProjectMemberStatus.INVITED;
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

        if (role == null) {
            role = ProjectMemberRole.CONTRIBUTOR;
        }

        if (status == null) {
            status = ProjectMemberStatus.INVITED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        normalize();
        updatedAt = LocalDateTime.now();
    }

    private void normalize() {
        if (contribution != null) {
            contribution = contribution.trim();
        }
    }

    public void activate() {
        this.status = ProjectMemberStatus.ACTIVE;

        if (this.joinedAt == null) {
            this.joinedAt = LocalDateTime.now();
        }

        this.leftAt = null;
    }

    public void invite() {
        this.status = ProjectMemberStatus.INVITED;
    }

    public void deactivate() {
        this.status = ProjectMemberStatus.INACTIVE;
    }

    public void remove() {
        this.status = ProjectMemberStatus.REMOVED;
        this.leftAt = LocalDateTime.now();
    }

    public void leave() {
        this.status = ProjectMemberStatus.LEFT;
        this.leftAt = LocalDateTime.now();
    }

    public boolean isInvited() {
        return status == ProjectMemberStatus.INVITED;
    }

    public boolean isActive() {
        return status == ProjectMemberStatus.ACTIVE;
    }

    public boolean isInactive() {
        return status == ProjectMemberStatus.INACTIVE;
    }

    public boolean isRemoved() {
        return status == ProjectMemberStatus.REMOVED;
    }

    public boolean hasLeft() {
        return status == ProjectMemberStatus.LEFT;
    }

    public boolean isOwner() {
        return role == ProjectMemberRole.OWNER;
    }

    public boolean isLead() {
        return role == ProjectMemberRole.LEAD;
    }

    public boolean canManageProject() {
        return role == ProjectMemberRole.OWNER
                || role == ProjectMemberRole.LEAD;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ProjectMemberRole getRole() {
        return role;
    }

    public void setRole(ProjectMemberRole role) {
        this.role = role;
    }

    public ProjectMemberStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectMemberStatus status) {
        this.status = status;
    }

    public String getContribution() {
        return contribution;
    }

    public void setContribution(String contribution) {
        this.contribution = contribution;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(LocalDateTime leftAt) {
        this.leftAt = leftAt;
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