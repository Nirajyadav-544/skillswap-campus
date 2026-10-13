package com.skillswap.rating.entity;

import com.skillswap.identity.entity.User;
import com.skillswap.session.entity.LearningSession;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ratings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_rating_session_rater_rated_category",
                        columnNames = {
                                "session_id",
                                "rater_user_id",
                                "rated_user_id",
                                "category"
                        }
                )
        },
        indexes = {
                @Index(name = "idx_rating_session", columnList = "session_id"),
                @Index(name = "idx_rating_rater", columnList = "rater_user_id"),
                @Index(name = "idx_rating_rated", columnList = "rated_user_id"),
                @Index(name = "idx_rating_status", columnList = "status"),
                @Index(name = "idx_rating_score", columnList = "score"),
                @Index(name = "idx_rating_created_at", columnList = "created_at")
        }
)
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private LearningSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rater_user_id", nullable = false)
    private User raterUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rated_user_id", nullable = false)
    private User ratedUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RatingCategory category = RatingCategory.OVERALL;

    @Column(nullable = false)
    private Integer score;

    @Column(length = 2000)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RatingStatus status = RatingStatus.ACTIVE;

    @Column(nullable = false)
    private boolean verified = true;

    @Column(nullable = false)
    private boolean edited = false;

    @Column(nullable = false)
    private boolean anonymous = false;

    @Column(length = 1000)
    private String moderationReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime editedAt;

    @Column
    private LocalDateTime deletedAt;

    public Rating() {
    }

    public Rating(
            LearningSession session,
            User raterUser,
            User ratedUser,
            RatingCategory category,
            Integer score
    ) {
        this.session = session;
        this.raterUser = raterUser;
        this.ratedUser = ratedUser;
        this.category = category != null
                ? category
                : RatingCategory.OVERALL;
        this.score = score;
        this.status = RatingStatus.ACTIVE;
        this.verified = true;
        this.edited = false;
        this.anonymous = false;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;

        if (category == null) {
            category = RatingCategory.OVERALL;
        }

        if (status == null) {
            status = RatingStatus.ACTIVE;
        }

        if (score != null) {
            score = Math.max(1, Math.min(5, score));
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        if (score != null) {
            score = Math.max(1, Math.min(5, score));
        }
    }

    public Long getId() {
        return id;
    }

    public LearningSession getSession() {
        return session;
    }

    public void setSession(LearningSession session) {
        this.session = session;
    }

    public User getRaterUser() {
        return raterUser;
    }

    public void setRaterUser(User raterUser) {
        this.raterUser = raterUser;
    }

    public User getRatedUser() {
        return ratedUser;
    }

    public void setRatedUser(User ratedUser) {
        this.ratedUser = ratedUser;
    }

    public RatingCategory getCategory() {
        return category;
    }

    public void setCategory(RatingCategory category) {
        this.category = category;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        if (score != null && (score < 1 || score > 5)) {
            throw new IllegalArgumentException("Rating score must be between 1 and 5");
        }

        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = normalize(comment);
    }

    public RatingStatus getStatus() {
        return status;
    }

    public void setStatus(RatingStatus status) {
        this.status = status;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getModerationReason() {
        return moderationReason;
    }

    public void setModerationReason(String moderationReason) {
        this.moderationReason = normalize(moderationReason);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void markEdited() {
        this.edited = true;
        this.editedAt = LocalDateTime.now();
    }

    public void hide(String reason) {
        this.status = RatingStatus.HIDDEN;
        this.moderationReason = normalize(reason);
    }

    public void flag(String reason) {
        this.status = RatingStatus.FLAGGED;
        this.moderationReason = normalize(reason);
    }

    public void activate() {
        this.status = RatingStatus.ACTIVE;
        this.moderationReason = null;
    }

    public void delete() {
        this.status = RatingStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == RatingStatus.ACTIVE;
    }

    public boolean isHidden() {
        return status == RatingStatus.HIDDEN;
    }

    public boolean isFlagged() {
        return status == RatingStatus.FLAGGED;
    }

    public boolean isDeleted() {
        return status == RatingStatus.DELETED;
    }

    public boolean isVisible() {
        return status == RatingStatus.ACTIVE;
    }

    public boolean belongsToSession(Long sessionId) {
        if (sessionId == null || session == null || session.getId() == null) {
            return false;
        }

        return sessionId.equals(session.getId());
    }

    public boolean isRatedBy(Long userId) {
        if (userId == null || raterUser == null || raterUser.getId() == null) {
            return false;
        }

        return userId.equals(raterUser.getId());
    }

    public boolean isForUser(Long userId) {
        if (userId == null || ratedUser == null || ratedUser.getId() == null) {
            return false;
        }

        return userId.equals(ratedUser.getId());
    }

    public boolean isValidScore() {
        return score != null && score >= 1 && score <= 5;
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isBlank() ? null : normalized;
    }
}