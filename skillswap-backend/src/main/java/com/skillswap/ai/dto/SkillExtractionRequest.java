package com.skillswap.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SkillExtractionRequest {

    @NotBlank(message = "Text is required")
    @Size(
            max = 20000,
            message = "Text must not exceed 20000 characters"
    )
    private String text;

    @Size(
            max = 100,
            message = "Source type must not exceed 100 characters"
    )
    private String sourceType;

    private Long sourceId;

    public SkillExtractionRequest() {
    }

    public SkillExtractionRequest(String text) {
        this.text = text;
    }

    public SkillExtractionRequest(
            String text,
            String sourceType,
            Long sourceId
    ) {
        this.text = text;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public void normalize() {
        if (text != null) {
            text = text.trim();
        }

        if (sourceType != null) {
            sourceType = sourceType.trim();
        }
    }

    public boolean hasSource() {
        return sourceType != null
                && !sourceType.isBlank()
                && sourceId != null;
    }
}