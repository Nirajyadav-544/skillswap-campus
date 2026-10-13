package com.skillswap.project.dto;

import com.skillswap.project.entity.ProjectStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class ProjectSearchRequest {

    @Size(max = 150, message = "Skill name must not exceed 150 characters")
    private String skillName;

    @Size(max = 150, message = "Category must not exceed 150 characters")
    private String category;

    private ProjectStatus status;

    @Size(max = 200, message = "Keyword must not exceed 200 characters")
    private String keyword;

    private Long ownerUserId;

    @Min(value = 0, message = "Page cannot be negative")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private Integer size = 20;

    public ProjectSearchRequest() {
    }

    public void normalize() {
        if (skillName != null) {
            skillName = skillName.trim();
        }

        if (category != null) {
            category = category.trim();
        }

        if (keyword != null) {
            keyword = keyword.trim();
        }

        if (page == null || page < 0) {
            page = 0;
        }

        if (size == null || size < 1) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }
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

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}