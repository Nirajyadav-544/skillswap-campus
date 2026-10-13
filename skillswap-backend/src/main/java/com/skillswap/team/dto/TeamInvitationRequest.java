package com.skillswap.team.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TeamInvitationRequest {

    @NotNull(message = "Invitee user ID is required")
    private Long inviteeUserId;

    @Size(
            max = 1000,
            message = "Invitation message cannot exceed 1000 characters"
    )
    private String message;

    public TeamInvitationRequest() {
    }

    public Long getInviteeUserId() {
        return inviteeUserId;
    }

    public void setInviteeUserId(Long inviteeUserId) {
        this.inviteeUserId = inviteeUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}