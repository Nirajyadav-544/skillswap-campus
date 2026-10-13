package com.skillswap.chat.dto;

import com.skillswap.chat.entity.CallSignalType;

public class CallSignalResponse {

    private Long conversationId;

    private Long senderUserId;

    private Long targetUserId;

    private CallSignalType type;

    private String payload;

    public CallSignalResponse() {
    }

    public CallSignalResponse(
            Long conversationId,
            Long senderUserId,
            Long targetUserId,
            CallSignalType type,
            String payload
    ) {
        this.conversationId = conversationId;
        this.senderUserId = senderUserId;
        this.targetUserId = targetUserId;
        this.type = type;
        this.payload = payload;
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

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public CallSignalType getType() {
        return type;
    }

    public void setType(CallSignalType type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}