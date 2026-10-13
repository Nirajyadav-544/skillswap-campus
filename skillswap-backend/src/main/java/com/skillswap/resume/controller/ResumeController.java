package com.skillswap.resume.controller;

import com.skillswap.resume.dto.CreateResumeRequest;
import com.skillswap.resume.dto.ResumePageResponse;
import com.skillswap.resume.dto.ResumeResponse;
import com.skillswap.resume.dto.ResumeSummaryResponse;
import com.skillswap.resume.dto.UpdateResumeRequest;
import com.skillswap.resume.entity.ResumeStatus;
import com.skillswap.resume.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(
            ResumeService resumeService
    ) {
        this.resumeService = resumeService;
    }

    @PostMapping
    public ResponseEntity<ResumeResponse> create(
            @Valid @RequestBody CreateResumeRequest request,
            @RequestParam String userEmail
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        resumeService.create(
                                request,
                                userEmail
                        )
                );
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeResponse> getById(
            @PathVariable Long resumeId,
            @RequestParam String userEmail
    ) {

        return ResponseEntity.ok(
                resumeService.getById(
                        resumeId,
                        userEmail
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<ResumeSummaryResponse>> getMyResumes(
            @RequestParam String userEmail
    ) {

        return ResponseEntity.ok(
                resumeService.getMyResumes(
                        userEmail
                )
        );
    }

    @GetMapping("/page")
    public ResponseEntity<ResumePageResponse> getMyResumesPage(
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        return ResponseEntity.ok(
                resumeService.getMyResumesPage(
                        userEmail,
                        page,
                        size
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ResumeSummaryResponse>> getByStatus(
            @PathVariable ResumeStatus status,
            @RequestParam String userEmail
    ) {

        return ResponseEntity.ok(
                resumeService.getMyResumesByStatus(
                        status,
                        userEmail
                )
        );
    }

    @GetMapping("/primary")
    public ResponseEntity<ResumeResponse> getPrimary(
            @RequestParam String userEmail
    ) {

        return ResponseEntity.ok(
                resumeService.getPrimaryResume(
                        userEmail
                )
        );
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @RequestParam String userEmail
    ) {

        return ResponseEntity.ok(
                resumeService.countMyResumes(
                        userEmail
                )
        );
    }

    @PatchMapping("/{resumeId}")
    public ResponseEntity<ResumeResponse> update(
            @PathVariable Long resumeId,
            @Valid @RequestBody UpdateResumeRequest request,
            @RequestParam String userEmail
    ) {

        return ResponseEntity.ok(
                resumeService.update(
                        resumeId,
                        request,
                        userEmail
                )
        );
    }

    @PatchMapping("/{resumeId}/activate")
    public ResponseEntity<ResumeResponse> activate(
            @PathVariable Long resumeId,
            @RequestParam String userEmail
    ) {

        return ResponseEntity.ok(
                resumeService.activate(
                        resumeId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{resumeId}/archive")
    public ResponseEntity<ResumeResponse> archive(
            @PathVariable Long resumeId,
            @RequestParam String userEmail
    ) {

        return ResponseEntity.ok(
                resumeService.archive(
                        resumeId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{resumeId}/restore")
    public ResponseEntity<ResumeResponse> restore(
            @PathVariable Long resumeId,
            @RequestParam String userEmail
    ) {

        return ResponseEntity.ok(
                resumeService.restore(
                        resumeId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{resumeId}/primary")
    public ResponseEntity<ResumeResponse> makePrimary(
            @PathVariable Long resumeId,
            @RequestParam String userEmail
    ) {

        return ResponseEntity.ok(
                resumeService.makePrimary(
                        resumeId,
                        userEmail
                )
        );
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long resumeId,
            @RequestParam String userEmail
    ) {

        resumeService.delete(
                resumeId,
                userEmail
        );

        return ResponseEntity.noContent().build();
    }
}