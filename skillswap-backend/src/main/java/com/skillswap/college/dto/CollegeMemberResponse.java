package com.skillswap.college.dto;

import com.skillswap.college.entity.CollegeMember;

import java.time.LocalDateTime;

public class CollegeMemberResponse {

    private Long id;
    private Long collegeId;
    private Long userId;
    private String role;
    private Boolean active;
    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;

    public CollegeMemberResponse() {
    }

    public static CollegeMemberResponse from(CollegeMember member) {
        CollegeMemberResponse response = new CollegeMemberResponse();

        response.setId(member.getId());

        response.setCollegeId(
                member.getCollege() != null
                        ? member.getCollege().getId()
                        : null
        );

        response.setUserId(
                member.getUser() != null
                        ? member.getUser().getId()
                        : null
        );

        response.setRole(member.getRole());
        response.setActive(member.getActive());
        response.setJoinedAt(member.getJoinedAt());
        response.setUpdatedAt(member.getUpdatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}