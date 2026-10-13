package com.skillswap.chat.dto;

import com.skillswap.chat.entity.CallSignalType;
import jakarta.validation.constraints.NotNull;

public class CallSignalRequest {

    @NotNull
    private Long conversationId;

    @NotNull
    private Long targetUserId;

    @NotNull
    private CallSignalType type;

    private String payload;

    public CallSignalRequest() {
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
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