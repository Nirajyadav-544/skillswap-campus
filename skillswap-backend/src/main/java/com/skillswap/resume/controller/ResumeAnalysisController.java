package com.skillswap.resume.controller;

import com.skillswap.resume.dto.ResumeAnalysisRequest;
import com.skillswap.resume.dto.ResumeAnalysisResponse;
import com.skillswap.resume.service.ResumeAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resumes/analysis")
public class ResumeAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeAnalysisController(
            ResumeAnalysisService resumeAnalysisService
    ) {
        this.resumeAnalysisService =
                resumeAnalysisService;
    }

    @PostMapping
    public ResponseEntity<ResumeAnalysisResponse> analyze(
            @Valid @RequestBody ResumeAnalysisRequest request,
            @RequestParam String userEmail
    ) {

        return ResponseEntity.ok(
                resumeAnalysisService.analyze(
                        request,
                        userEmail
                )
        );
    }
}