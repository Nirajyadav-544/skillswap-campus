package com.skillswap.search.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.search.dto.SearchResultResponse;
import com.skillswap.search.dto.SemanticSearchRequest;
import com.skillswap.search.dto.SemanticSearchResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class SemanticSearchService {

    public SemanticSearchResponse search(
            SemanticSearchRequest request
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Semantic search request is required"
            );
        }

        request.normalize();

        if (request.getQuery() == null
                || request.getQuery().isBlank()) {
            throw new BadRequestException(
                    "Semantic search query is required"
            );
        }

        String query =
                request.getQuery()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        /*
         * Initial Phase 11 implementation.
         *
         * Real embedding/vector search will be connected
         * later through the AI service. We intentionally
         * do not invent repositories or vector-database
         * APIs here.
         */
        List<SearchResultResponse> results =
                new ArrayList<>();

        return SemanticSearchResponse.success(
                query,
                results,
                request.getPage(),
                request.getSize(),
                results.size(),
                0
        );
    }
}