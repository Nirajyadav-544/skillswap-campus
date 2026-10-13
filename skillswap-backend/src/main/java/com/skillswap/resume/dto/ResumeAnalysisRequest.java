package com.skillswap.resume.dto;

import jakarta.validation.constraints.Size;

public class ResumeAnalysisRequest {

    @Size(
            max = 20000,
            message = "Resume text cannot exceed 20000 characters"
    )
    private String resumeText;

    @Size(
            max = 300,
            message = "Target career cannot exceed 300 characters"
    )
    private String targetCareer;

    @Size(
            max = 10000,
            message = "Job description cannot exceed 10000 characters"
    )
    private String jobDescription;

    private Long resumeId;

    private Long userId;

    public ResumeAnalysisRequest() {
    }

    public void normalize() {

        if (resumeText != null) {
            resumeText = resumeText.trim();

            if (resumeText.isBlank()) {
                resumeText = null;
            }
        }

        if (targetCareer != null) {
            targetCareer = targetCareer.trim();

            if (targetCareer.isBlank()) {
                targetCareer = null;
            }
        }

        if (jobDescription != null) {
            jobDescription = jobDescription.trim();

            if (jobDescription.isBlank()) {
                jobDescription = null;
            }
        }
    }

    public boolean hasResumeText() {
        return resumeText != null &&
                !resumeText.isBlank();
    }

    public boolean hasTargetCareer() {
        return targetCareer != null &&
                !targetCareer.isBlank();
    }

    public boolean hasJobDescription() {
        return jobDescription != null &&
                !jobDescription.isBlank();
    }

    public boolean hasResumeId() {
        return resumeId != null;
    }

    public boolean hasUserId() {
        return userId != null;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public String getTargetCareer() {
        return targetCareer;
    }

    public void setTargetCareer(String targetCareer) {
        this.targetCareer = targetCareer;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}