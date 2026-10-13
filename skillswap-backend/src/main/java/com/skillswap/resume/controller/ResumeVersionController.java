package com.skillswap.resume.controller;

import com.skillswap.resume.dto.CreateResumeVersionRequest;
import com.skillswap.resume.dto.ResumeVersionPageResponse;
import com.skillswap.resume.dto.ResumeVersionResponse;
import com.skillswap.resume.service.ResumeVersionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resumes/{resumeId}/versions")
public class ResumeVersionController {

    private final ResumeVersionService resumeVersionService;

    public ResumeVersionController(
            ResumeVersionService resumeVersionService
    ) {
        this.resumeVersionService = resumeVersionService;
    }

    @PostMapping
    public ResponseEntity<ResumeVersionResponse> createVersion(
            @PathVariable Long resumeId,
            @Valid @RequestBody CreateResumeVersionRequest request,
            Authentication authentication
    ) {

        ResumeVersionResponse response =
                resumeVersionService.createVersion(
                        resumeId,
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ResumeVersionResponse>> getVersions(
            @PathVariable Long resumeId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                resumeVersionService.getVersions(
                        resumeId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/page")
    public ResponseEntity<ResumeVersionPageResponse> getVersionsPage(
            @PathVariable Long resumeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                resumeVersionService.getVersionsPage(
                        resumeId,
                        authentication.getName(),
                        page,
                        size
                )
        );
    }

    @GetMapping("/{versionId}")
    public ResponseEntity<ResumeVersionResponse> getVersion(
            @PathVariable Long resumeId,
            @PathVariable Long versionId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                resumeVersionService.getVersion(
                        resumeId,
                        versionId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/number/{versionNumber}")
    public ResponseEntity<ResumeVersionResponse> getVersionByNumber(
            @PathVariable Long resumeId,
            @PathVariable Integer versionNumber,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                resumeVersionService.getVersionByNumber(
                        resumeId,
                        versionNumber,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countVersions(
            @PathVariable Long resumeId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                resumeVersionService.countVersions(
                        resumeId,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{versionId}")
    public ResponseEntity<Void> deleteVersion(
            @PathVariable Long resumeId,
            @PathVariable Long versionId,
            Authentication authentication
    ) {

        resumeVersionService.deleteVersion(
                resumeId,
                versionId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}