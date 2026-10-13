package com.skillswap.resume.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "resume_analyses",
        indexes = {
                @Index(
                        name = "idx_resume_analysis_resume",
                        columnList = "resume_id"
                ),
                @Index(
                        name = "idx_resume_analysis_created_at",
                        columnList = "created_at"
                )
        }
)
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "resume_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_resume_analysis_resume"
            )
    )
    private Resume resume;

    @Column(
            name = "overall_score"
    )
    private Double overallScore;

    @Column(
            name = "ats_score"
    )
    private Double atsScore;

    @Column(
            name = "skills_score"
    )
    private Double skillsScore;

    @Column(
            name = "experience_score"
    )
    private Double experienceScore;

    @Column(
            name = "education_score"
    )
    private Double educationScore;

    @Lob
    @Column(
            name = "strengths",
            columnDefinition = "LONGTEXT"
    )
    private String strengths;

    @Lob
    @Column(
            name = "weaknesses",
            columnDefinition = "LONGTEXT"
    )
    private String weaknesses;

    @Lob
    @Column(
            name = "recommendations",
            columnDefinition = "LONGTEXT"
    )
    private String recommendations;

    @Lob
    @Column(
            name = "analysis_text",
            columnDefinition = "LONGTEXT"
    )
    private String analysisText;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    public ResumeAnalysis() {
    }

    public ResumeAnalysis(
            Resume resume,
            Double overallScore
    ) {
        this.resume = resume;
        this.overallScore = overallScore;
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

        if (overallScore != null) {
            overallScore = clampScore(overallScore);
        }

        if (atsScore != null) {
            atsScore = clampScore(atsScore);
        }

        if (skillsScore != null) {
            skillsScore = clampScore(skillsScore);
        }

        if (experienceScore != null) {
            experienceScore = clampScore(experienceScore);
        }

        if (educationScore != null) {
            educationScore = clampScore(educationScore);
        }
    }

    private Double clampScore(Double score) {

        if (score < 0) {
            return 0.0;
        }

        if (score > 100) {
            return 100.0;
        }

        return score;
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

    public Double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }

    public Double getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(Double atsScore) {
        this.atsScore = atsScore;
    }

    public Double getSkillsScore() {
        return skillsScore;
    }

    public void setSkillsScore(Double skillsScore) {
        this.skillsScore = skillsScore;
    }

    public Double getExperienceScore() {
        return experienceScore;
    }

    public void setExperienceScore(Double experienceScore) {
        this.experienceScore = experienceScore;
    }

    public Double getEducationScore() {
        return educationScore;
    }

    public void setEducationScore(Double educationScore) {
        this.educationScore = educationScore;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public String getAnalysisText() {
        return analysisText;
    }

    public void setAnalysisText(String analysisText) {
        this.analysisText = analysisText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}