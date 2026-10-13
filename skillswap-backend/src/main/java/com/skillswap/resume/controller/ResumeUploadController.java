package com.skillswap.resume.controller;

import com.skillswap.resume.dto.ResumeUploadResponse;
import com.skillswap.resume.service.ResumeUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeUploadController {

    private final ResumeUploadService resumeUploadService;

    public ResumeUploadController(
            ResumeUploadService resumeUploadService
    ) {
        this.resumeUploadService = resumeUploadService;
    }

    @PostMapping(
            value = "/{resumeId}/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ResumeUploadResponse> upload(
            @PathVariable Long resumeId,
            @RequestPart("file") MultipartFile file,
            Authentication authentication
    ) {

        ResumeUploadResponse response =
                resumeUploadService.upload(
                        resumeId,
                        authentication.getName(),
                        file
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}