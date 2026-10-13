package com.skillswap.learning.controller;

import com.skillswap.learning.dto.CreateLearningPathRequest;
import com.skillswap.learning.dto.LearningPageResponse;
import com.skillswap.learning.dto.LearningPathResponse;
import com.skillswap.learning.dto.LearningPathSummaryResponse;
import com.skillswap.learning.dto.UpdateLearningPathRequest;
import com.skillswap.learning.entity.LearningPathStatus;
import com.skillswap.learning.service.LearningPathService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learning/paths")
public class LearningPathController {

    private final LearningPathService learningPathService;

    public LearningPathController(
            LearningPathService learningPathService
    ) {
        this.learningPathService = learningPathService;
    }

    @PostMapping
    public ResponseEntity<LearningPathResponse> create(
            @Valid @RequestBody CreateLearningPathRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        learningPathService.create(
                                request,
                                userEmail
                        )
                );
    }

    @GetMapping("/{pathId}")
    public ResponseEntity<LearningPathResponse> getById(
            @PathVariable Long pathId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningPathService.getById(
                        pathId,
                        userEmail
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<LearningPathSummaryResponse>> getMyPaths(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningPathService.getMyPaths(userEmail)
        );
    }

    @GetMapping("/page")
    public ResponseEntity<
            LearningPageResponse<LearningPathSummaryResponse>
            > getMyPathsPage(
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                learningPathService.getMyPaths(
                        userEmail,
                        page,
                        size
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LearningPathSummaryResponse>> getByStatus(
            @PathVariable LearningPathStatus status,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningPathService.getMyPathsByStatus(
                        userEmail,
                        status
                )
        );
    }

    @GetMapping("/status/{status}/page")
    public ResponseEntity<
            LearningPageResponse<LearningPathSummaryResponse>
            > getByStatusPage(
            @PathVariable LearningPathStatus status,
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                learningPathService.getMyPathsByStatus(
                        userEmail,
                        status,
                        page,
                        size
                )
        );
    }

    @PatchMapping("/{pathId}")
    public ResponseEntity<LearningPathResponse> update(
            @PathVariable Long pathId,
            @Valid @RequestBody UpdateLearningPathRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningPathService.update(
                        pathId,
                        request,
                        userEmail
                )
        );
    }

    @PatchMapping("/{pathId}/activate")
    public ResponseEntity<LearningPathResponse> activate(
            @PathVariable Long pathId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningPathService.activate(
                        pathId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{pathId}/pause")
    public ResponseEntity<LearningPathResponse> pause(
            @PathVariable Long pathId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningPathService.pause(
                        pathId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{pathId}/resume")
    public ResponseEntity<LearningPathResponse> resume(
            @PathVariable Long pathId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningPathService.resume(
                        pathId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{pathId}/complete")
    public ResponseEntity<LearningPathResponse> complete(
            @PathVariable Long pathId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningPathService.complete(
                        pathId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{pathId}/archive")
    public ResponseEntity<LearningPathResponse> archive(
            @PathVariable Long pathId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningPathService.archive(
                        pathId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{pathId}/advance")
    public ResponseEntity<LearningPathResponse> advanceStep(
            @PathVariable Long pathId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningPathService.advanceStep(
                        pathId,
                        userEmail
                )
        );
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningPathService.countMyPaths(userEmail)
        );
    }

    @GetMapping("/count/{status}")
    public ResponseEntity<Long> countByStatus(
            @PathVariable LearningPathStatus status,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningPathService.countMyPathsByStatus(
                        userEmail,
                        status
                )
        );
    }

    @DeleteMapping("/{pathId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long pathId,
            @RequestParam String userEmail
    ) {
        learningPathService.delete(
                pathId,
                userEmail
        );

        return ResponseEntity.noContent().build();
    }
}