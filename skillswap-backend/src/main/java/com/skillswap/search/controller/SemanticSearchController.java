package com.skillswap.search.controller;

import com.skillswap.search.dto.SemanticSearchRequest;
import com.skillswap.search.dto.SemanticSearchResponse;
import com.skillswap.search.service.SemanticSearchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search/semantic")
public class SemanticSearchController {

    private final SemanticSearchService semanticSearchService;

    public SemanticSearchController(
            SemanticSearchService semanticSearchService
    ) {
        this.semanticSearchService =
                semanticSearchService;
    }

    @PostMapping
    public ResponseEntity<SemanticSearchResponse> search(
            @Valid @RequestBody SemanticSearchRequest request
    ) {
        return ResponseEntity.ok(
                semanticSearchService.search(
                        request
                )
        );
    }

    @GetMapping
    public ResponseEntity<SemanticSearchResponse> searchGet(
            @RequestParam String query,
            @RequestParam(
                    required = false
            ) String searchType,
            @RequestParam(
                    defaultValue = "0"
            ) int page,
            @RequestParam(
                    defaultValue = "20"
            ) int size,
            @RequestParam(
                    defaultValue = "0.0"
            ) Double minimumScore
    ) {
        SemanticSearchRequest request =
                new SemanticSearchRequest();

        request.setQuery(query);
        request.setSearchType(searchType);
        request.setPage(page);
        request.setSize(size);
        request.setMinimumScore(minimumScore);

        return ResponseEntity.ok(
                semanticSearchService.search(
                        request
                )
        );
    }
}