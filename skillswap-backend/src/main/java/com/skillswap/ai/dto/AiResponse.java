package com.skillswap.ai.dto;

import java.time.LocalDateTime;

public class AiResponse {

    private boolean success;
    private String message;
    private String result;
    private String interactionType;
    private Long interactionId;
    private LocalDateTime createdAt;

    public AiResponse() {
    }

    public AiResponse(
            boolean success,
            String message,
            String result
    ) {
        this.success = success;
        this.message = message;
        this.result = result;
        this.createdAt = LocalDateTime.now();
    }

    public static AiResponse success(
            String result
    ) {
        return new AiResponse(
                true,
                "AI request processed successfully",
                result
        );
    }

    public static AiResponse success(
            String message,
            String result
    ) {
        return new AiResponse(
                true,
                message,
                result
        );
    }

    public static AiResponse failure(
            String message
    ) {
        return new AiResponse(
                false,
                message,
                null
        );
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }

    public Long getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(Long interactionId) {
        this.interactionId = interactionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}