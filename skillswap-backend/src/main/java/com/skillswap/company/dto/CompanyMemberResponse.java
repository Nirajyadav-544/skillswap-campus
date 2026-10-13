package com.skillswap.company.dto;

import com.skillswap.company.entity.CompanyMember;

import java.time.LocalDateTime;

public class CompanyMemberResponse {

    private Long id;
    private Long companyId;
    private Long userId;
    private String role;
    private Boolean active;
    private LocalDateTime joinedAt;

    public CompanyMemberResponse() {
    }

    public static CompanyMemberResponse from(
            CompanyMember member
    ) {

        if (member == null) {
            return null;
        }

        CompanyMemberResponse response =
                new CompanyMemberResponse();

        response.setId(member.getId());
        response.setRole(member.getRole());
        response.setActive(member.getActive());
        response.setJoinedAt(member.getJoinedAt());

        if (member.getCompany() != null) {
            response.setCompanyId(
                    member.getCompany().getId()
            );
        }

        if (member.getUser() != null) {
            response.setUserId(
                    member.getUser().getId()
            );
        }

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}