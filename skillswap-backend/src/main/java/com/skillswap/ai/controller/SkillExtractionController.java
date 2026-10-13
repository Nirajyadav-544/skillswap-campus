package com.skillswap.ai.controller;

import com.skillswap.ai.dto.SkillExtractionRequest;
import com.skillswap.ai.dto.SkillExtractionResponse;
import com.skillswap.ai.service.SkillExtractionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai/skills")
public class SkillExtractionController {

    private final SkillExtractionService skillExtractionService;

    public SkillExtractionController(
            SkillExtractionService skillExtractionService
    ) {
        this.skillExtractionService =
                skillExtractionService;
    }

    @PostMapping("/extract")
    public ResponseEntity<SkillExtractionResponse> extractSkills(
            @Valid @RequestBody SkillExtractionRequest request
    ) {
        return ResponseEntity.ok(
                skillExtractionService.extractSkills(
                        request
                )
        );
    }
}