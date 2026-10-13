package com.skillswap.resume.controller;

import com.skillswap.resume.dto.ResumeExportResponse;
import com.skillswap.resume.service.ResumeExportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeExportController {

    private final ResumeExportService resumeExportService;

    public ResumeExportController(
            ResumeExportService resumeExportService
    ) {
        this.resumeExportService = resumeExportService;
    }

    @GetMapping("/{resumeId}/export")
    public ResponseEntity<ResumeExportResponse> exportLatestVersion(
            @PathVariable Long resumeId,
            Authentication authentication
    ) {

        ResumeExportResponse response =
                resumeExportService.exportLatestVersion(
                        resumeId,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{resumeId}/versions/{versionId}/export")
    public ResponseEntity<ResumeExportResponse> exportVersion(
            @PathVariable Long resumeId,
            @PathVariable Long versionId,
            Authentication authentication
    ) {

        ResumeExportResponse response =
                resumeExportService.exportVersion(
                        resumeId,
                        versionId,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}