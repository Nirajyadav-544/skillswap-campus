package com.skillswap.skill.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents the relationship between a User and a Skill.
 *
 * Examples:
 *
 * User: Rahul
 * Skill: Java
 * Type: TEACH
 * Proficiency: ADVANCED
 *
 * User: Rahul
 * Skill: React
 * Type: LEARN
 * Proficiency: BEGINNER
 *
 * User: Rahul
 * Skill: Python
 * Type: BOTH
 * Proficiency: INTERMEDIATE
 *
 * This entity is intentionally separate from Skill because
 * proficiency, experience and learning/teaching preferences
 * belong to the individual user, not to the skill itself.
 */
@Entity
@Table(
        name = "user_skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_skill",
                        columnNames = {
                                "user_id",
                                "skill_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_user_skill_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_user_skill_skill_id",
                        columnList = "skill_id"
                ),
                @Index(
                        name = "idx_user_skill_type",
                        columnList = "skill_type"
                ),
                @Index(
                        name = "idx_user_skill_proficiency",
                        columnList = "proficiency_level"
                ),
                @Index(
                        name = "idx_user_skill_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_user_skill_verified",
                        columnList = "verified"
                ),
                @Index(
                        name = "idx_user_skill_experience",
                        columnList = "years_of_experience"
                )
        }
)
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who owns this skill relationship.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    /**
     * Skill from the global skill catalog.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "skill_id",
            nullable = false
    )
    private Skill skill;

    /**
     * Whether the user wants to teach, learn, or both.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "skill_type",
            nullable = false,
            length = 20
    )
    private SkillType skillType;

    /**
     * Current self-declared proficiency.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "proficiency_level",
            nullable = false,
            length = 30
    )
    private ProficiencyLevel proficiencyLevel;

    /**
     * Approximate years of practical experience.
     *
     * Allows decimal values such as:
     *
     * 0.5
     * 1.0
     * 2.5
     */
    @Column(
            name = "years_of_experience",
            nullable = false
    )
    private double yearsOfExperience = 0.0;

    /**
     * Optional description of how the user knows or uses
     * the skill.
     */
    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;

    /**
     * Indicates whether the user is actively using this
     * skill in the SkillSwap system.
     *
     * Soft deactivation is used instead of deleting the
     * relationship so historical information can be retained.
     */
    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

    /**
     * Indicates whether this skill has been verified.
     *
     * Verification can later be performed through:
     *
     * - projects
     * - certificates
     * - assessments
     * - peer verification
     * - admin verification
     *
     * Initially this defaults to false.
     */
    @Column(
            name = "verified",
            nullable = false
    )
    private boolean verified = false;

    /**
     * Number of times this skill has been involved in
     * successful SkillSwap interactions.
     *
     * This can later contribute to matching/ranking algorithms.
     */
    @Column(
            name = "exchange_count",
            nullable = false
    )
    private long exchangeCount = 0L;

    /**
     * Number of users who have interacted with this
     * particular user through this skill.
     */
    @Column(
            name = "successful_exchange_count",
            nullable = false
    )
    private long successfulExchangeCount = 0L;

    /**
     * Optional rating accumulated for this skill.
     *
     * Stored separately from the global user rating because
     * a person may be highly rated for one skill and less
     * experienced in another.
     */
    @Column(
            name = "average_rating",
            nullable = false
    )
    private double averageRating = 0.0;

    /**
     * Number of ratings received for this skill.
     */
    @Column(
            name = "rating_count",
            nullable = false
    )
    private long ratingCount = 0L;

    /**
     * Creation timestamp.
     */
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    /**
     * Last modification timestamp.
     */
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    /**
     * Timestamp at which the skill was last used in
     * an active SkillSwap interaction.
     */
    @Column(
            name = "last_used_at"
    )
    private LocalDateTime lastUsedAt;

    public UserSkill() {
    }

    /**
     * Initializes timestamps and protects numeric fields
     * before inserting a record.
     */
    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        normalizeValues();
    }

    /**
     * Updates modification timestamp.
     */
    @PreUpdate
    protected void onUpdate() {

        this.updatedAt = LocalDateTime.now();

        normalizeValues();
    }

    /**
     * Prevents invalid numeric values from being persisted.
     */
    private void normalizeValues() {

        if (yearsOfExperience < 0) {
            yearsOfExperience = 0;
        }

        if (exchangeCount < 0) {
            exchangeCount = 0;
        }

        if (successfulExchangeCount < 0) {
            successfulExchangeCount = 0;
        }

        if (ratingCount < 0) {
            ratingCount = 0;
        }

        if (averageRating < 0) {
            averageRating = 0;
        }

        if (averageRating > 5) {
            averageRating = 5;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }

    public ProficiencyLevel getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(
            ProficiencyLevel proficiencyLevel
    ) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public double getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(
            double yearsOfExperience
    ) {

        this.yearsOfExperience =
                Math.max(0.0, yearsOfExperience);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public long getExchangeCount() {
        return exchangeCount;
    }

    public void setExchangeCount(long exchangeCount) {

        this.exchangeCount =
                Math.max(0L, exchangeCount);
    }

    public long getSuccessfulExchangeCount() {
        return successfulExchangeCount;
    }

    public void setSuccessfulExchangeCount(
            long successfulExchangeCount
    ) {

        this.successfulExchangeCount =
                Math.max(0L, successfulExchangeCount);
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(
            double averageRating
    ) {

        if (averageRating < 0) {
            this.averageRating = 0;
        } else if (averageRating > 5) {
            this.averageRating = 5;
        } else {
            this.averageRating = averageRating;
        }
    }

    public long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(long ratingCount) {

        this.ratingCount =
                Math.max(0L, ratingCount);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt
    ) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(
            LocalDateTime lastUsedAt
    ) {
        this.lastUsedAt = lastUsedAt;
    }

    /**
     * Returns true when the user is currently offering
     * this skill for teaching.
     */
    public boolean canTeach() {

        return active
                && (
                skillType == SkillType.TEACH
                        || skillType == SkillType.BOTH
        );
    }

    /**
     * Returns true when the user currently wants to learn
     * this skill.
     */
    public boolean wantsToLearn() {

        return active
                && (
                skillType == SkillType.LEARN
                        || skillType == SkillType.BOTH
        );
    }

    /**
     * Marks this skill as active.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Soft-deactivates this skill.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Marks the skill as verified.
     */
    public void markVerified() {
        this.verified = true;
    }

    /**
     * Removes verification status.
     */
    public void unverify() {
        this.verified = false;
    }

    /**
     * Records one exchange involving this skill.
     */
    public void recordExchange() {

        this.exchangeCount++;
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * Records one successful exchange.
     */
    public void recordSuccessfulExchange() {

        this.successfulExchangeCount++;
        this.exchangeCount++;
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * Updates the average rating using an incremental
     * calculation.
     *
     * This avoids loading every previous rating merely
     * to calculate the new average.
     */
    public void addRating(
            double rating
    ) {

        if (rating < 1.0 || rating > 5.0) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5"
            );
        }

        double totalRating =
                this.averageRating * this.ratingCount;

        this.ratingCount++;

        this.averageRating =
                (totalRating + rating)
                        / this.ratingCount;
    }

    /**
     * Returns a value useful for future matching algorithms.
     *
     * This is not a final AI score. It simply exposes useful
     * information without introducing AI logic into the entity.
     */
    public double getExperienceWeight() {

        return Math.min(
                yearsOfExperience / 10.0,
                1.0
        );
    }

    /**
     * Returns whether this skill has received ratings.
     */
    public boolean hasRatings() {

        return ratingCount > 0;
    }

    /**
     * Returns whether this is an experienced skill.
     */
    public boolean isExperienced() {

        return yearsOfExperience >= 2.0;
    }

    @Override
    public String toString() {

        return "UserSkill{" +
                "id=" + id +
                ", skillType=" + skillType +
                ", proficiencyLevel=" + proficiencyLevel +
                ", yearsOfExperience=" + yearsOfExperience +
                ", active=" + active +
                ", verified=" + verified +
                ", exchangeCount=" + exchangeCount +
                ", averageRating=" + averageRating +
                ", ratingCount=" + ratingCount +
                '}';
    }
}