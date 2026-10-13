package com.skillswap.rating.entity;

import com.skillswap.identity.entity.User;
import com.skillswap.session.entity.LearningSession;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "reviews",
        indexes = {
                @Index(name = "idx_review_rating", columnList = "rating_id"),
                @Index(name = "idx_review_session", columnList = "session_id"),
                @Index(name = "idx_review_author", columnList = "author_user_id"),
                @Index(name = "idx_review_target", columnList = "target_user_id"),
                @Index(name = "idx_review_created_at", columnList = "created_at")
        }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "rating_id",
            nullable = false,
            unique = true
    )
    private Rating rating;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private LearningSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_user_id", nullable = false)
    private User authorUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private boolean visible = true;

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

    public Review() {
    }

    public Review(
            Rating rating,
            LearningSession session,
            User authorUser,
            User targetUser,
            String content
    ) {
        this.rating = rating;
        this.session = session;
        this.authorUser = authorUser;
        this.targetUser = targetUser;
        this.content = normalize(content);
        this.visible = true;
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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public LearningSession getSession() {
        return session;
    }

    public void setSession(LearningSession session) {
        this.session = session;
    }

    public User getAuthorUser() {
        return authorUser;
    }

    public void setAuthorUser(User authorUser) {
        this.authorUser = authorUser;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = normalize(content);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isEdited() {
        return edited;
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
        this.visible = false;
        this.moderationReason = normalize(reason);
    }

    public void show() {
        this.visible = true;
        this.moderationReason = null;
    }

    public void delete() {
        this.visible = false;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isVisibleToUsers() {
        return visible && deletedAt == null;
    }

    public boolean isWrittenBy(Long userId) {
        if (userId == null || authorUser == null || authorUser.getId() == null) {
            return false;
        }

        return userId.equals(authorUser.getId());
    }

    public boolean isForUser(Long userId) {
        if (userId == null || targetUser == null || targetUser.getId() == null) {
            return false;
        }

        return userId.equals(targetUser.getId());
    }

    public boolean belongsToSession(Long sessionId) {
        if (sessionId == null || session == null || session.getId() == null) {
            return false;
        }

        return sessionId.equals(session.getId());
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isBlank() ? null : normalized;
    }
}