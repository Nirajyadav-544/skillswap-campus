package com.skillswap.rating.controller;

import com.skillswap.rating.dto.CreateRatingRequest;
import com.skillswap.rating.dto.RatingResponse;
import com.skillswap.rating.dto.UpdateRatingRequest;
import com.skillswap.rating.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<RatingResponse> create(
            @Valid @RequestBody CreateRatingRequest request,
            Authentication authentication
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ratingService.create(
                                request,
                                authentication.getName()
                        )
                );
    }

    @GetMapping("/{ratingId:\\d+}")
    public ResponseEntity<RatingResponse> getById(
            @PathVariable Long ratingId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                ratingService.getById(
                        ratingId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/user/{userId:\\d+}")
    public ResponseEntity<List<RatingResponse>> getForUser(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                ratingService.getForUser(
                        userId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/mine")
    public ResponseEntity<List<RatingResponse>> getMyRatings(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                ratingService.getGivenByCurrentUser(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/session/{sessionId:\\d+}")
    public ResponseEntity<List<RatingResponse>> getForSession(
            @PathVariable Long sessionId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                ratingService.getForSession(
                        sessionId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        return ResponseEntity.ok(
                ratingService.search(
                        page,
                        size,
                        sortBy,
                        sortDirection
                )
        );
    }

    @PatchMapping("/{ratingId:\\d+}")
    public ResponseEntity<RatingResponse> update(
            @PathVariable Long ratingId,
            @Valid @RequestBody UpdateRatingRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                ratingService.update(
                        ratingId,
                        request,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{ratingId:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable Long ratingId,
            Authentication authentication
    ) {
        ratingService.delete(
                ratingId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{ratingId:\\d+}/hide")
    public ResponseEntity<Void> hide(
            @PathVariable Long ratingId,
            Authentication authentication
    ) {
        ratingService.hide(
                ratingId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{ratingId:\\d+}/activate")
    public ResponseEntity<Void> activate(
            @PathVariable Long ratingId,
            Authentication authentication
    ) {
        ratingService.activate(
                ratingId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId:\\d+}/count")
    public ResponseEntity<Long> countForUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                ratingService.countForUser(userId)
        );
    }

    @GetMapping("/user/{userId:\\d+}/average")
    public ResponseEntity<Double> averageForUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                ratingService.getAverageScore(userId)
        );
    }
}