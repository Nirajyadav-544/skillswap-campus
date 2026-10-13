package com.skillswap.team.dto;

import jakarta.validation.constraints.NotNull;

public class AddTeamMemberRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    public AddTeamMemberRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}