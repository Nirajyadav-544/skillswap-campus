package com.skillswap.skill.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents a skill available in the SkillSwap Campus ecosystem.
 *
 * Examples:
 *
 * Java
 * Python
 * React
 * Spring Boot
 * Machine Learning
 * Graphic Design
 * Public Speaking
 *
 * A Skill is a reusable catalog item.
 * Users are connected to skills through UserSkill.
 */
@Entity
@Table(
        name = "skills",
        indexes = {
                @Index(
                        name = "idx_skill_name",
                        columnList = "name"
                ),
                @Index(
                        name = "idx_skill_category",
                        columnList = "category"
                ),
                @Index(
                        name = "idx_skill_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_skill_popularity",
                        columnList = "popularity_score"
                )
        }
)
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "name",
            nullable = false,
            unique = true,
            length = 150
    )
    private String name;

    @Column(
            name = "normalized_name",
            nullable = false,
            unique = true,
            length = 150
    )
    private String normalizedName;

    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "category",
            nullable = false,
            length = 50
    )
    private SkillCategory category;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

    @Column(
            name = "system_defined",
            nullable = false
    )
    private boolean systemDefined = false;

    @Column(
            name = "popularity_score",
            nullable = false
    )
    private long popularityScore = 0L;

    @Column(
            name = "user_count",
            nullable = false
    )
    private long userCount = 0L;

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

    public Skill() {
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        normalizeCounters();
    }

    @PreUpdate
    protected void onUpdate() {

        this.updatedAt = LocalDateTime.now();

        normalizeCounters();
    }

    private void normalizeCounters() {

        if (this.popularityScore < 0) {
            this.popularityScore = 0;
        }

        if (this.userCount < 0) {
            this.userCount = 0;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isSystemDefined() {
        return systemDefined;
    }

    public void setSystemDefined(boolean systemDefined) {
        this.systemDefined = systemDefined;
    }

    public long getPopularityScore() {
        return popularityScore;
    }

    public void setPopularityScore(long popularityScore) {
        this.popularityScore = Math.max(0L, popularityScore);
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = Math.max(0L, userCount);
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

    /**
     * Increases the popularity score by one.
     */
    public void incrementPopularity() {
        this.popularityScore++;
    }

    /**
     * Increases the popularity score by one.
     *
     * This method is kept for compatibility with SkillService.
     */
    public void incrementPopularityScore() {
        this.popularityScore++;
    }

    /**
     * Increases the popularity score by a specific amount.
     */
    public void incrementPopularity(long amount) {

        if (amount > 0) {
            this.popularityScore += amount;
        }
    }

    /**
     * Decreases popularity safely.
     */
    public void decrementPopularity() {

        if (this.popularityScore > 0) {
            this.popularityScore--;
        }
    }

    /**
     * Decreases the popularity score by one.
     *
     * This method is kept for compatibility with SkillService.
     */
    public void decrementPopularityScore() {

        if (this.popularityScore > 0) {
            this.popularityScore--;
        }
    }

    /**
     * Increases the number of associated users.
     */
    public void incrementUserCount() {
        this.userCount++;
    }

    /**
     * Decreases the number of associated users safely.
     */
    public void decrementUserCount() {

        if (this.userCount > 0) {
            this.userCount--;
        }
    }

    /**
     * Returns whether this skill can currently be selected
     * by users.
     */
    public boolean isAvailable() {
        return active;
    }

    @Override
    public String toString() {

        return "Skill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", active=" + active +
                ", systemDefined=" + systemDefined +
                ", popularityScore=" + popularityScore +
                ", userCount=" + userCount +
                '}';
    }
}