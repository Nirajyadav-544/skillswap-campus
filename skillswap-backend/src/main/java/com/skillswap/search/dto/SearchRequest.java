package com.skillswap.search.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SearchRequest {

    @NotBlank(message = "Search query is required")
    @Size(max = 500, message = "Search query cannot exceed 500 characters")
    private String query;

    @Size(max = 100, message = "Search type cannot exceed 100 characters")
    private String searchType;

    @Min(value = 0, message = "Page cannot be negative")
    private int page = 0;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size cannot exceed 100")
    private int size = 20;

    public SearchRequest() {
    }

    public void normalize() {
        if (query != null) {
            query = query.trim();
        }

        if (searchType != null) {
            searchType = searchType.trim();

            if (searchType.isBlank()) {
                searchType = null;
            }
        }

        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
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
}