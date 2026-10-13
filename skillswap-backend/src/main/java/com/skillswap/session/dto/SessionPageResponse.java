package com.skillswap.session.dto;

import com.skillswap.session.entity.LearningSession;
import org.springframework.data.domain.Page;

import java.util.List;

public record SessionPageResponse(

        List<SessionResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages,

        boolean first,

        boolean last,

        boolean empty
) {

    public static SessionPageResponse from(
            Page<LearningSession> page
    ) {

        if (page == null) {
            return new SessionPageResponse(
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

        List<SessionResponse> content =
                page.getContent()
                        .stream()
                        .map(SessionResponse::from)
                        .toList();

        return new SessionPageResponse(
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
        return content != null
                && !content.isEmpty();
    }

    public boolean hasNextPage() {
        return !last;
    }

    public boolean hasPreviousPage() {
        return !first;
    }
}