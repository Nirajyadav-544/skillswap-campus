package com.skillswap.chat.dto;

import com.skillswap.chat.entity.ConversationType;
import com.skillswap.chat.entity.MessageStatus;
import com.skillswap.chat.entity.MessageType;

public class ChatSearchRequest {

    private ConversationType conversationType;

    private MessageType messageType;

    private MessageStatus messageStatus;

    private String keyword;

    private Long conversationId;

    private Long userId;

    private int page = 0;

    private int size = 20;

    public ChatSearchRequest() {
    }

    public ConversationType getConversationType() {
        return conversationType;
    }

    public void setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void normalize() {
        if (keyword != null) {
            keyword = keyword.trim();

            if (keyword.isEmpty()) {
                keyword = null;
            }
        }

        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }
    }
}