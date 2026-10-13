package com.skillswap.search.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.search.dto.SearchRequest;
import com.skillswap.search.dto.SearchResponse;
import com.skillswap.search.dto.SearchResultResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class SearchService {

    public SearchResponse search(
            SearchRequest request
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Search request is required"
            );
        }

        request.normalize();

        if (request.getQuery() == null
                || request.getQuery().isBlank()) {
            throw new BadRequestException(
                    "Search query is required"
            );
        }

        /*
         * Phase 11 initial implementation.
         *
         * This service provides the search contract and
         * safe local matching behavior. Database-wide
         * semantic indexing will be added through the
         * semantic search service.
         */
        List<SearchResultResponse> results =
                new ArrayList<>();

        String query =
                request.getQuery()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        /*
         * No entities are searched yet because the current
         * Search module has no search-specific repository.
         * Returning an empty result is intentional rather
         * than inventing repository methods against the
         * existing modules.
         */

        int totalPages =
                results.isEmpty()
                        ? 0
                        : (int) Math.ceil(
                                results.size()
                                        / (double) request.getSize()
                        );

        return SearchResponse.success(
                query,
                results,
                request.getPage(),
                request.getSize(),
                results.size(),
                totalPages
        );
    }
}