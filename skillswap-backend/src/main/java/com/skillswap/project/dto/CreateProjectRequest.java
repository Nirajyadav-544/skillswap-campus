package com.skillswap.project.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreateProjectRequest {

    @NotBlank(message = "Project title is required")
    @Size(max = 200, message = "Project title must not exceed 200 characters")
    private String title;

    @Size(max = 3000, message = "Description must not exceed 3000 characters")
    private String description;

    @Size(max = 150, message = "Skill name must not exceed 150 characters")
    private String skillName;

    @Size(max = 150, message = "Category must not exceed 150 characters")
    private String category;

    @Max(value = 100, message = "Maximum members cannot exceed 100")
    private Integer maxMembers;

    private LocalDate startDate;

    private LocalDate targetEndDate;

    @Size(max = 1000, message = "Requirements must not exceed 1000 characters")
    private String requirements;

    @Size(max = 2000, message = "Goals must not exceed 2000 characters")
    private String goals;

    @Size(max = 500, message = "GitHub URL must not exceed 500 characters")
    private String githubUrl;

    @Size(max = 500, message = "Demo URL must not exceed 500 characters")
    private String demoUrl;

    @Size(max = 500, message = "Project URL must not exceed 500 characters")
    private String projectUrl;

    public CreateProjectRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getTargetEndDate() {
        return targetEndDate;
    }

    public void setTargetEndDate(LocalDate targetEndDate) {
        this.targetEndDate = targetEndDate;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getDemoUrl() {
        return demoUrl;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public void normalize() {
        if (title != null) {
            title = title.trim();
        }

        if (description != null) {
            description = description.trim();
        }

        if (skillName != null) {
            skillName = skillName.trim();
        }

        if (category != null) {
            category = category.trim();
        }

        if (requirements != null) {
            requirements = requirements.trim();
        }

        if (goals != null) {
            goals = goals.trim();
        }

        if (githubUrl != null) {
            githubUrl = githubUrl.trim();
        }

        if (demoUrl != null) {
            demoUrl = demoUrl.trim();
        }

        if (projectUrl != null) {
            projectUrl = projectUrl.trim();
        }
    }
}