package com.skillswap.learning.dto;

import java.util.List;

public record LearningPageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty
) {

    public static <T> LearningPageResponse<T> of(
            List<T> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
        List<T> safeContent = content == null ? List.of() : content;

        return new LearningPageResponse<>(
                safeContent,
                page,
                size,
                totalElements,
                totalPages,
                page == 0,
                page >= Math.max(totalPages - 1, 0),
                safeContent.isEmpty()
        );
    }

    public int numberOfElements() {
        return content == null ? 0 : content.size();
    }

    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }
}