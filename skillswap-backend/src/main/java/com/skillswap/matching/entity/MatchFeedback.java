package com.skillswap.matching.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Stores feedback provided by a user about a generated match.
 *
 * Feedback is deliberately stored as its own entity because it will
 * eventually become valuable training/evaluation data for the
 * SkillSwap matching engine.
 *
 * Example:
 *
 * User A receives:
 *
 *     User B
 *     Java -> TEACH
 *     Match score: 91
 *
 * User A can then provide:
 *
 *     INTERESTED
 *     HELPFUL
 *
 * The system can later use this historical feedback to evaluate
 * and improve recommendation quality.
 */
@Entity
@Table(
        name = "match_feedback",
        indexes = {
                @Index(
                        name = "idx_match_feedback_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_match_feedback_match",
                        columnList = "match_result_id"
                ),
                @Index(
                        name = "idx_match_feedback_type",
                        columnList = "feedback_type"
                ),
                @Index(
                        name = "idx_match_feedback_created",
                        columnList = "created_at"
                )
        }
)
public class MatchFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who provided the feedback.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_match_feedback_user")
    )
    private User user;

    /**
     * Match against which this feedback was provided.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "match_result_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_match_feedback_match")
    )
    private MatchResult matchResult;

    /**
     * Main feedback classification.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "feedback_type",
            nullable = false,
            length = 30
    )
    private MatchFeedbackType feedbackType;

    /**
     * Optional numeric rating supplied by the user.
     *
     * Range:
     *
     *     1 -> 5
     *
     * This is optional because not every feedback action needs
     * a numeric rating.
     */
    @Column(name = "rating")
    private Integer rating;

    /**
     * Optional free-form feedback.
     */
    @Column(
            name = "comment",
            length = 2000
    )
    private String comment;

    /**
     * Indicates whether this feedback was generated from an
     * explicit user action.
     *
     * This gives us flexibility later if the system generates
     * implicit feedback from behavior such as profile views.
     */
    @Column(
            name = "explicit_feedback",
            nullable = false
    )
    private boolean explicitFeedback = true;

    /**
     * Indicates whether this feedback has already been processed
     * by a future recommendation-learning pipeline.
     */
    @Column(
            name = "processed",
            nullable = false
    )
    private boolean processed = false;

    /**
     * Optional source describing where the feedback originated.
     *
     * Examples:
     *
     *     MATCH_PAGE
     *     RECOMMENDATION_LIST
     *     PROFILE_PAGE
     *     API
     */
    @Column(
            name = "source",
            length = 50
    )
    private String source;

    /**
     * Timestamp when feedback was created.
     */
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    /**
     * Timestamp when feedback was last updated.
     */
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public MatchFeedback() {
    }

    public MatchFeedback(
            User user,
            MatchResult matchResult,
            MatchFeedbackType feedbackType
    ) {
        this.user = user;
        this.matchResult = matchResult;
        this.feedbackType = feedbackType;
        this.explicitFeedback = true;
        this.processed = false;
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
     * Normalizes values before persistence.
     */
    private void normalizeValues() {

        if (rating != null) {

            if (rating < 1) {
                rating = 1;
            }

            if (rating > 5) {
                rating = 5;
            }
        }

        if (comment != null) {

            comment = comment.trim();

            if (comment.isBlank()) {
                comment = null;
            }
        }

        if (source != null) {

            source = source.trim();

            if (source.isBlank()) {
                source = null;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public MatchResult getMatchResult() {
        return matchResult;
    }

    public MatchFeedbackType getFeedbackType() {
        return feedbackType;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public boolean isExplicitFeedback() {
        return explicitFeedback;
    }

    public boolean isProcessed() {
        return processed;
    }

    public String getSource() {
        return source;
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

    public void setMatchResult(MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    public void setFeedbackType(MatchFeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public void setRating(Integer rating) {

        if (rating == null) {
            this.rating = null;
            return;
        }

        this.rating = Math.max(1, Math.min(5, rating));
    }

    public void setComment(String comment) {

        if (comment == null || comment.isBlank()) {
            this.comment = null;
        } else {
            this.comment = comment.trim();
        }
    }

    public void setExplicitFeedback(boolean explicitFeedback) {
        this.explicitFeedback = explicitFeedback;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public void setSource(String source) {

        if (source == null || source.isBlank()) {
            this.source = null;
        } else {
            this.source = source.trim();
        }
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Marks this feedback as processed by a learning/analytics
     * pipeline.
     */
    public void markProcessed() {
        this.processed = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks this feedback as unprocessed.
     */
    public void markUnprocessed() {
        this.processed = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Returns true when the feedback contains a numeric rating.
     */
    public boolean hasRating() {
        return rating != null;
    }

    /**
     * Returns true when the feedback contains a written comment.
     */
    public boolean hasComment() {
        return comment != null && !comment.isBlank();
    }

    /**
     * Returns true when this feedback represents positive
     * recommendation behavior.
     */
    public boolean isPositiveFeedback() {

        if (feedbackType == null) {
            return false;
        }

        return feedbackType == MatchFeedbackType.INTERESTED
                || feedbackType == MatchFeedbackType.HELPFUL
                || feedbackType == MatchFeedbackType.RELEVANT;
    }

    /**
     * Returns true when this feedback represents negative
     * recommendation behavior.
     */
    public boolean isNegativeFeedback() {

        if (feedbackType == null) {
            return false;
        }

        return feedbackType == MatchFeedbackType.NOT_INTERESTED
                || feedbackType == MatchFeedbackType.NOT_HELPFUL
                || feedbackType == MatchFeedbackType.NOT_RELEVANT
                || feedbackType == MatchFeedbackType.DISMISSED;
    }

    /**
     * Returns whether the entity contains enough information
     * to be persisted as meaningful feedback.
     */
    public boolean isValidFeedback() {

        return user != null
                && matchResult != null
                && feedbackType != null;
    }

    @Override
    public String toString() {

        return "MatchFeedback{" +
                "id=" + id +
                ", userId=" +
                (user != null ? user.getId() : null) +
                ", matchResultId=" +
                (matchResult != null ? matchResult.getId() : null) +
                ", feedbackType=" + feedbackType +
                ", rating=" + rating +
                ", explicitFeedback=" + explicitFeedback +
                ", processed=" + processed +
                ", createdAt=" + createdAt +
                '}';
    }
}