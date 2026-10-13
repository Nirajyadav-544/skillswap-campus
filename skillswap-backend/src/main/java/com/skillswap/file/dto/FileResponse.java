package com.skillswap.file.dto;

import java.time.LocalDateTime;

public class FileResponse {

    private Long id;

    private String originalFileName;

    private String storedFileName;

    private String fileUrl;

    private String contentType;

    private Long fileSize;

    private String extension;

    private LocalDateTime uploadedAt;

    public FileResponse() {
    }

    public FileResponse(
            Long id,
            String originalFileName,
            String storedFileName,
            String fileUrl,
            String contentType,
            Long fileSize,
            String extension,
            LocalDateTime uploadedAt
    ) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileUrl = fileUrl;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.extension = extension;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getExtension() {
        return extension;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}