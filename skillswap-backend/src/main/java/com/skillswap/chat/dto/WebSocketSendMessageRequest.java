package com.skillswap.chat.dto;

public class WebSocketSendMessageRequest
        extends SendMessageRequest {

    private Long conversationId;

    public WebSocketSendMessageRequest() {
        super();
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
}