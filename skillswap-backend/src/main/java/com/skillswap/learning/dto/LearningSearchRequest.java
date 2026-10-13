package com.skillswap.learning.dto;

import com.skillswap.learning.entity.LearningPathStatus;
import com.skillswap.learning.entity.LearningProgressStatus;
import com.skillswap.learning.entity.LearningResourceType;

public class LearningSearchRequest {

    private String keyword;
    private String skillName;
    private LearningPathStatus pathStatus;
    private LearningResourceType resourceType;
    private LearningProgressStatus progressStatus;
    private Long userId;
    private Boolean activeOnly;
    private Boolean completedOnly;
    private Integer page = 0;
    private Integer size = 20;

    public LearningSearchRequest() {
    }

    public void normalize() {
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isBlank()) {
                keyword = null;
            }
        }

        if (skillName != null) {
            skillName = skillName.trim();
            if (skillName.isBlank()) {
                skillName = null;
            }
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

    public boolean hasKeyword() {
        return keyword != null && !keyword.isBlank();
    }

    public boolean hasSkillName() {
        return skillName != null && !skillName.isBlank();
    }

    public boolean hasPathStatus() {
        return pathStatus != null;
    }

    public boolean hasResourceType() {
        return resourceType != null;
    }

    public boolean hasProgressStatus() {
        return progressStatus != null;
    }

    public boolean hasUserId() {
        return userId != null;
    }

    public boolean isActiveOnly() {
        return Boolean.TRUE.equals(activeOnly);
    }

    public boolean isCompletedOnly() {
        return Boolean.TRUE.equals(completedOnly);
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public LearningPathStatus getPathStatus() {
        return pathStatus;
    }

    public void setPathStatus(LearningPathStatus pathStatus) {
        this.pathStatus = pathStatus;
    }

    public LearningResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(LearningResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public LearningProgressStatus getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(LearningProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getActiveOnly() {
        return activeOnly;
    }

    public void setActiveOnly(Boolean activeOnly) {
        this.activeOnly = activeOnly;
    }

    public Boolean getCompletedOnly() {
        return completedOnly;
    }

    public void setCompletedOnly(Boolean completedOnly) {
        this.completedOnly = completedOnly;
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