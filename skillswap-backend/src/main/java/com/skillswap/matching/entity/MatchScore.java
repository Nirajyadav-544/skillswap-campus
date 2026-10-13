package com.skillswap.matching.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Stores the detailed score breakdown for a MatchResult.
 *
 * MatchResult contains the final overall score.
 * MatchScore contains the individual components used to calculate
 * that overall score.
 *
 * This separation is important because the frontend can later show
 * users WHY two people were recommended instead of displaying only
 * one unexplained number.
 *
 * It also gives the future AI/ML layer a clean place to store
 * additional scoring information.
 */
@Entity
@Table(
        name = "match_scores",
        indexes = {
                @Index(
                        name = "idx_match_score_match_result",
                        columnList = "match_result_id",
                        unique = true
                ),
                @Index(
                        name = "idx_match_score_overall",
                        columnList = "overall_score"
                ),
                @Index(
                        name = "idx_match_score_skill",
                        columnList = "skill_score"
                )
        }
)
public class MatchScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Match result for which this score was calculated.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "match_result_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_match_score_result")
    )
    private MatchResult matchResult;

    /**
     * Overall score from 0 to 100.
     */
    @Column(name = "overall_score", nullable = false)
    private double overallScore = 0.0;

    /**
     * Skill compatibility component.
     *
     * This is normally the strongest component of the score.
     */
    @Column(name = "skill_score", nullable = false)
    private double skillScore = 0.0;

    /**
     * Teaching/learning compatibility component.
     */
    @Column(name = "skill_type_score", nullable = false)
    private double skillTypeScore = 0.0;

    /**
     * Proficiency compatibility component.
     */
    @Column(name = "proficiency_score", nullable = false)
    private double proficiencyScore = 0.0;

    /**
     * Experience compatibility component.
     */
    @Column(name = "experience_score", nullable = false)
    private double experienceScore = 0.0;

    /**
     * Profile compatibility component.
     */
    @Column(name = "profile_score", nullable = false)
    private double profileScore = 0.0;

    /**
     * Availability compatibility component.
     */
    @Column(name = "availability_score", nullable = false)
    private double availabilityScore = 0.0;

    /**
     * Location compatibility component.
     */
    @Column(name = "location_score", nullable = false)
    private double locationScore = 0.0;

    /**
     * Verification/trust component.
     */
    @Column(name = "verification_score", nullable = false)
    private double verificationScore = 0.0;

    /**
     * Rating/reputation component.
     */
    @Column(name = "rating_score", nullable = false)
    private double ratingScore = 0.0;

    /**
     * College compatibility component.
     */
    @Column(name = "college_score", nullable = false)
    private double collegeScore = 0.0;

    /**
     * Department compatibility component.
     */
    @Column(name = "department_score", nullable = false)
    private double departmentScore = 0.0;

    /**
     * Course compatibility component.
     */
    @Column(name = "course_score", nullable = false)
    private double courseScore = 0.0;

    /**
     * Number of skills contributing to the match.
     */
    @Column(name = "matching_skill_count", nullable = false)
    private int matchingSkillCount = 0;

    /**
     * Number of highly compatible skills.
     */
    @Column(name = "strong_matching_skill_count", nullable = false)
    private int strongMatchingSkillCount = 0;

    /**
     * Algorithm used to generate this score.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "algorithm", nullable = false, length = 30)
    private MatchAlgorithm algorithm = MatchAlgorithm.RULE_BASED;

    /**
     * Optional explanation of the score.
     */
    @Column(name = "score_explanation", length = 3000)
    private String scoreExplanation;

    /**
     * Timestamp when the score was generated.
     */
    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    /**
     * Timestamp of the latest recalculation.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public MatchScore() {
    }

    public MatchScore(MatchResult matchResult) {
        this.matchResult = matchResult;

        if (matchResult != null) {
            this.overallScore = matchResult.getOverallScore();
            this.algorithm = matchResult.getAlgorithm();
        }

        this.calculatedAt = LocalDateTime.now();
        this.updatedAt = this.calculatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (calculatedAt == null) {
            calculatedAt = now;
        }

        updatedAt = now;

        if (algorithm == null) {
            algorithm = MatchAlgorithm.RULE_BASED;
        }

        normalizeValues();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        normalizeValues();
    }

    private void normalizeValues() {

        overallScore = normalizeScore(overallScore);
        skillScore = normalizeScore(skillScore);
        skillTypeScore = normalizeScore(skillTypeScore);
        proficiencyScore = normalizeScore(proficiencyScore);
        experienceScore = normalizeScore(experienceScore);
        profileScore = normalizeScore(profileScore);
        availabilityScore = normalizeScore(availabilityScore);
        locationScore = normalizeScore(locationScore);
        verificationScore = normalizeScore(verificationScore);
        ratingScore = normalizeScore(ratingScore);
        collegeScore = normalizeScore(collegeScore);
        departmentScore = normalizeScore(departmentScore);
        courseScore = normalizeScore(courseScore);

        if (matchingSkillCount < 0) {
            matchingSkillCount = 0;
        }

        if (strongMatchingSkillCount < 0) {
            strongMatchingSkillCount = 0;
        }

        if (strongMatchingSkillCount > matchingSkillCount) {
            strongMatchingSkillCount = matchingSkillCount;
        }

        if (scoreExplanation != null) {
            scoreExplanation = scoreExplanation.trim();

            if (scoreExplanation.isBlank()) {
                scoreExplanation = null;
            }
        }
    }

    private double normalizeScore(double value) {

        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }

        return Math.max(0.0, Math.min(100.0, value));
    }

    public Long getId() {
        return id;
    }

    public MatchResult getMatchResult() {
        return matchResult;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public double getSkillScore() {
        return skillScore;
    }

    public double getSkillTypeScore() {
        return skillTypeScore;
    }

    public double getProficiencyScore() {
        return proficiencyScore;
    }

    public double getExperienceScore() {
        return experienceScore;
    }

    public double getProfileScore() {
        return profileScore;
    }

    public double getAvailabilityScore() {
        return availabilityScore;
    }

    public double getLocationScore() {
        return locationScore;
    }

    public double getVerificationScore() {
        return verificationScore;
    }

    public double getRatingScore() {
        return ratingScore;
    }

    public double getCollegeScore() {
        return collegeScore;
    }

    public double getDepartmentScore() {
        return departmentScore;
    }

    public double getCourseScore() {
        return courseScore;
    }

    public int getMatchingSkillCount() {
        return matchingSkillCount;
    }

    public int getStrongMatchingSkillCount() {
        return strongMatchingSkillCount;
    }

    public MatchAlgorithm getAlgorithm() {
        return algorithm;
    }

    public String getScoreExplanation() {
        return scoreExplanation;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMatchResult(MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = normalizeScore(overallScore);
    }

    public void setSkillScore(double skillScore) {
        this.skillScore = normalizeScore(skillScore);
    }

    public void setSkillTypeScore(double skillTypeScore) {
        this.skillTypeScore = normalizeScore(skillTypeScore);
    }

    public void setProficiencyScore(double proficiencyScore) {
        this.proficiencyScore = normalizeScore(proficiencyScore);
    }

    public void setExperienceScore(double experienceScore) {
        this.experienceScore = normalizeScore(experienceScore);
    }

    public void setProfileScore(double profileScore) {
        this.profileScore = normalizeScore(profileScore);
    }

    public void setAvailabilityScore(double availabilityScore) {
        this.availabilityScore = normalizeScore(availabilityScore);
    }

    public void setLocationScore(double locationScore) {
        this.locationScore = normalizeScore(locationScore);
    }

    public void setVerificationScore(double verificationScore) {
        this.verificationScore = normalizeScore(verificationScore);
    }

    public void setRatingScore(double ratingScore) {
        this.ratingScore = normalizeScore(ratingScore);
    }

    public void setCollegeScore(double collegeScore) {
        this.collegeScore = normalizeScore(collegeScore);
    }

    public void setDepartmentScore(double departmentScore) {
        this.departmentScore = normalizeScore(departmentScore);
    }

    public void setCourseScore(double courseScore) {
        this.courseScore = normalizeScore(courseScore);
    }

    public void setMatchingSkillCount(int matchingSkillCount) {
        this.matchingSkillCount = Math.max(0, matchingSkillCount);

        if (strongMatchingSkillCount > this.matchingSkillCount) {
            strongMatchingSkillCount = this.matchingSkillCount;
        }
    }

    public void setStrongMatchingSkillCount(int strongMatchingSkillCount) {
        this.strongMatchingSkillCount =
                Math.max(
                        0,
                        Math.min(strongMatchingSkillCount, matchingSkillCount)
                );
    }

    public void setAlgorithm(MatchAlgorithm algorithm) {
        this.algorithm =
                algorithm != null ? algorithm : MatchAlgorithm.RULE_BASED;
    }

    public void setScoreExplanation(String scoreExplanation) {

        if (scoreExplanation == null || scoreExplanation.isBlank()) {
            this.scoreExplanation = null;
        } else {
            this.scoreExplanation = scoreExplanation.trim();
        }
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Recalculates the overall score from the individual components.
     *
     * These weights are intentionally centralized here for the initial
     * Phase 4 deterministic engine.
     *
     * The total weight is exactly 100.
     */
    public void calculateWeightedOverallScore() {

        double calculated =
                (skillScore * 0.30)
                        + (skillTypeScore * 0.15)
                        + (proficiencyScore * 0.10)
                        + (experienceScore * 0.10)
                        + (profileScore * 0.08)
                        + (availabilityScore * 0.05)
                        + (locationScore * 0.04)
                        + (verificationScore * 0.04)
                        + (ratingScore * 0.04)
                        + (collegeScore * 0.04)
                        + (departmentScore * 0.03)
                        + (courseScore * 0.03);

        this.overallScore = normalizeScore(calculated);
    }

    /**
     * Copies the final score to the associated MatchResult.
     */
    public void synchronizeWithMatchResult() {

        if (matchResult == null) {
            return;
        }

        matchResult.setOverallScore(overallScore);
        matchResult.setAlgorithm(algorithm);
        matchResult.setMatchingSkillCount(matchingSkillCount);
    }

    /**
     * Returns whether the score is strong enough to be considered
     * a high-quality recommendation.
     */
    public boolean isStrongMatch() {
        return overallScore >= 80.0;
    }

    /**
     * Returns whether this score is a medium-quality match.
     */
    public boolean isModerateMatch() {
        return overallScore >= 60.0 && overallScore < 80.0;
    }

    /**
     * Returns whether this score is below the normal recommendation
     * threshold.
     */
    public boolean isWeakMatch() {
        return overallScore < 60.0;
    }

    /**
     * Returns whether at least one compatible skill exists.
     */
    public boolean hasMatchingSkills() {
        return matchingSkillCount > 0;
    }

    /**
     * Returns whether there are strong compatible skills.
     */
    public boolean hasStrongMatchingSkills() {
        return strongMatchingSkillCount > 0;
    }

    @Override
    public String toString() {
        return "MatchScore{" +
                "id=" + id +
                ", matchResultId=" +
                (matchResult != null ? matchResult.getId() : null) +
                ", overallScore=" + overallScore +
                ", skillScore=" + skillScore +
                ", skillTypeScore=" + skillTypeScore +
                ", proficiencyScore=" + proficiencyScore +
                ", experienceScore=" + experienceScore +
                ", profileScore=" + profileScore +
                ", availabilityScore=" + availabilityScore +
                ", locationScore=" + locationScore +
                ", verificationScore=" + verificationScore +
                ", ratingScore=" + ratingScore +
                ", matchingSkillCount=" + matchingSkillCount +
                ", algorithm=" + algorithm +
                '}';
    }
}