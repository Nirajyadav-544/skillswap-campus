package com.skillswap.ai.controller;

import com.skillswap.ai.dto.SkillGapRequest;
import com.skillswap.ai.dto.SkillGapResponse;
import com.skillswap.ai.service.SkillGapService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai/skill-gap")
public class SkillGapController {

    private final SkillGapService skillGapService;

    public SkillGapController(
            SkillGapService skillGapService
    ) {
        this.skillGapService =
                skillGapService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<SkillGapResponse> analyze(
            @Valid @RequestBody SkillGapRequest request
    ) {
        return ResponseEntity.ok(
                skillGapService.analyze(
                        request
                )
        );
    }
}