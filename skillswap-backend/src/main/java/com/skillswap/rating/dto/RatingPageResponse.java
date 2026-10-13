package com.skillswap.rating.dto;

import com.skillswap.rating.entity.Rating;
import org.springframework.data.domain.Page;

import java.util.List;

public record RatingPageResponse(
        List<RatingSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty
) {

    public static RatingPageResponse from(Page<Rating> page) {

        if (page == null) {
            return new RatingPageResponse(
                    List.of(),
                    0,
                    0,
                    0,
                    0,
                    true,
                    true,
                    true
            );
        }

        List<RatingSummaryResponse> content = page
                .getContent()
                .stream()
                .map(RatingSummaryResponse::from)
                .toList();

        return new RatingPageResponse(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }

    public int getNumberOfElements() {
        return content != null
                ? content.size()
                : 0;
    }

    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }

    public boolean hasNextPage() {
        return !last;
    }

    public boolean hasPreviousPage() {
        return !first;
    }
}