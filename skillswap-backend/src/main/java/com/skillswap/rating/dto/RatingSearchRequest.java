package com.skillswap.rating.dto;

import com.skillswap.rating.entity.RatingCategory;
import com.skillswap.rating.entity.RatingStatus;
import lombok.Data;

@Data
public class RatingSearchRequest {

    private RatingCategory category;

    private RatingStatus status;

    private Long sessionId;

    private Long raterUserId;

    private Long ratedUserId;

    private Integer minimumScore;

    private Integer maximumScore;

    private Boolean verified;

    private Boolean anonymous;

    private Boolean withComments;

    private String keyword;

    private int page = 0;

    private int size = 20;

    private String sortBy = "createdAt";

    private String sortDirection = "desc";

    public RatingSearchRequest() {
    }

    public void normalize() {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "createdAt";
        } else {
            sortBy = sortBy.trim();
        }

        if (sortDirection == null
                || sortDirection.isBlank()) {
            sortDirection = "desc";
        } else {
            sortDirection =
                    sortDirection.trim().toLowerCase();
        }

        if (!"asc".equals(sortDirection)
                && !"desc".equals(sortDirection)) {
            sortDirection = "desc";
        }

        if (keyword != null) {
            keyword = keyword.trim();

            if (keyword.isBlank()) {
                keyword = null;
            }
        }

        if (minimumScore != null) {
            minimumScore =
                    Math.max(1, Math.min(5, minimumScore));
        }

        if (maximumScore != null) {
            maximumScore =
                    Math.max(1, Math.min(5, maximumScore));
        }

        if (minimumScore != null
                && maximumScore != null
                && minimumScore > maximumScore) {

            int temp = minimumScore;
            minimumScore = maximumScore;
            maximumScore = temp;
        }
    }

    public boolean hasScoreFilter() {
        return minimumScore != null
                || maximumScore != null;
    }

    public boolean hasUserFilter() {
        return raterUserId != null
                || ratedUserId != null;
    }

    public boolean hasSessionFilter() {
        return sessionId != null;
    }

    public boolean hasCategoryFilter() {
        return category != null;
    }

    public boolean hasKeyword() {
        return keyword != null
                && !keyword.isBlank();
    }

    public boolean hasVerificationFilter() {
        return verified != null;
    }

    public boolean hasAnonymousFilter() {
        return anonymous != null;
    }

    public boolean hasCommentFilter() {
        return Boolean.TRUE.equals(withComments);
    }

    public RatingCategory getCategory() {
        return category;
    }

    public void setCategory(RatingCategory category) {
        this.category = category;
    }

    public RatingStatus getStatus() {
        return status;
    }

    public void setStatus(RatingStatus status) {
        this.status = status;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getRaterUserId() {
        return raterUserId;
    }

    public void setRaterUserId(Long raterUserId) {
        this.raterUserId = raterUserId;
    }

    public Long getRatedUserId() {
        return ratedUserId;
    }

    public void setRatedUserId(Long ratedUserId) {
        this.ratedUserId = ratedUserId;
    }

    public Integer getMinimumScore() {
        return minimumScore;
    }

    public void setMinimumScore(Integer minimumScore) {
        this.minimumScore = minimumScore;
    }

    public Integer getMaximumScore() {
        return maximumScore;
    }

    public void setMaximumScore(Integer maximumScore) {
        this.maximumScore = maximumScore;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Boolean getWithComments() {
        return withComments;
    }

    public void setWithComments(Boolean withComments) {
        this.withComments = withComments;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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
}