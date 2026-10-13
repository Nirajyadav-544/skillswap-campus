package com.skillswap.search.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SearchResponse {

    private boolean success;

    private String message;

    private String query;

    private List<SearchResultResponse> results =
            new ArrayList<>();

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    private boolean first;

    private boolean last;

    private boolean empty;

    private LocalDateTime createdAt;

    public SearchResponse() {
    }

    public static SearchResponse success(
            String query,
            List<SearchResultResponse> results,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
        SearchResponse response =
                new SearchResponse();

        response.success = true;
        response.message = "Search completed successfully";
        response.query = query;
        response.results =
                results == null
                        ? new ArrayList<>()
                        : results;
        response.page = page;
        response.size = size;
        response.totalElements = totalElements;
        response.totalPages = totalPages;
        response.first = page == 0;
        response.last =
                totalPages == 0
                        || page >= totalPages - 1;
        response.empty =
                response.results.isEmpty();
        response.createdAt =
                LocalDateTime.now();

        return response;
    }

    public static SearchResponse failure(
            String message
    ) {
        SearchResponse response =
                new SearchResponse();

        response.success = false;
        response.message = message;
        response.results =
                new ArrayList<>();
        response.createdAt =
                LocalDateTime.now();

        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<SearchResultResponse> getResults() {
        return results;
    }

    public void setResults(
            List<SearchResultResponse> results
    ) {
        this.results = results;
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

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(
            long totalElements
    ) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }
}