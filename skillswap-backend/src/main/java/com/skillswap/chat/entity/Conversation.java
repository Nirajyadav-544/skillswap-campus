package com.skillswap.chat.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "conversations",
        indexes = {
                @Index(name = "idx_conversation_type", columnList = "type"),
                @Index(name = "idx_conversation_created", columnList = "created_at"),
                @Index(name = "idx_conversation_updated", columnList = "updated_at")
        }
)
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ConversationType type = ConversationType.DIRECT;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "reference_type", length = 100)
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Conversation() {
    }

    public Conversation(
            ConversationType type,
            String title
    ) {
        this.type = type;
        this.title = title;
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

        if (type == null) {
            type = ConversationType.DIRECT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        normalize();
        updatedAt = LocalDateTime.now();
    }

    public void normalize() {
        if (title != null) {
            title = title.trim();
        }

        if (description != null) {
            description = description.trim();
        }

        if (referenceType != null) {
            referenceType = referenceType.trim();
        }
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public void updateLastMessageTime() {
        lastMessageAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDirect() {
        return type == ConversationType.DIRECT;
    }

    public boolean isGroup() {
        return type == ConversationType.GROUP;
    }

    public boolean isProject() {
        return type == ConversationType.PROJECT;
    }

    public boolean isExchange() {
        return type == ConversationType.EXCHANGE;
    }

    public boolean isSession() {
        return type == ConversationType.SESSION;
    }

    public Long getId() {
        return id;
    }

    public ConversationType getType() {
        return type;
    }

    public void setType(ConversationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}