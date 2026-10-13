package com.skillswap.company.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddCompanyMemberRequest {

    @NotNull
    private Long userId;

    @Size(max = 100)
    private String role;

    public AddCompanyMemberRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}