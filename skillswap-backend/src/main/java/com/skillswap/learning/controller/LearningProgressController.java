package com.skillswap.learning.controller;

import com.skillswap.learning.dto.LearningPageResponse;
import com.skillswap.learning.dto.LearningProgressResponse;
import com.skillswap.learning.dto.UpdateLearningProgressRequest;
import com.skillswap.learning.entity.LearningProgressStatus;
import com.skillswap.learning.service.LearningProgressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learning/progress")
public class LearningProgressController {

    private final LearningProgressService learningProgressService;

    public LearningProgressController(
            LearningProgressService learningProgressService
    ) {
        this.learningProgressService = learningProgressService;
    }

    @PostMapping("/{resourceId}/start")
    public ResponseEntity<LearningProgressResponse> start(
            @PathVariable Long resourceId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningProgressService.start(
                        resourceId,
                        userEmail
                )
        );
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<LearningProgressResponse> getByResource(
            @PathVariable Long resourceId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningProgressService.getByResource(
                        resourceId,
                        userEmail
                )
        );
    }

    @GetMapping("/path/{pathId}/resource/{resourceId}")
    public ResponseEntity<LearningProgressResponse> getByPathAndResource(
            @PathVariable Long pathId,
            @PathVariable Long resourceId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningProgressService.getByPathAndResource(
                        pathId,
                        resourceId,
                        userEmail
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<LearningProgressResponse>> getMyProgress(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningProgressService.getMyProgress(userEmail)
        );
    }

    @GetMapping("/page")
    public ResponseEntity<
            LearningPageResponse<LearningProgressResponse>
            > getMyProgressPage(
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                learningProgressService.getMyProgress(
                        userEmail,
                        page,
                        size
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LearningProgressResponse>> getByStatus(
            @PathVariable LearningProgressStatus status,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningProgressService.getMyProgressByStatus(
                        userEmail,
                        status
                )
        );
    }

    @GetMapping("/status/{status}/page")
    public ResponseEntity<
            LearningPageResponse<LearningProgressResponse>
            > getByStatusPage(
            @PathVariable LearningProgressStatus status,
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                learningProgressService.getMyProgressByStatus(
                        userEmail,
                        status,
                        page,
                        size
                )
        );
    }

    @GetMapping("/path/{pathId}")
    public ResponseEntity<List<LearningProgressResponse>> getPathProgress(
            @PathVariable Long pathId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningProgressService.getPathProgress(
                        pathId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{resourceId}")
    public ResponseEntity<LearningProgressResponse> update(
            @PathVariable Long resourceId,
            @Valid @RequestBody UpdateLearningProgressRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningProgressService.update(
                        resourceId,
                        request,
                        userEmail
                )
        );
    }

    @PatchMapping("/{resourceId}/complete")
    public ResponseEntity<LearningProgressResponse> complete(
            @PathVariable Long resourceId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningProgressService.complete(
                        resourceId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{resourceId}/reset")
    public ResponseEntity<LearningProgressResponse> reset(
            @PathVariable Long resourceId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningProgressService.reset(
                        resourceId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{resourceId}/skip")
    public ResponseEntity<LearningProgressResponse> skip(
            @PathVariable Long resourceId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningProgressService.skip(
                        resourceId,
                        userEmail
                )
        );
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningProgressService.countMyProgress(
                        userEmail
                )
        );
    }

    @GetMapping("/count/{status}")
    public ResponseEntity<Long> countByStatus(
            @PathVariable LearningProgressStatus status,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningProgressService.countMyProgressByStatus(
                        userEmail,
                        status
                )
        );
    }
}