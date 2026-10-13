package com.skillswap.project.dto;

import com.skillswap.project.entity.ProjectMember;
import com.skillswap.project.entity.ProjectMemberRole;
import com.skillswap.project.entity.ProjectMemberStatus;

import java.time.LocalDateTime;

public class ProjectMemberResponse {

    private Long id;
    private Long projectId;

    private Long userId;
    private String userEmail;
    private String userName;

    private ProjectMemberRole role;
    private ProjectMemberStatus status;

    private String contribution;

    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProjectMemberResponse() {
    }

    public static ProjectMemberResponse from(ProjectMember member) {
        ProjectMemberResponse response = new ProjectMemberResponse();

        response.id = member.getId();

        if (member.getProject() != null) {
            response.projectId = member.getProject().getId();
        }

        if (member.getUser() != null) {
            response.userId = member.getUser().getId();
            response.userEmail = member.getUser().getEmail();

            String firstName = member.getUser().getFirstName();
            String lastName = member.getUser().getLastName();

            StringBuilder userName = new StringBuilder();

            if (firstName != null && !firstName.isBlank()) {
                userName.append(firstName.trim());
            }

            if (lastName != null && !lastName.isBlank()) {
                if (!userName.isEmpty()) {
                    userName.append(" ");
                }

                userName.append(lastName.trim());
            }

            response.userName = userName.toString();
        }

        response.role = member.getRole();
        response.status = member.getStatus();
        response.contribution = member.getContribution();

        response.joinedAt = member.getJoinedAt();
        response.leftAt = member.getLeftAt();

        response.createdAt = member.getCreatedAt();
        response.updatedAt = member.getUpdatedAt();

        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public ProjectMemberRole getRole() {
        return role;
    }

    public ProjectMemberStatus getStatus() {
        return status;
    }

    public String getContribution() {
        return contribution;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}