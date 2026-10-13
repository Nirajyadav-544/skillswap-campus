package com.skillswap.resume.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class ResumeVersionPageResponse {

    private List<ResumeVersionResponse> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    private boolean first;

    private boolean last;

    private boolean empty;

    public ResumeVersionPageResponse() {
    }

    public static ResumeVersionPageResponse from(
            Page<ResumeVersionResponse> page
    ) {

        ResumeVersionPageResponse response =
                new ResumeVersionPageResponse();

        response.content =
                page.getContent();

        response.page =
                page.getNumber();

        response.size =
                page.getSize();

        response.totalElements =
                page.getTotalElements();

        response.totalPages =
                page.getTotalPages();

        response.first =
                page.isFirst();

        response.last =
                page.isLast();

        response.empty =
                page.isEmpty();

        return response;
    }

    public List<ResumeVersionResponse> getContent() {
        return content;
    }

    public void setContent(
            List<ResumeVersionResponse> content
    ) {
        this.content = content;
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

    public void setTotalElements(long totalElements) {
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
}