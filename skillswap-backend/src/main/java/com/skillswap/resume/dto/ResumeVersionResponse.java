package com.skillswap.resume.dto;

import com.skillswap.resume.entity.ResumeVersion;

import java.time.LocalDateTime;

public class ResumeVersionResponse {

    private Long id;

    private Long resumeId;

    private Integer versionNumber;

    private String title;

    private String description;

    private String fileUrl;

    private String fileName;

    private String fileType;

    private String extractedText;

    private Double aiScore;

    private String changeSummary;

    private LocalDateTime createdAt;

    private boolean hasFile;

    private boolean hasExtractedText;

    private boolean hasAiAnalysis;

    public ResumeVersionResponse() {
    }

    public static ResumeVersionResponse from(
            ResumeVersion version
    ) {

        if (version == null) {
            return null;
        }

        ResumeVersionResponse response =
                new ResumeVersionResponse();

        response.id = version.getId();

        if (version.getResume() != null) {
            response.resumeId =
                    version.getResume().getId();
        }

        response.versionNumber =
                version.getVersionNumber();

        response.title =
                version.getTitle();

        response.description =
                version.getDescription();

        response.fileUrl =
                version.getFileUrl();

        response.fileName =
                version.getFileName();

        response.fileType =
                version.getFileType();

        response.extractedText =
                version.getExtractedText();

        response.aiScore =
                version.getAiScore();

        response.changeSummary =
                version.getChangeSummary();

        response.createdAt =
                version.getCreatedAt();

        response.hasFile =
                version.hasFile();

        response.hasExtractedText =
                version.hasExtractedText();

        response.hasAiAnalysis =
                version.hasAiAnalysis();

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
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

    public Double getAiScore() {
        return aiScore;
    }

    public void setAiScore(Double aiScore) {
        this.aiScore = aiScore;
    }

    public String getChangeSummary() {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isHasFile() {
        return hasFile;
    }

    public void setHasFile(boolean hasFile) {
        this.hasFile = hasFile;
    }

    public boolean isHasExtractedText() {
        return hasExtractedText;
    }

    public void setHasExtractedText(boolean hasExtractedText) {
        this.hasExtractedText = hasExtractedText;
    }

    public boolean isHasAiAnalysis() {
        return hasAiAnalysis;
    }

    public void setHasAiAnalysis(boolean hasAiAnalysis) {
        this.hasAiAnalysis = hasAiAnalysis;
    }
}