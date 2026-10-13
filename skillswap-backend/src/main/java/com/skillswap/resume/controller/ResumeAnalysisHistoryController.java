package com.skillswap.resume.controller;

import com.skillswap.resume.dto.ResumeAnalysisResponse;
import com.skillswap.resume.service.ResumeAnalysisHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resumes/{resumeId}/analyses")
public class ResumeAnalysisHistoryController {

    private final ResumeAnalysisHistoryService historyService;

    public ResumeAnalysisHistoryController(
            ResumeAnalysisHistoryService historyService
    ) {
        this.historyService = historyService;
    }

    @GetMapping
    public ResponseEntity<List<ResumeAnalysisResponse>> getAnalyses(
            @PathVariable Long resumeId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                historyService.getAnalyses(
                        resumeId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ResumeAnalysisResponse>> getAnalysesPage(
            @PathVariable Long resumeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                historyService.getAnalysesPage(
                        resumeId,
                        authentication.getName(),
                        page,
                        size
                )
        );
    }

    @GetMapping("/latest")
    public ResponseEntity<ResumeAnalysisResponse> getLatestAnalysis(
            @PathVariable Long resumeId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                historyService.getLatestAnalysis(
                        resumeId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countAnalyses(
            @PathVariable Long resumeId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                historyService.countAnalyses(
                        resumeId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<ResumeAnalysisResponse> getAnalysis(
            @PathVariable Long resumeId,
            @PathVariable Long analysisId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                historyService.getAnalysis(
                        resumeId,
                        analysisId,
                        authentication.getName()
                )
        );
    }
}