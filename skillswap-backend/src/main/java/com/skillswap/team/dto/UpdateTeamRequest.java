package com.skillswap.team.dto;

import com.skillswap.team.entity.TeamStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class UpdateTeamRequest {

    @Size(
            max = 200,
            message = "Team name cannot exceed 200 characters"
    )
    private String name;

    @Size(
            max = 2000,
            message = "Description cannot exceed 2000 characters"
    )
    private String description;

    @Size(
            max = 200,
            message = "Project title cannot exceed 200 characters"
    )
    private String projectTitle;

    @Size(
            max = 3000,
            message = "Project description cannot exceed 3000 characters"
    )
    private String projectDescription;

    @Size(
            max = 200,
            message = "Target skill cannot exceed 200 characters"
    )
    private String targetSkill;

    @Min(
            value = 2,
            message = "Team must allow at least 2 members"
    )
    @Max(
            value = 100,
            message = "Team cannot allow more than 100 members"
    )
    private Integer maxMembers;

    private TeamStatus status;

    public UpdateTeamRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getTargetSkill() {
        return targetSkill;
    }

    public void setTargetSkill(String targetSkill) {
        this.targetSkill = targetSkill;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public TeamStatus getStatus() {
        return status;
    }

    public void setStatus(TeamStatus status) {
        this.status = status;
    }
}