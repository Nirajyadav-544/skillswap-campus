package com.skillswap.chat.dto;

import com.skillswap.chat.entity.Message;
import com.skillswap.chat.entity.MessageStatus;
import com.skillswap.chat.entity.MessageType;

import java.time.LocalDateTime;

public class MessageSummaryResponse {

    private Long id;
    private Long conversationId;
    private Long senderUserId;
    private String senderEmail;
    private MessageType type;
    private MessageStatus status;
    private String content;

    private String attachmentUrl;
    private String attachmentName;
    private String attachmentType;

    private Long replyToMessageId;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;

    public MessageSummaryResponse() {
    }

    public static MessageSummaryResponse from(Message message) {
        MessageSummaryResponse response = new MessageSummaryResponse();

        response.setId(message.getId());

        if (message.getConversation() != null) {
            response.setConversationId(message.getConversation().getId());
        }

        if (message.getSenderUser() != null) {
            response.setSenderUserId(message.getSenderUser().getId());
            response.setSenderEmail(message.getSenderUser().getEmail());
        }

        response.setType(message.getType());
        response.setStatus(message.getStatus());
        response.setContent(message.getContent());

        response.setAttachmentUrl(message.getAttachmentUrl());
        response.setAttachmentName(message.getAttachmentName());
        response.setAttachmentType(message.getAttachmentType());

        response.setReplyToMessageId(message.getReplyToMessageId());
        response.setCreatedAt(message.getCreatedAt());
        response.setEditedAt(message.getEditedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }
}