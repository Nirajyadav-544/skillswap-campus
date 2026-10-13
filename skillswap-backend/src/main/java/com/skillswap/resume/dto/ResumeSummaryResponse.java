package com.skillswap.resume.dto;

import com.skillswap.resume.entity.Resume;
import com.skillswap.resume.entity.ResumeStatus;
import com.skillswap.resume.entity.ResumeVisibility;

import java.time.LocalDateTime;

public class ResumeSummaryResponse {

    private Long id;

    private Long userId;

    private String title;

    private ResumeStatus status;

    private ResumeVisibility visibility;

    private Boolean primaryResume;

    private Integer versionNumber;

    private Double aiScore;

    private LocalDateTime lastAnalyzedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ResumeSummaryResponse() {
    }

    public ResumeSummaryResponse(
            Long id,
            Long userId,
            String title,
            ResumeStatus status,
            ResumeVisibility visibility,
            Boolean primaryResume,
            Integer versionNumber,
            Double aiScore,
            LocalDateTime lastAnalyzedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.status = status;
        this.visibility = visibility;
        this.primaryResume = primaryResume;
        this.versionNumber = versionNumber;
        this.aiScore = aiScore;
        this.lastAnalyzedAt = lastAnalyzedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ResumeSummaryResponse from(Resume resume) {

        if (resume == null) {
            return null;
        }

        Long userId = null;

        if (resume.getUser() != null) {
            userId = resume.getUser().getId();
        }

        return new ResumeSummaryResponse(
                resume.getId(),
                userId,
                resume.getTitle(),
                resume.getStatus(),
                resume.getVisibility(),
                resume.getPrimaryResume(),
                resume.getVersionNumber(),
                resume.getAiScore(),
                resume.getLastAnalyzedAt(),
                resume.getCreatedAt(),
                resume.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ResumeStatus getStatus() {
        return status;
    }

    public void setStatus(ResumeStatus status) {
        this.status = status;
    }

    public ResumeVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ResumeVisibility visibility) {
        this.visibility = visibility;
    }

    public Boolean getPrimaryResume() {
        return primaryResume;
    }

    public void setPrimaryResume(Boolean primaryResume) {
        this.primaryResume = primaryResume;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Double getAiScore() {
        return aiScore;
    }

    public void setAiScore(Double aiScore) {
        this.aiScore = aiScore;
    }

    public LocalDateTime getLastAnalyzedAt() {
        return lastAnalyzedAt;
    }

    public void setLastAnalyzedAt(LocalDateTime lastAnalyzedAt) {
        this.lastAnalyzedAt = lastAnalyzedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}