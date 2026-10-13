package com.skillswap.chat.dto;

import com.skillswap.chat.entity.Conversation;
import com.skillswap.chat.entity.ConversationType;

import java.time.LocalDateTime;

public class ConversationSummaryResponse {

    private Long id;
    private ConversationType type;
    private String title;
    private boolean active;
    private String referenceType;
    private Long referenceId;
    private LocalDateTime lastMessageAt;
    private LocalDateTime updatedAt;

    public ConversationSummaryResponse() {
    }

    public static ConversationSummaryResponse from(Conversation conversation) {
        ConversationSummaryResponse response = new ConversationSummaryResponse();

        response.setId(conversation.getId());
        response.setType(conversation.getType());
        response.setTitle(conversation.getTitle());
        response.setActive(conversation.isActive());
        response.setReferenceType(conversation.getReferenceType());
        response.setReferenceId(conversation.getReferenceId());
        response.setLastMessageAt(conversation.getLastMessageAt());
        response.setUpdatedAt(conversation.getUpdatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}