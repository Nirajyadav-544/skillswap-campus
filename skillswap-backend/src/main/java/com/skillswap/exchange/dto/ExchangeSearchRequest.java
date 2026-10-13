package com.skillswap.exchange.dto;

import com.skillswap.exchange.entity.ExchangeStatus;
import com.skillswap.exchange.entity.ExchangeType;

public class ExchangeSearchRequest {

    private ExchangeStatus status;

    private ExchangeType exchangeType;

    private String skillName;

    private Boolean incoming;

    private Boolean outgoing;

    private Integer page = 0;

    private Integer size = 20;

    private String sortBy = "createdAt";

    private String sortDirection = "DESC";

    public ExchangeSearchRequest() {
    }

    public ExchangeStatus getStatus() {
        return status;
    }

    public void setStatus(ExchangeStatus status) {
        this.status = status;
    }

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(ExchangeType exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Boolean getIncoming() {
        return incoming;
    }

    public void setIncoming(Boolean incoming) {
        this.incoming = incoming;
    }

    public Boolean getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(Boolean outgoing) {
        this.outgoing = outgoing;
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
        if (skillName != null) {
            skillName = skillName.trim();

            if (skillName.isEmpty()) {
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

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "createdAt";
        } else {
            sortBy = sortBy.trim();
        }

        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "DESC";
        } else {
            sortDirection = sortDirection.trim().toUpperCase();
        }

        if (!sortDirection.equals("ASC")
                && !sortDirection.equals("DESC")) {
            sortDirection = "DESC";
        }
    }

    public boolean hasStatusFilter() {
        return status != null;
    }

    public boolean hasExchangeTypeFilter() {
        return exchangeType != null;
    }

    public boolean hasSkillFilter() {
        return skillName != null
                && !skillName.isBlank();
    }

    public boolean isIncomingOnly() {
        return Boolean.TRUE.equals(incoming)
                && !Boolean.TRUE.equals(outgoing);
    }

    public boolean isOutgoingOnly() {
        return Boolean.TRUE.equals(outgoing)
                && !Boolean.TRUE.equals(incoming);
    }

    public boolean isAllDirections() {
        return !Boolean.TRUE.equals(incoming)
                && !Boolean.TRUE.equals(outgoing);
    }
}