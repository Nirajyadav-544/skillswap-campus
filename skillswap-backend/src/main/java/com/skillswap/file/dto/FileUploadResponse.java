package com.skillswap.file.dto;

public class FileUploadResponse {

    private Long fileId;

    private String originalFileName;

    private String fileUrl;

    private String contentType;

    private Long fileSize;

    private String extension;

    private String message;

    public FileUploadResponse() {
    }

    public FileUploadResponse(
            Long fileId,
            String originalFileName,
            String fileUrl,
            String contentType,
            Long fileSize,
            String extension,
            String message
    ) {
        this.fileId = fileId;
        this.originalFileName = originalFileName;
        this.fileUrl = fileUrl;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.extension = extension;
        this.message = message;
    }

    public Long getFileId() {
        return fileId;
    }

    public String getOriginalFileName() {
        return originalFileName;
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

    public String getMessage() {
        return message;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
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

    public void setMessage(String message) {
        this.message = message;
    }
}