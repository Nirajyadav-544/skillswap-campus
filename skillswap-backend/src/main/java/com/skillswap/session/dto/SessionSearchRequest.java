package com.skillswap.session.dto;

import com.skillswap.session.entity.SessionStatus;
import com.skillswap.session.entity.SessionType;

import java.time.LocalDateTime;

public class SessionSearchRequest {

    private SessionStatus status;

    private SessionType sessionType;

    private Long exchangeRequestId;

    private Long teacherUserId;

    private Long learnerUserId;

    private String skillName;

    private String keyword;

    private LocalDateTime startFrom;

    private LocalDateTime startTo;

    private LocalDateTime endFrom;

    private LocalDateTime endTo;

    private Boolean upcoming;

    private Boolean online;

    private Boolean completed;

    private Boolean cancelled;

    private int page = 0;

    private int size = 20;

    private String sortBy = "scheduledStart";

    private String sortDirection = "ASC";

    public SessionSearchRequest() {
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public Long getExchangeRequestId() {
        return exchangeRequestId;
    }

    public void setExchangeRequestId(Long exchangeRequestId) {
        this.exchangeRequestId = exchangeRequestId;
    }

    public Long getTeacherUserId() {
        return teacherUserId;
    }

    public void setTeacherUserId(Long teacherUserId) {
        this.teacherUserId = teacherUserId;
    }

    public Long getLearnerUserId() {
        return learnerUserId;
    }

    public void setLearnerUserId(Long learnerUserId) {
        this.learnerUserId = learnerUserId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public LocalDateTime getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(LocalDateTime startFrom) {
        this.startFrom = startFrom;
    }

    public LocalDateTime getStartTo() {
        return startTo;
    }

    public void setStartTo(LocalDateTime startTo) {
        this.startTo = startTo;
    }

    public LocalDateTime getEndFrom() {
        return endFrom;
    }

    public void setEndFrom(LocalDateTime endFrom) {
        this.endFrom = endFrom;
    }

    public LocalDateTime getEndTo() {
        return endTo;
    }

    public void setEndTo(LocalDateTime endTo) {
        this.endTo = endTo;
    }

    public Boolean getUpcoming() {
        return upcoming;
    }

    public void setUpcoming(Boolean upcoming) {
        this.upcoming = upcoming;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public void normalize() {

        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }

        if (skillName != null) {
            skillName = skillName.trim();

            if (skillName.isBlank()) {
                skillName = null;
            }
        }

        if (keyword != null) {
            keyword = keyword.trim();

            if (keyword.isBlank()) {
                keyword = null;
            }
        }

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "scheduledStart";
        } else {
            sortBy = sortBy.trim();
        }

        if (sortDirection == null
                || sortDirection.isBlank()) {
            sortDirection = "ASC";
        } else {
            sortDirection =
                    sortDirection.trim().toUpperCase();
        }

        if (!sortDirection.equals("ASC")
                && !sortDirection.equals("DESC")) {
            sortDirection = "ASC";
        }
    }

    public boolean hasStatusFilter() {
        return status != null;
    }

    public boolean hasSessionTypeFilter() {
        return sessionType != null;
    }

    public boolean hasExchangeRequestFilter() {
        return exchangeRequestId != null;
    }

    public boolean hasTeacherFilter() {
        return teacherUserId != null;
    }

    public boolean hasLearnerFilter() {
        return learnerUserId != null;
    }

    public boolean hasSkillFilter() {
        return skillName != null
                && !skillName.isBlank();
    }

    public boolean hasKeywordFilter() {
        return keyword != null
                && !keyword.isBlank();
    }

    public boolean hasStartDateFilter() {
        return startFrom != null
                || startTo != null;
    }

    public boolean hasEndDateFilter() {
        return endFrom != null
                || endTo != null;
    }

    public boolean isUpcomingOnly() {
        return Boolean.TRUE.equals(upcoming);
    }

    public boolean isOnlineOnly() {
        return Boolean.TRUE.equals(online);
    }

    public boolean isCompletedOnly() {
        return Boolean.TRUE.equals(completed);
    }

    public boolean isCancelledOnly() {
        return Boolean.TRUE.equals(cancelled);
    }

    public boolean isDescending() {
        return "DESC".equalsIgnoreCase(sortDirection);
    }

    public boolean isAscending() {
        return !isDescending();
    }
}