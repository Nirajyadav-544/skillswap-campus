package com.skillswap.career.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "career_goals",
        indexes = {
                @Index(
                        name = "idx_career_goal_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_career_goal_status",
                        columnList = "status"
                )
        }
)
public class CareerGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            name = "goal_title",
            nullable = false,
            length = 200
    )
    private String goalTitle;

    @Column(
            name = "description",
            length = 2000
    )
    private String description;

    @Column(
            name = "target_role",
            length = 200
    )
    private String targetRole;

    @Column(
            name = "target_company",
            length = 200
    )
    private String targetCompany;

    @Column(
            name = "target_date"
    )
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private CareerGoalStatus status;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public CareerGoal() {
    }

    public CareerGoal(
            User user,
            String goalTitle,
            String description,
            String targetRole,
            String targetCompany,
            LocalDate targetDate
    ) {
        this.user = user;
        this.goalTitle = goalTitle;
        this.description = description;
        this.targetRole = targetRole;
        this.targetCompany = targetCompany;
        this.targetDate = targetDate;
        this.status = CareerGoalStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (status == null) {
            status = CareerGoalStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getGoalTitle() {
        return goalTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public String getTargetCompany() {
        return targetCompany;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public CareerGoalStatus getStatus() {
        return status;
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

    public void setGoalTitle(String goalTitle) {
        this.goalTitle = goalTitle;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public void setTargetCompany(String targetCompany) {
        this.targetCompany = targetCompany;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public void setStatus(CareerGoalStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}