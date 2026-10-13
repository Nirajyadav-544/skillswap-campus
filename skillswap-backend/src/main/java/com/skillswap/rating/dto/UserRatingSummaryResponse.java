package com.skillswap.rating.dto;

import java.util.Map;

public record UserRatingSummaryResponse(
        Long userId,
        long totalRatings,
        double averageScore,
        double teachingAverage,
        double learningAverage,
        double communicationAverage,
        double knowledgeAverage,
        double professionalismAverage,
        double punctualityAverage,
        double collaborationAverage,
        long fiveStarCount,
        long fourStarCount,
        long threeStarCount,
        long twoStarCount,
        long oneStarCount,
        long verifiedRatingCount,
        long commentCount
) {

    public UserRatingSummaryResponse(
            Long userId,
            long totalRatings,
            double averageScore,
            double teachingAverage,
            double learningAverage,
            double communicationAverage,
            double knowledgeAverage,
            double professionalismAverage,
            double punctualityAverage,
            double collaborationAverage,
            long fiveStarCount,
            long fourStarCount,
            long threeStarCount,
            long twoStarCount,
            long oneStarCount,
            long verifiedRatingCount,
            long commentCount
    ) {
        this.userId = userId;
        this.totalRatings = Math.max(0, totalRatings);
        this.averageScore = normalizeAverage(averageScore);
        this.teachingAverage = normalizeAverage(teachingAverage);
        this.learningAverage = normalizeAverage(learningAverage);
        this.communicationAverage = normalizeAverage(communicationAverage);
        this.knowledgeAverage = normalizeAverage(knowledgeAverage);
        this.professionalismAverage = normalizeAverage(professionalismAverage);
        this.punctualityAverage = normalizeAverage(punctualityAverage);
        this.collaborationAverage = normalizeAverage(collaborationAverage);
        this.fiveStarCount = Math.max(0, fiveStarCount);
        this.fourStarCount = Math.max(0, fourStarCount);
        this.threeStarCount = Math.max(0, threeStarCount);
        this.twoStarCount = Math.max(0, twoStarCount);
        this.oneStarCount = Math.max(0, oneStarCount);
        this.verifiedRatingCount = Math.max(0, verifiedRatingCount);
        this.commentCount = Math.max(0, commentCount);
    }

    public double getAverageScorePercentage() {
        if (averageScore <= 0) {
            return 0.0;
        }

        return Math.round((averageScore / 5.0) * 10000.0) / 100.0;
    }

    public String getAverageScoreLabel() {

        if (totalRatings == 0) {
            return "No ratings";
        }

        if (averageScore >= 4.5) {
            return "Excellent";
        }

        if (averageScore >= 4.0) {
            return "Very Good";
        }

        if (averageScore >= 3.0) {
            return "Good";
        }

        if (averageScore >= 2.0) {
            return "Needs Improvement";
        }

        return "Poor";
    }

    public long getFiveStarCount() {
        return fiveStarCount;
    }

    public long getFourStarCount() {
        return fourStarCount;
    }

    public long getThreeStarCount() {
        return threeStarCount;
    }

    public long getTwoStarCount() {
        return twoStarCount;
    }

    public long getOneStarCount() {
        return oneStarCount;
    }

    public long getVerifiedRatingCount() {
        return verifiedRatingCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public double getAverageForCategory(String category) {

        if (category == null) {
            return averageScore;
        }

        return switch (category.trim().toUpperCase()) {
            case "TEACHING" -> teachingAverage;
            case "LEARNING" -> learningAverage;
            case "COMMUNICATION" -> communicationAverage;
            case "KNOWLEDGE" -> knowledgeAverage;
            case "PROFESSIONALISM" -> professionalismAverage;
            case "PUNCTUALITY" -> punctualityAverage;
            case "COLLABORATION" -> collaborationAverage;
            case "OVERALL" -> averageScore;
            default -> averageScore;
        };
    }

    public Map<String, Double> getCategoryAverages() {
        return Map.of(
                "OVERALL", averageScore,
                "TEACHING", teachingAverage,
                "LEARNING", learningAverage,
                "COMMUNICATION", communicationAverage,
                "KNOWLEDGE", knowledgeAverage,
                "PROFESSIONALISM", professionalismAverage,
                "PUNCTUALITY", punctualityAverage,
                "COLLABORATION", collaborationAverage
        );
    }

    public boolean hasRatings() {
        return totalRatings > 0;
    }

    public boolean hasComments() {
        return commentCount > 0;
    }

    public boolean hasVerifiedRatings() {
        return verifiedRatingCount > 0;
    }

    private static double normalizeAverage(double value) {

        if (Double.isNaN(value)
                || Double.isInfinite(value)
                || value < 0) {
            return 0.0;
        }

        if (value > 5.0) {
            return 5.0;
        }

        return Math.round(value * 100.0) / 100.0;
    }
}