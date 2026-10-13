package com.skillswap.matching.controller;

import com.skillswap.matching.dto.MatchFeedbackRequest;
import com.skillswap.matching.dto.MatchFeedbackResponse;
import com.skillswap.matching.service.MatchFeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches/feedback")
public class MatchFeedbackController {

    private final MatchFeedbackService matchFeedbackService;

    public MatchFeedbackController(
            MatchFeedbackService matchFeedbackService
    ) {
        this.matchFeedbackService = matchFeedbackService;
    }

    /**
     * Create feedback for a match.
     */
    @PostMapping
    public ResponseEntity<MatchFeedbackResponse> createFeedback(
            Authentication authentication,
            @Valid @RequestBody MatchFeedbackRequest request
    ) {
        MatchFeedbackResponse response =
                matchFeedbackService.createFeedback(
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Get one feedback record.
     */
    @GetMapping("/{feedbackId}")
    public ResponseEntity<MatchFeedbackResponse> getFeedback(
            Authentication authentication,
            @PathVariable Long feedbackId
    ) {
        return ResponseEntity.ok(
                matchFeedbackService.getFeedback(
                        authentication.getName(),
                        feedbackId
                )
        );
    }

    /**
     * Get all feedback created by current user.
     */
    @GetMapping("/me")
    public ResponseEntity<List<MatchFeedbackResponse>> getMyFeedback(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                matchFeedbackService.getMyFeedback(
                        authentication.getName()
                )
        );
    }

    /**
     * Get feedback for one match.
     */
    @GetMapping("/match/{matchResultId}")
    public ResponseEntity<List<MatchFeedbackResponse>> getFeedbackForMatch(
            Authentication authentication,
            @PathVariable Long matchResultId
    ) {
        return ResponseEntity.ok(
                matchFeedbackService.getFeedbackForMatch(
                        authentication.getName(),
                        matchResultId
                )
        );
    }

    /**
     * Mark feedback as processed.
     */
    @PatchMapping("/{feedbackId}/processed")
    public ResponseEntity<MatchFeedbackResponse> markProcessed(
            Authentication authentication,
            @PathVariable Long feedbackId
    ) {
        return ResponseEntity.ok(
                matchFeedbackService.markProcessed(
                        authentication.getName(),
                        feedbackId
                )
        );
    }

    /**
     * Delete feedback.
     */
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(
            Authentication authentication,
            @PathVariable Long feedbackId
    ) {
        matchFeedbackService.deleteFeedback(
                authentication.getName(),
                feedbackId
        );

        return ResponseEntity.noContent().build();
    }
}