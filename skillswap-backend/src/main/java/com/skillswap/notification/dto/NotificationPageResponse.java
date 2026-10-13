package com.skillswap.notification.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class NotificationPageResponse {

    private List<NotificationSummaryResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;

    public NotificationPageResponse() {
    }

    public static NotificationPageResponse from(
            Page<NotificationSummaryResponse> pageResult
    ) {
        NotificationPageResponse response = new NotificationPageResponse();

        response.content = pageResult.getContent();
        response.page = pageResult.getNumber();
        response.size = pageResult.getSize();
        response.totalElements = pageResult.getTotalElements();
        response.totalPages = pageResult.getTotalPages();
        response.first = pageResult.isFirst();
        response.last = pageResult.isLast();
        response.empty = pageResult.isEmpty();

        return response;
    }

    public List<NotificationSummaryResponse> getContent() {
        return content;
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