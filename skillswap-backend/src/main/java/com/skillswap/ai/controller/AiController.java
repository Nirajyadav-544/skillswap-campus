package com.skillswap.ai.controller;

import com.skillswap.ai.dto.AiRequest;
import com.skillswap.ai.dto.AiResponse;
import com.skillswap.ai.entity.AiInteraction;
import com.skillswap.ai.service.AiInteractionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiInteractionService aiInteractionService;

    public AiController(
            AiInteractionService aiInteractionService
    ) {
        this.aiInteractionService =
                aiInteractionService;
    }

    @PostMapping("/process")
    public ResponseEntity<AiResponse> process(
            @Valid @RequestBody AiRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                aiInteractionService.process(
                        request,
                        userEmail
                )
        );
    }

    @GetMapping("/interactions/{interactionId}")
    public ResponseEntity<AiInteraction> getById(
            @PathVariable Long interactionId,
            @RequestParam String userEmail
    ) {
        return aiInteractionService
                .getById(
                        interactionId,
                        userEmail
                )
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    @GetMapping("/interactions")
    public ResponseEntity<List<AiInteraction>> getMyInteractions(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                aiInteractionService
                        .getMyInteractions(
                                userEmail
                        )
        );
    }

    @GetMapping("/interactions/count")
    public ResponseEntity<Long> count(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                aiInteractionService.count(
                        userEmail
                )
        );
    }
}