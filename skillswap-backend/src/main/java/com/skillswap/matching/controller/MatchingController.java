package com.skillswap.matching.controller;

import com.skillswap.matching.dto.MatchResponse;
import com.skillswap.matching.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matching")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping("/generate")
    public ResponseEntity<List<MatchResponse>> generateMatches(
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(
                matchingService.generateMatches(
                        userEmail,
                        limit
                )
        );
    }

    @GetMapping("/preview")
    public ResponseEntity<List<MatchResponse>> previewMatches(
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(
                matchingService.previewMatches(
                        userEmail,
                        limit
                )
        );
    }

    @GetMapping("/{matchId:\\d+}")
    public ResponseEntity<MatchResponse> getMatch(
            @PathVariable Long matchId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                matchingService.getMatch(
                        userEmail,
                        matchId
                )
        );
    }

    @PatchMapping("/{matchId:\\d+}/viewed")
    public ResponseEntity<MatchResponse> markViewed(
            @PathVariable Long matchId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                matchingService.markViewed(
                        userEmail,
                        matchId
                )
        );
    }

    @PatchMapping("/{matchId:\\d+}/interested")
    public ResponseEntity<MatchResponse> markInterested(
            @PathVariable Long matchId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                matchingService.markInterested(
                        userEmail,
                        matchId
                )
        );
    }

    @PatchMapping("/{matchId:\\d+}/dismiss")
    public ResponseEntity<MatchResponse> dismissMatch(
            @PathVariable Long matchId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                matchingService.dismissMatch(
                        userEmail,
                        matchId
                )
        );
    }
}