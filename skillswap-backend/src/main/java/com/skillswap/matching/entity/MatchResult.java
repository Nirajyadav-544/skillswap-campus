package com.skillswap.matching.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents one calculated match between two users.
 *
 * requesterUser:
 *     The user for whom the recommendation/match was calculated.
 *
 * candidateUser:
 *     The user who was identified as a potential match.
 *
 * The same pair can be evaluated repeatedly as profiles and skills
 * change. The generatedAt / updatedAt timestamps allow the system
 * to know when the current calculation was produced.
 */
@Entity
@Table(
        name = "match_results",
        indexes = {
                @Index(
                        name = "idx_match_result_requester",
                        columnList = "requester_user_id"
                ),
                @Index(
                        name = "idx_match_result_candidate",
                        columnList = "candidate_user_id"
                ),
                @Index(
                        name = "idx_match_result_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_match_result_score",
                        columnList = "overall_score"
                ),
                @Index(
                        name = "idx_match_result_algorithm",
                        columnList = "algorithm"
                ),
                @Index(
                        name = "idx_match_result_generated",
                        columnList = "generated_at"
                )
        }
)
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User requesting or receiving the recommendation.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "requester_user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_match_result_requester")
    )
    private User requesterUser;

    /**
     * User identified as a potential match.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "candidate_user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_match_result_candidate")
    )
    private User candidateUser;

    /**
     * Current lifecycle status of this match.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MatchStatus status = MatchStatus.CALCULATED;

    /**
     * Algorithm that generated this result.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MatchAlgorithm algorithm = MatchAlgorithm.RULE_BASED;

    /**
     * Overall calculated score, from 0 to 100.
     */
    @Column(name = "overall_score", nullable = false)
    private double overallScore = 0.0;

    /**
     * Human-readable explanation of why the users were matched.
     */
    @Column(name = "match_reason", length = 2000)
    private String matchReason;

    /**
     * Number of compatible skills discovered.
     */
    @Column(name = "matching_skill_count", nullable = false)
    private int matchingSkillCount = 0;

    /**
     * Whether this result has already been shown to the requester.
     */
    @Column(name = "viewed", nullable = false)
    private boolean viewed = false;

    /**
     * Number of times the result has been presented/opened.
     */
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    /**
     * Timestamp when the match was generated.
     */
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    /**
     * Timestamp when the match was last viewed.
     */
    @Column(name = "last_viewed_at")
    private LocalDateTime lastViewedAt;

    /**
     * Timestamp when the requester expressed interest.
     */
    @Column(name = "interested_at")
    private LocalDateTime interestedAt;

    /**
     * Timestamp when the requester dismissed the result.
     */
    @Column(name = "dismissed_at")
    private LocalDateTime dismissedAt;

    /**
     * Optional expiration timestamp.
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * Timestamp when the result was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public MatchResult() {
    }

    public MatchResult(
            User requesterUser,
            User candidateUser,
            double overallScore,
            MatchAlgorithm algorithm
    ) {
        this.requesterUser = requesterUser;
        this.candidateUser = candidateUser;
        this.overallScore = normalizeScore(overallScore);
        this.algorithm =
                algorithm != null ? algorithm : MatchAlgorithm.RULE_BASED;
        this.status = MatchStatus.CALCULATED;
        this.generatedAt = LocalDateTime.now();
        this.updatedAt = this.generatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (generatedAt == null) {
            generatedAt = now;
        }

        updatedAt = now;

        if (status == null) {
            status = MatchStatus.CALCULATED;
        }

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

        if (matchingSkillCount < 0) {
            matchingSkillCount = 0;
        }

        if (viewCount < 0) {
            viewCount = 0;
        }

        if (matchReason != null) {
            matchReason = matchReason.trim();

            if (matchReason.isBlank()) {
                matchReason = null;
            }
        }
    }

    private double normalizeScore(double score) {
        if (Double.isNaN(score) || Double.isInfinite(score)) {
            return 0.0;
        }

        return Math.max(0.0, Math.min(100.0, score));
    }

    public Long getId() {
        return id;
    }

    public User getRequesterUser() {
        return requesterUser;
    }

    public User getCandidateUser() {
        return candidateUser;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public MatchAlgorithm getAlgorithm() {
        return algorithm;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public int getMatchingSkillCount() {
        return matchingSkillCount;
    }

    public boolean isViewed() {
        return viewed;
    }

    public int getViewCount() {
        return viewCount;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public LocalDateTime getLastViewedAt() {
        return lastViewedAt;
    }

    public LocalDateTime getInterestedAt() {
        return interestedAt;
    }

    public LocalDateTime getDismissedAt() {
        return dismissedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRequesterUser(User requesterUser) {
        this.requesterUser = requesterUser;
    }

    public void setCandidateUser(User candidateUser) {
        this.candidateUser = candidateUser;
    }

    public void setStatus(MatchStatus status) {
        this.status =
                status != null ? status : MatchStatus.CALCULATED;
    }

    public void setAlgorithm(MatchAlgorithm algorithm) {
        this.algorithm =
                algorithm != null ? algorithm : MatchAlgorithm.RULE_BASED;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = normalizeScore(overallScore);
    }

    public void setMatchReason(String matchReason) {
        if (matchReason == null || matchReason.isBlank()) {
            this.matchReason = null;
        } else {
            this.matchReason = matchReason.trim();
        }
    }

    public void setMatchingSkillCount(int matchingSkillCount) {
        this.matchingSkillCount = Math.max(0, matchingSkillCount);
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;

        if (viewed) {
            markViewed();
        }
    }

    public void setViewCount(int viewCount) {
        this.viewCount = Math.max(0, viewCount);
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public void setLastViewedAt(LocalDateTime lastViewedAt) {
        this.lastViewedAt = lastViewedAt;
    }

    public void setInterestedAt(LocalDateTime interestedAt) {
        this.interestedAt = interestedAt;
    }

    public void setDismissedAt(LocalDateTime dismissedAt) {
        this.dismissedAt = dismissedAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Marks this result as viewed.
     */
    public void markViewed() {
        this.viewed = true;
        this.viewCount++;
        this.lastViewedAt = LocalDateTime.now();
        this.status = MatchStatus.VIEWED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks the match as interesting to the requester.
     */
    public void markInterested() {
        this.status = MatchStatus.INTERESTED;
        this.interestedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Dismisses the match.
     */
    public void markDismissed() {
        this.status = MatchStatus.DISMISSED;
        this.dismissedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Expires the match.
     */
    public void markExpired() {
        this.status = MatchStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Archives the match.
     */
    public void archive() {
        this.status = MatchStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Adds a compatible skill to the result.
     */
    public void incrementMatchingSkillCount() {
        this.matchingSkillCount++;
    }

    /**
     * Sets the number of matching skills safely.
     */
    public void setMatchingSkills(int count) {
        this.matchingSkillCount = Math.max(0, count);
    }

    /**
     * Returns whether the match can currently be shown.
     */
    public boolean isDisplayable() {

        if (status == MatchStatus.DISMISSED
                || status == MatchStatus.EXPIRED
                || status == MatchStatus.ARCHIVED) {
            return false;
        }

        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    /**
     * Returns whether the match is expired by timestamp.
     */
    public boolean isExpired() {

        if (status == MatchStatus.EXPIRED) {
            return true;
        }

        return expiresAt != null
                && expiresAt.isBefore(LocalDateTime.now());
    }

    /**
     * Returns whether this result represents a valid pair.
     */
    public boolean isValidPair() {
        return requesterUser != null
                && candidateUser != null
                && requesterUser.getId() != null
                && candidateUser.getId() != null
                && !requesterUser.getId().equals(candidateUser.getId());
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "id=" + id +
                ", requesterUserId=" +
                (requesterUser != null ? requesterUser.getId() : null) +
                ", candidateUserId=" +
                (candidateUser != null ? candidateUser.getId() : null) +
                ", status=" + status +
                ", algorithm=" + algorithm +
                ", overallScore=" + overallScore +
                ", matchingSkillCount=" + matchingSkillCount +
                ", viewed=" + viewed +
                ", generatedAt=" + generatedAt +
                '}';
    }
}