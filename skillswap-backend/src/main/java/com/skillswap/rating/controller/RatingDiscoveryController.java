package com.skillswap.rating.controller;

import com.skillswap.rating.dto.RatingPageResponse;
import com.skillswap.rating.dto.RatingResponse;
import com.skillswap.rating.dto.RatingSearchRequest;
import com.skillswap.rating.dto.UserRatingSummaryResponse;
import com.skillswap.rating.entity.RatingCategory;
import com.skillswap.rating.service.RatingDiscoveryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings/discovery")
public class RatingDiscoveryController {

    private final RatingDiscoveryService ratingDiscoveryService;

    public RatingDiscoveryController(
            RatingDiscoveryService ratingDiscoveryService
    ) {
        this.ratingDiscoveryService = ratingDiscoveryService;
    }

    @GetMapping("/search")
    public ResponseEntity<RatingPageResponse> search(
            @Valid RatingSearchRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                ratingDiscoveryService.search(
                        request,
                        userEmail
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<RatingPageResponse> getUserRatings(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        return ResponseEntity.ok(
                ratingDiscoveryService.getUserRatings(
                        userId,
                        page,
                        size,
                        sortBy,
                        sortDirection
                )
        );
    }

    @GetMapping("/user/{userId}/given")
    public ResponseEntity<RatingPageResponse> getRatingsGivenByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                ratingDiscoveryService.getRatingsGivenByUser(
                        userId,
                        page,
                        size
                )
        );
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<RatingPageResponse> getSessionRatings(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                ratingDiscoveryService.getSessionRatings(
                        sessionId,
                        page,
                        size
                )
        );
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<RatingResponse>> getRecentRatingsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                ratingDiscoveryService.getRecentRatingsForUser(
                        userId,
                        limit
                )
        );
    }

    @GetMapping("/user/{userId}/top")
    public ResponseEntity<List<RatingResponse>> getTopRatingsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                ratingDiscoveryService.getTopRatingsForUser(
                        userId,
                        limit
                )
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<RatingResponse>> getRatingsByCategory(
            @PathVariable RatingCategory category,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(
                ratingDiscoveryService.getRatingsByCategory(
                        category,
                        limit
                )
        );
    }

    @GetMapping("/user/{userId}/score")
    public ResponseEntity<RatingPageResponse> getUserRatingsByScore(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer minimumScore,
            @RequestParam(required = false) Integer maximumScore,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                ratingDiscoveryService.getUserRatingsByScore(
                        userId,
                        minimumScore,
                        maximumScore,
                        page,
                        size
                )
        );
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<UserRatingSummaryResponse> getUserRatingSummary(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                ratingDiscoveryService.getUserRatingSummary(
                        userId
                )
        );
    }

    @GetMapping("/user/{userId}/verified")
    public ResponseEntity<List<RatingResponse>> getVerifiedRatingsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(
                ratingDiscoveryService.getVerifiedRatingsForUser(
                        userId,
                        limit
                )
        );
    }

    @GetMapping("/user/{userId}/comments")
    public ResponseEntity<List<RatingResponse>> getRatingsWithComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(
                ratingDiscoveryService.getRatingsWithComments(
                        userId,
                        limit
                )
        );
    }
}