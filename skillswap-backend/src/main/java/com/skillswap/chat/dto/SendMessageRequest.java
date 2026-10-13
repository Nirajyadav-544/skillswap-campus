package com.skillswap.chat.dto;

import com.skillswap.chat.entity.MessageType;
import jakarta.validation.constraints.Size;

public class SendMessageRequest {

    private MessageType type = MessageType.TEXT;

    @Size(max = 10000)
    private String content;

    @Size(max = 1000)
    private String attachmentUrl;

    @Size(max = 255)
    private String attachmentName;

    @Size(max = 100)
    private String attachmentType;

    private Long replyToMessageId;

    public SendMessageRequest() {
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
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

    public void normalize() {
        if (type == null) {
            type = MessageType.TEXT;
        }

        if (content != null) {
            content = content.trim();

            if (content.isEmpty()) {
                content = null;
            }
        }

        if (attachmentUrl != null) {
            attachmentUrl = attachmentUrl.trim();

            if (attachmentUrl.isEmpty()) {
                attachmentUrl = null;
            }
        }

        if (attachmentName != null) {
            attachmentName = attachmentName.trim();

            if (attachmentName.isEmpty()) {
                attachmentName = null;
            }
        }

        if (attachmentType != null) {
            attachmentType = attachmentType.trim();

            if (attachmentType.isEmpty()) {
                attachmentType = null;
            }
        }
    }

    public boolean hasContent() {
        return content != null && !content.isBlank();
    }

    public boolean hasAttachment() {
        return attachmentUrl != null && !attachmentUrl.isBlank();
    }

    public boolean isEmpty() {
        return !hasContent() && !hasAttachment();
    }
}