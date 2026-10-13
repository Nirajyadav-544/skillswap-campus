package com.skillswap.chat.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "conversation_participants",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conversation_participant",
                        columnNames = {"conversation_id", "user_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_conversation_participant_conversation",
                        columnList = "conversation_id"
                ),
                @Index(
                        name = "idx_conversation_participant_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_conversation_participant_status",
                        columnList = "status"
                )
        }
)
public class ConversationParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ParticipantStatus status = ParticipantStatus.INVITED;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ConversationParticipant() {
    }

    public ConversationParticipant(
            Conversation conversation,
            User user
    ) {
        this.conversation = conversation;
        this.user = user;
        this.status = ParticipantStatus.INVITED;
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
            status = ParticipantStatus.INVITED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void activate() {
        status = ParticipantStatus.ACTIVE;
        joinedAt = LocalDateTime.now();
        leftAt = null;
    }

    public void invite() {
        status = ParticipantStatus.INVITED;
        leftAt = null;
    }

    public void leave() {
        status = ParticipantStatus.LEFT;
        leftAt = LocalDateTime.now();
    }

    public void remove() {
        status = ParticipantStatus.REMOVED;
        leftAt = LocalDateTime.now();
    }

    public void block() {
        status = ParticipantStatus.BLOCKED;
        leftAt = LocalDateTime.now();
    }

    public void markRead() {
        lastReadAt = LocalDateTime.now();
    }

    public boolean isInvited() {
        return status == ParticipantStatus.INVITED;
    }

    public boolean isActive() {
        return status == ParticipantStatus.ACTIVE;
    }

    public boolean hasLeft() {
        return status == ParticipantStatus.LEFT;
    }

    public boolean isRemoved() {
        return status == ParticipantStatus.REMOVED;
    }

    public boolean isBlocked() {
        return status == ParticipantStatus.BLOCKED;
    }

    public Long getId() {
        return id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ParticipantStatus getStatus() {
        return status;
    }

    public void setStatus(ParticipantStatus status) {
        this.status = status;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}