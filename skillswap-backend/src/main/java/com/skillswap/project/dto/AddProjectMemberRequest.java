package com.skillswap.project.dto;

import com.skillswap.project.entity.ProjectMemberRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddProjectMemberRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private ProjectMemberRole role;

    @Size(max = 1000, message = "Contribution must not exceed 1000 characters")
    private String contribution;

    public AddProjectMemberRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ProjectMemberRole getRole() {
        return role;
    }

    public void setRole(ProjectMemberRole role) {
        this.role = role;
    }

    public String getContribution() {
        return contribution;
    }

    public void setContribution(String contribution) {
        this.contribution = contribution;
    }

    public void normalize() {
        if (contribution != null) {
            contribution = contribution.trim();
        }
    }
}