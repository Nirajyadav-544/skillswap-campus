package com.skillswap.chat.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "messages",
        indexes = {
                @Index(name = "idx_message_conversation", columnList = "conversation_id"),
                @Index(name = "idx_message_sender", columnList = "sender_user_id"),
                @Index(name = "idx_message_status", columnList = "status"),
                @Index(name = "idx_message_created", columnList = "created_at")
        }
)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_user_id", nullable = false)
    private User senderUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MessageType type = MessageType.TEXT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MessageStatus status = MessageStatus.SENT;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "attachment_url", length = 1000)
    private String attachmentUrl;

    @Column(name = "attachment_name", length = 255)
    private String attachmentName;

    @Column(name = "attachment_type", length = 100)
    private String attachmentType;

    @Column(name = "reply_to_message_id")
    private Long replyToMessageId;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Message() {
    }

    public Message(
            Conversation conversation,
            User senderUser,
            MessageType type,
            String content
    ) {
        this.conversation = conversation;
        this.senderUser = senderUser;
        this.type = type;
        this.content = content;
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
            type = MessageType.TEXT;
        }

        if (status == null) {
            status = MessageStatus.SENT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        normalize();
        updatedAt = LocalDateTime.now();
    }

    public void normalize() {
        if (content != null) {
            content = content.trim();
        }

        if (attachmentUrl != null) {
            attachmentUrl = attachmentUrl.trim();
        }

        if (attachmentName != null) {
            attachmentName = attachmentName.trim();
        }

        if (attachmentType != null) {
            attachmentType = attachmentType.trim();
        }
    }

    public void markDelivered() {
        if (status != MessageStatus.DELETED) {
            status = MessageStatus.DELIVERED;
        }
    }

    public void markRead() {
        if (status != MessageStatus.DELETED) {
            status = MessageStatus.READ;
        }
    }

    public void markEdited(String newContent) {
        if (status == MessageStatus.DELETED) {
            return;
        }

        content = newContent;
        status = MessageStatus.EDITED;
        editedAt = LocalDateTime.now();
    }

    public void markDeleted() {
        status = MessageStatus.DELETED;
        deletedAt = LocalDateTime.now();
        content = null;
        attachmentUrl = null;
    }

    public void markFailed() {
        status = MessageStatus.FAILED;
    }

    public boolean isDeleted() {
        return status == MessageStatus.DELETED;
    }

    public boolean isEdited() {
        return status == MessageStatus.EDITED || editedAt != null;
    }

    public boolean isRead() {
        return status == MessageStatus.READ;
    }

    public boolean isDelivered() {
        return status == MessageStatus.DELIVERED;
    }

    public boolean isSent() {
        return status == MessageStatus.SENT;
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

    public User getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(User senderUser) {
        this.senderUser = senderUser;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public Long getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(Long replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}