package com.skillswap.matching.controller;

import com.skillswap.matching.dto.MatchPreferenceRequest;
import com.skillswap.matching.dto.MatchPreferenceResponse;
import com.skillswap.matching.service.MatchPreferenceService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/matching/preferences")
public class MatchPreferenceController {

    private final MatchPreferenceService matchPreferenceService;

    public MatchPreferenceController(
            MatchPreferenceService matchPreferenceService
    ) {
        this.matchPreferenceService = matchPreferenceService;
    }

    @GetMapping
    public ResponseEntity<MatchPreferenceResponse> getOrCreate(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                matchPreferenceService.getOrCreate(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/existing")
    public ResponseEntity<MatchPreferenceResponse> get(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                matchPreferenceService.get(
                        authentication.getName()
                )
        );
    }

    @PostMapping
    public ResponseEntity<MatchPreferenceResponse> createOrUpdate(
            Authentication authentication,
            @Valid @RequestBody MatchPreferenceRequest request
    ) {
        return ResponseEntity.ok(
                matchPreferenceService.createOrUpdate(
                        authentication.getName(),
                        request
                )
        );
    }

    @PatchMapping
    public ResponseEntity<MatchPreferenceResponse> update(
            Authentication authentication,
            @Valid @RequestBody MatchPreferenceRequest request
    ) {
        return ResponseEntity.ok(
                matchPreferenceService.update(
                        authentication.getName(),
                        request
                )
        );
    }

    @PatchMapping("/activate")
    public ResponseEntity<MatchPreferenceResponse> activate(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                matchPreferenceService.activate(
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<MatchPreferenceResponse> deactivate(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                matchPreferenceService.deactivate(
                        authentication.getName()
                )
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            Authentication authentication
    ) {
        matchPreferenceService.delete(
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}