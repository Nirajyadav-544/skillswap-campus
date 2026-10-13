package com.skillswap.resume.dto;

public class ResumeExportResponse {

    private Long resumeId;
    private Long versionId;

    private String fileName;
    private String fileType;
    private String fileUrl;

    private boolean exported;
    private String message;

    public ResumeExportResponse() {
    }

    public ResumeExportResponse(
            Long resumeId,
            Long versionId,
            String fileName,
            String fileType,
            String fileUrl,
            boolean exported,
            String message
    ) {
        this.resumeId = resumeId;
        this.versionId = versionId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
        this.exported = exported;
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

    public boolean isExported() {
        return exported;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}