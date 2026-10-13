package com.skillswap.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CareerAnalysisRequest {

    @NotBlank(message = "Career goal is required")
    @Size(
            max = 300,
            message = "Career goal must not exceed 300 characters"
    )
    private String careerGoal;

    @Size(
            max = 10000,
            message = "Skills must not exceed 10000 characters"
    )
    private String skills;

    @Size(
            max = 10000,
            message = "Projects must not exceed 10000 characters"
    )
    private String projects;

    @Size(
            max = 10000,
            message = "Experience must not exceed 10000 characters"
    )
    private String experience;

    @Size(
            max = 10000,
            message = "Education must not exceed 10000 characters"
    )
    private String education;

    private Long userId;

    public CareerAnalysisRequest() {
    }

    public CareerAnalysisRequest(
            String careerGoal,
            String skills
    ) {
        this.careerGoal = careerGoal;
        this.skills = skills;
    }

    public CareerAnalysisRequest(
            String careerGoal,
            String skills,
            String projects,
            String experience,
            String education,
            Long userId
    ) {
        this.careerGoal = careerGoal;
        this.skills = skills;
        this.projects = projects;
        this.experience = experience;
        this.education = education;
        this.userId = userId;
    }

    public String getCareerGoal() {
        return careerGoal;
    }

    public void setCareerGoal(String careerGoal) {
        this.careerGoal = careerGoal;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getProjects() {
        return projects;
    }

    public void setProjects(String projects) {
        this.projects = projects;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void normalize() {
        if (careerGoal != null) {
            careerGoal = careerGoal.trim();
        }

        if (skills != null) {
            skills = skills.trim();
        }

        if (projects != null) {
            projects = projects.trim();
        }

        if (experience != null) {
            experience = experience.trim();
        }

        if (education != null) {
            education = education.trim();
        }
    }
}