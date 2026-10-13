package com.skillswap.resume.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "resume_versions",
        indexes = {
                @Index(name = "idx_resume_version_resume", columnList = "resume_id"),
                @Index(name = "idx_resume_version_number", columnList = "version_number"),
                @Index(name = "idx_resume_version_created_at", columnList = "created_at")
        }
)
public class ResumeVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "resume_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_resume_version_resume")
    )
    private Resume resume;

    @Column(
            name = "version_number",
            nullable = false
    )
    private Integer versionNumber = 1;

    @Column(
            name = "title",
            nullable = false,
            length = 200
    )
    private String title;

    @Column(
            name = "description",
            length = 1000
    )
    private String description;

    @Column(
            name = "file_url",
            length = 1000
    )
    private String fileUrl;

    @Column(
            name = "file_name",
            length = 255
    )
    private String fileName;

    @Column(
            name = "file_type",
            length = 150
    )
    private String fileType;

    @Lob
    @Column(
            name = "extracted_text",
            columnDefinition = "LONGTEXT"
    )
    private String extractedText;

    @Column(
            name = "ai_score"
    )
    private Double aiScore;

    @Column(
            name = "change_summary",
            length = 1000
    )
    private String changeSummary;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    public ResumeVersion() {
    }

    public ResumeVersion(
            Resume resume,
            Integer versionNumber,
            String title
    ) {
        this.resume = resume;
        this.versionNumber = versionNumber;
        this.title = title;
    }

    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        normalize();
    }

    @PreUpdate
    protected void onUpdate() {

        normalize();
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

        if (changeSummary != null) {
            changeSummary = changeSummary.trim();

            if (changeSummary.isBlank()) {
                changeSummary = null;
            }
        }

        if (versionNumber == null || versionNumber < 1) {
            versionNumber = 1;
        }

        if (aiScore != null) {

            if (aiScore < 0) {
                aiScore = 0.0;
            }

            if (aiScore > 100) {
                aiScore = 100.0;
            }
        }
    }

    public boolean hasFile() {
        return fileUrl != null && !fileUrl.isBlank();
    }

    public boolean hasExtractedText() {
        return extractedText != null && !extractedText.isBlank();
    }

    public boolean hasAiAnalysis() {
        return aiScore != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
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
}