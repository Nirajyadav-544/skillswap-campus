package com.skillswap.resume.dto;

public class ResumeUploadResponse {

    private Long resumeId;
    private Long versionId;
    private Integer versionNumber;

    private String fileName;
    private String fileType;
    private String fileUrl;

    private Long fileSize;

    private boolean uploaded;
    private boolean extracted;

    private String message;

    public ResumeUploadResponse() {
    }

    public ResumeUploadResponse(
            Long resumeId,
            Long versionId,
            Integer versionNumber,
            String fileName,
            String fileType,
            String fileUrl,
            Long fileSize,
            boolean uploaded,
            boolean extracted,
            String message
    ) {
        this.resumeId = resumeId;
        this.versionId = versionId;
        this.versionNumber = versionNumber;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.uploaded = uploaded;
        this.extracted = extracted;
        this.message = message;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public boolean isExtracted() {
        return extracted;
    }

    public void setExtracted(boolean extracted) {
        this.extracted = extracted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}