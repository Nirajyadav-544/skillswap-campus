package com.skillswap.resume.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "resumes",
        indexes = {
                @Index(name = "idx_resume_user", columnList = "user_id"),
                @Index(name = "idx_resume_status", columnList = "status"),
                @Index(name = "idx_resume_visibility", columnList = "visibility"),
                @Index(name = "idx_resume_created_at", columnList = "created_at"),
                @Index(name = "idx_resume_updated_at", columnList = "updated_at")
        }
)
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Owner of this resume.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_resume_user")
    )
    private User user;

    /**
     * Human-readable resume title.
     *
     * Example:
     * "Software Developer Resume"
     */
    @Column(
            name = "title",
            nullable = false,
            length = 200
    )
    private String title;

    /**
     * Short description of the resume.
     */
    @Column(
            name = "description",
            length = 1000
    )
    private String description;

    /**
     * Optional file URL/path if the resume is uploaded as a document.
     */
    @Column(
            name = "file_url",
            length = 1000
    )
    private String fileUrl;

    /**
     * Original uploaded filename.
     */
    @Column(
            name = "file_name",
            length = 255
    )
    private String fileName;

    /**
     * MIME type of uploaded resume.
     *
     * Examples:
     * application/pdf
     * application/vnd.openxmlformats-officedocument.wordprocessingml.document
     */
    @Column(
            name = "file_type",
            length = 150
    )
    private String fileType;

    /**
     * Extracted resume text.
     *
     * This will later be used by the AI resume-analysis system.
     */
    @Lob
    @Column(
            name = "extracted_text",
            columnDefinition = "LONGTEXT"
    )
    private String extractedText;

    /**
     * Current resume status.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private ResumeStatus status = ResumeStatus.DRAFT;

    /**
     * Who can see this resume.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "visibility",
            nullable = false,
            length = 30
    )
    private ResumeVisibility visibility = ResumeVisibility.PRIVATE;

    /**
     * Whether this is the user's primary resume.
     */
    @Column(
            name = "primary_resume",
            nullable = false
    )
    private Boolean primaryResume = false;

    /**
     * Version number.
     */
    @Column(
            name = "version_number",
            nullable = false
    )
    private Integer versionNumber = 1;

    /**
     * AI analysis score.
     *
     * Example:
     * 78.5
     */
    @Column(
            name = "ai_score"
    )
    private Double aiScore;

    /**
     * Last time AI analysis was performed.
     */
    @Column(
            name = "last_analyzed_at"
    )
    private LocalDateTime lastAnalyzedAt;

    /**
     * Record creation timestamp.
     */
    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    /**
     * Last update timestamp.
     */
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public Resume() {
    }

    public Resume(
            User user,
            String title
    ) {
        this.user = user;
        this.title = title;
        this.status = ResumeStatus.DRAFT;
        this.visibility = ResumeVisibility.PRIVATE;
        this.primaryResume = false;
        this.versionNumber = 1;
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (status == null) {
            status = ResumeStatus.DRAFT;
        }

        if (visibility == null) {
            visibility = ResumeVisibility.PRIVATE;
        }

        if (primaryResume == null) {
            primaryResume = false;
        }

        if (versionNumber == null || versionNumber < 1) {
            versionNumber = 1;
        }

        normalize();
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

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

        if (status == null) {
            status = ResumeStatus.DRAFT;
        }

        if (visibility == null) {
            visibility = ResumeVisibility.PRIVATE;
        }

        if (primaryResume == null) {
            primaryResume = false;
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

    public void activate() {

        this.status = ResumeStatus.ACTIVE;

        if (this.versionNumber == null || this.versionNumber < 1) {
            this.versionNumber = 1;
        }
    }

    public void archive() {

        this.status = ResumeStatus.ARCHIVED;
    }

    public void delete() {

        this.status = ResumeStatus.DELETED;
    }

    public void restore() {

        if (this.status == ResumeStatus.DELETED ||
                this.status == ResumeStatus.ARCHIVED) {

            this.status = ResumeStatus.DRAFT;
        }
    }

    public void makePrimary() {

        this.primaryResume = true;
    }

    public void removePrimary() {

        this.primaryResume = false;
    }

    public void incrementVersion() {

        if (this.versionNumber == null || this.versionNumber < 1) {
            this.versionNumber = 1;
        } else {
            this.versionNumber++;
        }
    }

    public void markAnalyzed(Double score) {

        if (score != null) {

            if (score < 0) {
                score = 0.0;
            }

            if (score > 100) {
                score = 100.0;
            }
        }

        this.aiScore = score;
        this.lastAnalyzedAt = LocalDateTime.now();
    }

    public boolean isDraft() {

        return status == ResumeStatus.DRAFT;
    }

    public boolean isActive() {

        return status == ResumeStatus.ACTIVE;
    }

    public boolean isArchived() {

        return status == ResumeStatus.ARCHIVED;
    }

    public boolean isDeleted() {

        return status == ResumeStatus.DELETED;
    }

    public boolean isPrivate() {

        return visibility == ResumeVisibility.PRIVATE;
    }

    public boolean isCampusVisible() {

        return visibility == ResumeVisibility.CAMPUS;
    }

    public boolean isPublic() {

        return visibility == ResumeVisibility.PUBLIC;
    }

    public boolean isPrimary() {

        return Boolean.TRUE.equals(primaryResume);
    }

    public boolean hasExtractedText() {

        return extractedText != null &&
                !extractedText.isBlank();
    }

    public boolean hasFile() {

        return fileUrl != null &&
                !fileUrl.isBlank();
    }

    public boolean hasAiAnalysis() {

        return aiScore != null &&
                lastAnalyzedAt != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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