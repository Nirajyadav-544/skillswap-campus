package com.skillswap.career.dto;

public class CareerReadinessResponse {

    private Long userId;
    private Integer readinessPercentage;
    private Integer goalsScore;
    private Integer roadmapScore;
    private Integer completionScore;
    private String readinessLevel;
    private String summary;

    public CareerReadinessResponse() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getReadinessPercentage() {
        return readinessPercentage;
    }

    public void setReadinessPercentage(Integer readinessPercentage) {
        this.readinessPercentage = readinessPercentage;
    }

    public Integer getGoalsScore() {
        return goalsScore;
    }

    public void setGoalsScore(Integer goalsScore) {
        this.goalsScore = goalsScore;
    }

    public Integer getRoadmapScore() {
        return roadmapScore;
    }

    public void setRoadmapScore(Integer roadmapScore) {
        this.roadmapScore = roadmapScore;
    }

    public Integer getCompletionScore() {
        return completionScore;
    }

    public void setCompletionScore(Integer completionScore) {
        this.completionScore = completionScore;
    }

    public String getReadinessLevel() {
        return readinessLevel;
    }

    public void setReadinessLevel(String readinessLevel) {
        this.readinessLevel = readinessLevel;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}