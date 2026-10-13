package com.skillswap.resume.dto;

import com.skillswap.resume.entity.ResumeVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateResumeRequest {

    @NotBlank(message = "Resume title is required")
    @Size(max = 200, message = "Resume title cannot exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 1000, message = "File URL cannot exceed 1000 characters")
    private String fileUrl;

    @Size(max = 255, message = "File name cannot exceed 255 characters")
    private String fileName;

    @Size(max = 150, message = "File type cannot exceed 150 characters")
    private String fileType;

    @Size(max = 100000, message = "Extracted text is too large")
    private String extractedText;

    private ResumeVisibility visibility = ResumeVisibility.PRIVATE;

    private Boolean primaryResume = false;

    public CreateResumeRequest() {
    }

    public void normalize() {

        if (title != null) {
            title = title.trim();
        }

        if (description != null) {
            description = description.trim();

            if (description.isBlank()) {
                description = null;
            }
        }

        if (fileUrl != null) {
            fileUrl = fileUrl.trim();

            if (fileUrl.isBlank()) {
                fileUrl = null;
            }
        }

        if (fileName != null) {
            fileName = fileName.trim();

            if (fileName.isBlank()) {
                fileName = null;
            }
        }

        if (fileType != null) {
            fileType = fileType.trim();

            if (fileType.isBlank()) {
                fileType = null;
            }
        }

        if (extractedText != null) {
            extractedText = extractedText.trim();

            if (extractedText.isBlank()) {
                extractedText = null;
            }
        }

        if (visibility == null) {
            visibility = ResumeVisibility.PRIVATE;
        }

        if (primaryResume == null) {
            primaryResume = false;
        }
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
}