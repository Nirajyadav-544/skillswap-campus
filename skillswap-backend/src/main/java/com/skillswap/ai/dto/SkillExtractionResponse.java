package com.skillswap.ai.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SkillExtractionResponse {

    private boolean success;
    private String message;
    private List<String> skills;
    private String sourceType;
    private Long sourceId;
    private LocalDateTime createdAt;

    public SkillExtractionResponse() {
        this.skills = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public SkillExtractionResponse(
            boolean success,
            String message,
            List<String> skills
    ) {
        this.success = success;
        this.message = message;
        this.skills = skills == null
                ? new ArrayList<>()
                : new ArrayList<>(skills);
        this.createdAt = LocalDateTime.now();
    }

    public static SkillExtractionResponse success(
            List<String> skills
    ) {
        return new SkillExtractionResponse(
                true,
                "Skills extracted successfully",
                skills
        );
    }

    public static SkillExtractionResponse failure(
            String message
    ) {
        return new SkillExtractionResponse(
                false,
                message,
                new ArrayList<>()
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

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills == null
                ? new ArrayList<>()
                : new ArrayList<>(skills);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}