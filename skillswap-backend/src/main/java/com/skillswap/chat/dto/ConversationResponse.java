package com.skillswap.chat.dto;

import com.skillswap.chat.entity.Conversation;
import com.skillswap.chat.entity.ConversationType;

import java.time.LocalDateTime;

public class ConversationResponse {

    private Long id;
    private ConversationType type;
    private String title;
    private String description;
    private String referenceType;
    private Long referenceId;
    private boolean active;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ConversationResponse() {
    }

    public static ConversationResponse from(Conversation conversation) {
        ConversationResponse response = new ConversationResponse();

        response.id = conversation.getId();
        response.type = conversation.getType();
        response.title = conversation.getTitle();
        response.description = conversation.getDescription();
        response.referenceType = conversation.getReferenceType();
        response.referenceId = conversation.getReferenceId();
        response.active = conversation.isActive();
        response.lastMessageAt = conversation.getLastMessageAt();
        response.createdAt = conversation.getCreatedAt();
        response.updatedAt = conversation.getUpdatedAt();

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

    public boolean isActive() {
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}