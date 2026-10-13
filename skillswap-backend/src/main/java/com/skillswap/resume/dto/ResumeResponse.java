package com.skillswap.resume.dto;

import com.skillswap.resume.entity.Resume;
import com.skillswap.resume.entity.ResumeStatus;
import com.skillswap.resume.entity.ResumeVisibility;

import java.time.LocalDateTime;

public class ResumeResponse {

    private Long id;

    private Long userId;

    private String title;

    private String description;

    private String fileUrl;

    private String fileName;

    private String fileType;

    private String extractedText;

    private ResumeStatus status;

    private ResumeVisibility visibility;

    private Boolean primaryResume;

    private Integer versionNumber;

    private Double aiScore;

    private LocalDateTime lastAnalyzedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ResumeResponse() {
    }

    public static ResumeResponse from(Resume resume) {

        if (resume == null) {
            return null;
        }

        ResumeResponse response = new ResumeResponse();

        response.setId(resume.getId());

        if (resume.getUser() != null) {
            response.setUserId(resume.getUser().getId());
        }

        response.setTitle(resume.getTitle());
        response.setDescription(resume.getDescription());
        response.setFileUrl(resume.getFileUrl());
        response.setFileName(resume.getFileName());
        response.setFileType(resume.getFileType());
        response.setExtractedText(resume.getExtractedText());
        response.setStatus(resume.getStatus());
        response.setVisibility(resume.getVisibility());
        response.setPrimaryResume(resume.getPrimaryResume());
        response.setVersionNumber(resume.getVersionNumber());
        response.setAiScore(resume.getAiScore());
        response.setLastAnalyzedAt(resume.getLastAnalyzedAt());
        response.setCreatedAt(resume.getCreatedAt());
        response.setUpdatedAt(resume.getUpdatedAt());

        return response;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
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