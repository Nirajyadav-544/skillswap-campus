package com.skillswap.matching.entity;

import com.skillswap.identity.entity.User;
import com.skillswap.skill.entity.ProficiencyLevel;
import com.skillswap.skill.entity.SkillCategory;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Stores the matching preferences of a user.
 *
 * Each user has at most one active preference configuration.
 *
 * These preferences are intentionally kept separate from User and
 * StudentProfile so that the matching engine can evolve independently.
 *
 * The entity contains both current Phase 4 requirements and fields
 * that will be useful for future recommendation improvements.
 */
@Entity
@Table(
        name = "match_preferences",
        indexes = {
                @Index(
                        name = "idx_match_preference_user",
                        columnList = "user_id",
                        unique = true
                ),
                @Index(
                        name = "idx_match_preference_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_match_preference_min_score",
                        columnList = "minimum_match_score"
                )
        }
)
public class MatchPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User whose matching preferences these are.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_match_preference_user")
    )
    private User user;

    /**
     * Whether these preferences are currently active.
     */
    @Column(nullable = false)
    private boolean active = true;

    /**
     * Minimum acceptable overall match score.
     *
     * Example:
     * 60.0 means recommendations below 60 should normally
     * not be returned by the recommendation service.
     */
    @Column(name = "minimum_match_score", nullable = false)
    private double minimumMatchScore = 50.0;

    /**
     * Minimum proficiency expected from a potential teacher.
     *
     * Nullable because the user may not care about a minimum level.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "minimum_proficiency", length = 30)
    private ProficiencyLevel minimumProficiency;

    /**
     * Preferred skill category.
     *
     * This provides a simple primary category preference.
     *
     * More advanced category preference handling can be introduced
     * later without changing the rest of the matching architecture.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_category", length = 50)
    private SkillCategory preferredCategory;

    /**
     * Minimum experience expected from a potential teacher.
     */
    @Column(name = "minimum_teacher_experience", nullable = false)
    private double minimumTeacherExperience = 0.0;

    /**
     * Whether verified users should be preferred.
     */
    @Column(name = "prefer_verified_users", nullable = false)
    private boolean preferVerifiedUsers = false;

    /**
     * Whether users with teaching experience should be preferred.
     */
    @Column(name = "prefer_experienced_teachers", nullable = false)
    private boolean preferExperiencedTeachers = false;

    /**
     * Whether users with ratings should be preferred.
     */
    @Column(name = "prefer_rated_users", nullable = false)
    private boolean preferRatedUsers = false;

    /**
     * Whether matching should consider profile completion.
     */
    @Column(name = "prefer_complete_profiles", nullable = false)
    private boolean preferCompleteProfiles = true;

    /**
     * Whether the user wants online matches.
     */
    @Column(name = "allow_online", nullable = false)
    private boolean allowOnline = true;

    /**
     * Whether the user wants offline matches.
     */
    @Column(name = "allow_offline", nullable = false)
    private boolean allowOffline = true;

    /**
     * Maximum number of recommendations returned in a
     * recommendation request.
     */
    @Column(name = "maximum_recommendations", nullable = false)
    private int maximumRecommendations = 20;

    /**
     * Whether users from the same college should receive
     * a preference boost.
     */
    @Column(name = "prefer_same_college", nullable = false)
    private boolean preferSameCollege = true;

    /**
     * Whether users from the same department should receive
     * a preference boost.
     */
    @Column(name = "prefer_same_department", nullable = false)
    private boolean preferSameDepartment = true;

    /**
     * Whether users from the same course should receive
     * a preference boost.
     */
    @Column(name = "prefer_same_course", nullable = false)
    private boolean preferSameCourse = true;

    /**
     * Optional preferred location.
     */
    @Column(name = "preferred_location", length = 200)
    private String preferredLocation;

    /**
     * Optional availability preference.
     *
     * Kept as text for Phase 4 because StudentProfile currently
     * stores availability as text. A structured availability model
     * can be introduced later without breaking this entity.
     */
    @Column(name = "preferred_availability", length = 500)
    private String preferredAvailability;

    /**
     * Timestamp when the preference record was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the preference record was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public MatchPreference() {
    }

    public MatchPreference(User user) {
        this.user = user;
        this.active = true;
        this.minimumMatchScore = 50.0;
        this.minimumTeacherExperience = 0.0;
        this.preferVerifiedUsers = false;
        this.preferExperiencedTeachers = false;
        this.preferRatedUsers = false;
        this.preferCompleteProfiles = true;
        this.allowOnline = true;
        this.allowOffline = true;
        this.maximumRecommendations = 20;
        this.preferSameCollege = true;
        this.preferSameDepartment = true;
        this.preferSameCourse = true;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;

        normalizeValues();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        normalizeValues();
    }

    /**
     * Keeps preference values inside safe application-level ranges.
     */
    private void normalizeValues() {

        if (minimumMatchScore < 0.0) {
            minimumMatchScore = 0.0;
        }

        if (minimumMatchScore > 100.0) {
            minimumMatchScore = 100.0;
        }

        if (minimumTeacherExperience < 0.0) {
            minimumTeacherExperience = 0.0;
        }

        if (minimumTeacherExperience > 80.0) {
            minimumTeacherExperience = 80.0;
        }

        if (maximumRecommendations < 1) {
            maximumRecommendations = 1;
        }

        if (maximumRecommendations > 100) {
            maximumRecommendations = 100;
        }

        if (preferredLocation != null) {
            preferredLocation = preferredLocation.trim();

            if (preferredLocation.isBlank()) {
                preferredLocation = null;
            }
        }

        if (preferredAvailability != null) {
            preferredAvailability = preferredAvailability.trim();

            if (preferredAvailability.isBlank()) {
                preferredAvailability = null;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public boolean isActive() {
        return active;
    }

    public double getMinimumMatchScore() {
        return minimumMatchScore;
    }

    public ProficiencyLevel getMinimumProficiency() {
        return minimumProficiency;
    }

    public SkillCategory getPreferredCategory() {
        return preferredCategory;
    }

    public double getMinimumTeacherExperience() {
        return minimumTeacherExperience;
    }

    public boolean isPreferVerifiedUsers() {
        return preferVerifiedUsers;
    }

    public boolean isPreferExperiencedTeachers() {
        return preferExperiencedTeachers;
    }

    public boolean isPreferRatedUsers() {
        return preferRatedUsers;
    }

    public boolean isPreferCompleteProfiles() {
        return preferCompleteProfiles;
    }

    public boolean isAllowOnline() {
        return allowOnline;
    }

    public boolean isAllowOffline() {
        return allowOffline;
    }

    public int getMaximumRecommendations() {
        return maximumRecommendations;
    }

    public boolean isPreferSameCollege() {
        return preferSameCollege;
    }

    public boolean isPreferSameDepartment() {
        return preferSameDepartment;
    }

    public boolean isPreferSameCourse() {
        return preferSameCourse;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public String getPreferredAvailability() {
        return preferredAvailability;
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

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setMinimumMatchScore(double minimumMatchScore) {
        this.minimumMatchScore = Math.max(0.0, Math.min(100.0, minimumMatchScore));
    }

    public void setMinimumProficiency(ProficiencyLevel minimumProficiency) {
        this.minimumProficiency = minimumProficiency;
    }

    public void setPreferredCategory(SkillCategory preferredCategory) {
        this.preferredCategory = preferredCategory;
    }

    public void setMinimumTeacherExperience(double minimumTeacherExperience) {
        this.minimumTeacherExperience =
                Math.max(0.0, Math.min(80.0, minimumTeacherExperience));
    }

    public void setPreferVerifiedUsers(boolean preferVerifiedUsers) {
        this.preferVerifiedUsers = preferVerifiedUsers;
    }

    public void setPreferExperiencedTeachers(boolean preferExperiencedTeachers) {
        this.preferExperiencedTeachers = preferExperiencedTeachers;
    }

    public void setPreferRatedUsers(boolean preferRatedUsers) {
        this.preferRatedUsers = preferRatedUsers;
    }

    public void setPreferCompleteProfiles(boolean preferCompleteProfiles) {
        this.preferCompleteProfiles = preferCompleteProfiles;
    }

    public void setAllowOnline(boolean allowOnline) {
        this.allowOnline = allowOnline;
    }

    public void setAllowOffline(boolean allowOffline) {
        this.allowOffline = allowOffline;
    }

    public void setMaximumRecommendations(int maximumRecommendations) {
        this.maximumRecommendations =
                Math.max(1, Math.min(100, maximumRecommendations));
    }

    public void setPreferSameCollege(boolean preferSameCollege) {
        this.preferSameCollege = preferSameCollege;
    }

    public void setPreferSameDepartment(boolean preferSameDepartment) {
        this.preferSameDepartment = preferSameDepartment;
    }

    public void setPreferSameCourse(boolean preferSameCourse) {
        this.preferSameCourse = preferSameCourse;
    }

    public void setPreferredLocation(String preferredLocation) {
        if (preferredLocation == null || preferredLocation.isBlank()) {
            this.preferredLocation = null;
        } else {
            this.preferredLocation = preferredLocation.trim();
        }
    }

    public void setPreferredAvailability(String preferredAvailability) {
        if (preferredAvailability == null || preferredAvailability.isBlank()) {
            this.preferredAvailability = null;
        } else {
            this.preferredAvailability = preferredAvailability.trim();
        }
    }

    /**
     * Returns whether this preference configuration can be used.
     */
    public boolean isUsable() {
        return active
                && minimumMatchScore >= 0.0
                && minimumMatchScore <= 100.0
                && maximumRecommendations > 0;
    }

    /**
     * Returns whether at least one interaction mode is enabled.
     */
    public boolean hasAvailableMode() {
        return allowOnline || allowOffline;
    }

    @Override
    public String toString() {
        return "MatchPreference{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", active=" + active +
                ", minimumMatchScore=" + minimumMatchScore +
                ", minimumProficiency=" + minimumProficiency +
                ", preferredCategory=" + preferredCategory +
                ", minimumTeacherExperience=" + minimumTeacherExperience +
                ", maximumRecommendations=" + maximumRecommendations +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}