package com.skillswap.career.dto;

import java.util.ArrayList;
import java.util.List;

public class CareerAnalysisResponse {

    private Long userId;
    private String targetRole;
    private Integer totalGoals;
    private Integer activeGoals;
    private Integer completedGoals;
    private Integer totalRoadmaps;
    private Integer totalRoadmapItems;
    private Integer completedRoadmapItems;
    private Double roadmapProgressPercentage;
    private List<String> skillGaps;
    private List<String> recommendations;

    public CareerAnalysisResponse() {
        this.skillGaps = new ArrayList<>();
        this.recommendations = new ArrayList<>();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public Integer getTotalGoals() {
        return totalGoals;
    }

    public void setTotalGoals(Integer totalGoals) {
        this.totalGoals = totalGoals;
    }

    public Integer getActiveGoals() {
        return activeGoals;
    }

    public void setActiveGoals(Integer activeGoals) {
        this.activeGoals = activeGoals;
    }

    public Integer getCompletedGoals() {
        return completedGoals;
    }

    public void setCompletedGoals(Integer completedGoals) {
        this.completedGoals = completedGoals;
    }

    public Integer getTotalRoadmaps() {
        return totalRoadmaps;
    }

    public void setTotalRoadmaps(Integer totalRoadmaps) {
        this.totalRoadmaps = totalRoadmaps;
    }

    public Integer getTotalRoadmapItems() {
        return totalRoadmapItems;
    }

    public void setTotalRoadmapItems(Integer totalRoadmapItems) {
        this.totalRoadmapItems = totalRoadmapItems;
    }

    public Integer getCompletedRoadmapItems() {
        return completedRoadmapItems;
    }

    public void setCompletedRoadmapItems(Integer completedRoadmapItems) {
        this.completedRoadmapItems = completedRoadmapItems;
    }

    public Double getRoadmapProgressPercentage() {
        return roadmapProgressPercentage;
    }

    public void setRoadmapProgressPercentage(Double roadmapProgressPercentage) {
        this.roadmapProgressPercentage = roadmapProgressPercentage;
    }

    public List<String> getSkillGaps() {
        return skillGaps;
    }

    public void setSkillGaps(List<String> skillGaps) {
        this.skillGaps = skillGaps;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}