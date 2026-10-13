package com.skillswap.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AiRequest {

    @NotBlank(message = "Prompt is required")
    @Size(max = 10000, message = "Prompt must not exceed 10000 characters")
    private String prompt;

    @Size(max = 100, message = "Context type must not exceed 100 characters")
    private String contextType;

    private Long contextId;

    public AiRequest() {
    }

    public AiRequest(String prompt) {
        this.prompt = prompt;
    }

    public AiRequest(
            String prompt,
            String contextType,
            Long contextId
    ) {
        this.prompt = prompt;
        this.contextType = contextType;
        this.contextId = contextId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getContextType() {
        return contextType;
    }

    public void setContextType(String contextType) {
        this.contextType = contextType;
    }

    public Long getContextId() {
        return contextId;
    }

    public void setContextId(Long contextId) {
        this.contextId = contextId;
    }

    public void normalize() {
        if (prompt != null) {
            prompt = prompt.trim();
        }

        if (contextType != null) {
            contextType = contextType.trim();
        }
    }

    public boolean hasContext() {
        return contextType != null
                && !contextType.isBlank()
                && contextId != null;
    }
}