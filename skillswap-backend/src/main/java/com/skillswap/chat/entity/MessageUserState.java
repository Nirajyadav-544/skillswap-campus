package com.skillswap.chat.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "message_user_states",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_message_user_state",
                        columnNames = {"message_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_message_user_state_message", columnList = "message_id"),
                @Index(name = "idx_message_user_state_user", columnList = "user_id")
        }
)
public class MessageUserState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "deleted_for_user", nullable = false)
    private boolean deletedForUser = false;

    @Column(name = "starred", nullable = false)
    private boolean starred = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public MessageUserState() {
    }

    public MessageUserState(Message message, User user) {
        this.message = message;
        this.user = user;
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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isDeletedForUser() {
        return deletedForUser;
    }

    public void setDeletedForUser(boolean deletedForUser) {
        this.deletedForUser = deletedForUser;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public void markDeletedForUser() {
        this.deletedForUser = true;
    }

    public void restoreForUser() {
        this.deletedForUser = false;
    }

    public void star() {
        this.starred = true;
    }

    public void unstar() {
        this.starred = false;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}