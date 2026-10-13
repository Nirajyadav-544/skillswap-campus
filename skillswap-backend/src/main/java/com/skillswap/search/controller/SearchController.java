package com.skillswap.search.controller;

import com.skillswap.search.dto.SearchRequest;
import com.skillswap.search.dto.SearchResponse;
import com.skillswap.search.service.SearchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(
            SearchService searchService
    ) {
        this.searchService =
                searchService;
    }

    @PostMapping
    public ResponseEntity<SearchResponse> search(
            @Valid @RequestBody SearchRequest request
    ) {
        return ResponseEntity.ok(
                searchService.search(
                        request
                )
        );
    }

    @GetMapping
    public ResponseEntity<SearchResponse> searchGet(
            @RequestParam String query,
            @RequestParam(
                    required = false
            ) String searchType,
            @RequestParam(
                    defaultValue = "0"
            ) int page,
            @RequestParam(
                    defaultValue = "20"
            ) int size
    ) {
        SearchRequest request =
                new SearchRequest();

        request.setQuery(query);
        request.setSearchType(searchType);
        request.setPage(page);
        request.setSize(size);

        return ResponseEntity.ok(
                searchService.search(
                        request
                )
        );
    }
}