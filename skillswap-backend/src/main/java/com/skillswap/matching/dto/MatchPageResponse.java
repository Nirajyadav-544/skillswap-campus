package com.skillswap.matching.dto;

import java.util.List;

public record MatchPageResponse(
        List<MatchSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty
) {

    public MatchPageResponse {
        content = content == null ? List.of() : List.copyOf(content);
        page = Math.max(0, page);
        size = Math.max(1, size);
        totalElements = Math.max(0L, totalElements);
        totalPages = Math.max(0, totalPages);
        empty = content.isEmpty();
    }

    public static MatchPageResponse of(
            List<MatchSummaryResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
        return new MatchPageResponse(
                content,
                page,
                size,
                totalElements,
                totalPages,
                page == 0,
                totalPages == 0 || page >= totalPages - 1,
                content == null || content.isEmpty()
        );
    }

    public boolean hasContent() {
        return !content.isEmpty();
    }

    public int numberOfElements() {
        return content.size();
    }
}