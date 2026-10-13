package com.skillswap.rating.controller;

import com.skillswap.rating.dto.ReviewRequest;
import com.skillswap.rating.dto.ReviewResponse;
import com.skillswap.rating.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        reviewService.create(
                                request,
                                authentication.getName()
                        )
                );
    }

    @GetMapping("/{reviewId:\\d+}")
    public ResponseEntity<ReviewResponse> getById(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                reviewService.getById(
                        reviewId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/user/{userId:\\d+}")
    public ResponseEntity<List<ReviewResponse>> getForUser(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                reviewService.getForUser(
                        userId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                reviewService.getWrittenByCurrentUser(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/session/{sessionId:\\d+}")
    public ResponseEntity<List<ReviewResponse>> getForSession(
            @PathVariable Long sessionId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                reviewService.getForSession(
                        sessionId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{reviewId:\\d+}")
    public ResponseEntity<ReviewResponse> update(
            @PathVariable Long reviewId,
            @RequestParam
            @NotBlank(message = "Content is required")
            @Size(
                    max = 2000,
                    message = "Content must not exceed 2000 characters"
            )
            String content,
            @RequestParam(required = false) Boolean anonymous,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                reviewService.update(
                        reviewId,
                        content,
                        anonymous,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{reviewId:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        reviewService.delete(
                reviewId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{reviewId:\\d+}/hide")
    public ResponseEntity<Void> hide(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        reviewService.hide(
                reviewId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{reviewId:\\d+}/show")
    public ResponseEntity<Void> show(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        reviewService.show(
                reviewId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId:\\d+}/count")
    public ResponseEntity<Long> countForUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                reviewService.countForUser(userId)
        );
    }

    @GetMapping("/user/{userId:\\d+}/written-count")
    public ResponseEntity<Long> countWrittenByUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                reviewService.countWrittenByUser(userId)
        );
    }
}