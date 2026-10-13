package com.skillswap.career.dto;

import java.util.ArrayList;
import java.util.List;

public class CareerRecommendationResponse {

    private Long userId;
    private String targetRole;
    private List<String> recommendations;

    public CareerRecommendationResponse() {
        this.recommendations = new ArrayList<>();
    }

    public CareerRecommendationResponse(
            Long userId,
            String targetRole,
            List<String> recommendations
    ) {
        this.userId = userId;
        this.targetRole = targetRole;
        this.recommendations = recommendations;
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

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}