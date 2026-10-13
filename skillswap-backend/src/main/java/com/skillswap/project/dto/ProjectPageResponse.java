package com.skillswap.project.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class ProjectPageResponse {

    private List<ProjectSummaryResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;

    public ProjectPageResponse() {
    }

    public static ProjectPageResponse from(Page<ProjectSummaryResponse> page) {
        ProjectPageResponse response = new ProjectPageResponse();

        response.content = page.getContent();
        response.page = page.getNumber();
        response.size = page.getSize();
        response.totalElements = page.getTotalElements();
        response.totalPages = page.getTotalPages();
        response.first = page.isFirst();
        response.last = page.isLast();
        response.empty = page.isEmpty();

        return response;
    }

    public List<ProjectSummaryResponse> getContent() {
        return content;
    }

    public void setContent(List<ProjectSummaryResponse> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isEmpty() {
        return empty;
    }
}