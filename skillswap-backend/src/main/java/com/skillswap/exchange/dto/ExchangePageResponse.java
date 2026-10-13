package com.skillswap.exchange.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class ExchangePageResponse {

    private List<ExchangeSummaryResponse> content;

    private int page;
    private int size;

    private long totalElements;
    private int totalPages;

    private boolean first;
    private boolean last;
    private boolean empty;

    public ExchangePageResponse() {
    }

    public static ExchangePageResponse from(
            Page<?> page
    ) {
        ExchangePageResponse response =
                new ExchangePageResponse();

        response.page = page.getNumber();
        response.size = page.getSize();

        response.totalElements = page.getTotalElements();
        response.totalPages = page.getTotalPages();

        response.first = page.isFirst();
        response.last = page.isLast();
        response.empty = page.isEmpty();

        response.content = page.getContent()
                .stream()
                .map(item -> {
                    if (item instanceof com.skillswap.exchange.entity.ExchangeRequest) {
                        return ExchangeSummaryResponse.from(
                                (com.skillswap.exchange.entity.ExchangeRequest) item
                        );
                    }

                    throw new IllegalArgumentException(
                            "Unsupported page content type: "
                                    + item.getClass().getName()
                    );
                })
                .toList();

        return response;
    }

    public List<ExchangeSummaryResponse> getContent() {
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