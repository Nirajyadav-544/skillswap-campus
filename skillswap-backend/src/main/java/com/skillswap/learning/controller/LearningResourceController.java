package com.skillswap.learning.controller;

import com.skillswap.learning.dto.CreateLearningResourceRequest;
import com.skillswap.learning.dto.LearningPageResponse;
import com.skillswap.learning.dto.LearningResourceResponse;
import com.skillswap.learning.dto.UpdateLearningResourceRequest;
import com.skillswap.learning.entity.LearningResourceType;
import com.skillswap.learning.service.LearningResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learning/resources")
public class LearningResourceController {

    private final LearningResourceService learningResourceService;

    public LearningResourceController(
            LearningResourceService learningResourceService
    ) {
        this.learningResourceService = learningResourceService;
    }

    @PostMapping
    public ResponseEntity<LearningResourceResponse> create(
            @Valid @RequestBody CreateLearningResourceRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        learningResourceService.create(
                                request,
                                userEmail
                        )
                );
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<LearningResourceResponse> getById(
            @PathVariable Long resourceId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningResourceService.getById(
                        resourceId,
                        userEmail
                )
        );
    }

    @GetMapping("/path/{pathId}")
    public ResponseEntity<List<LearningResourceResponse>> getByPath(
            @PathVariable Long pathId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningResourceService.getByPath(
                        pathId,
                        userEmail
                )
        );
    }

    @GetMapping("/path/{pathId}/page")
    public ResponseEntity<
            LearningPageResponse<LearningResourceResponse>
            > getByPathPage(
            @PathVariable Long pathId,
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                learningResourceService.getByPath(
                        pathId,
                        userEmail,
                        page,
                        size
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<LearningResourceResponse>> getMyResources(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningResourceService.getMyResources(userEmail)
        );
    }

    @GetMapping("/type/{resourceType}")
    public ResponseEntity<List<LearningResourceResponse>> getByType(
            @PathVariable LearningResourceType resourceType,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningResourceService.getMyResourcesByType(
                        userEmail,
                        resourceType
                )
        );
    }

    @PatchMapping("/{resourceId}")
    public ResponseEntity<LearningResourceResponse> update(
            @PathVariable Long resourceId,
            @Valid @RequestBody UpdateLearningResourceRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningResourceService.update(
                        resourceId,
                        request,
                        userEmail
                )
        );
    }

    @PatchMapping("/{resourceId}/complete")
    public ResponseEntity<LearningResourceResponse> markCompleted(
            @PathVariable Long resourceId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningResourceService.markCompleted(
                        resourceId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{resourceId}/incomplete")
    public ResponseEntity<LearningResourceResponse> markIncomplete(
            @PathVariable Long resourceId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningResourceService.markIncomplete(
                        resourceId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{resourceId}/toggle")
    public ResponseEntity<LearningResourceResponse> toggleCompleted(
            @PathVariable Long resourceId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningResourceService.toggleCompleted(
                        resourceId,
                        userEmail
                )
        );
    }

    @GetMapping("/path/{pathId}/count")
    public ResponseEntity<Long> countByPath(
            @PathVariable Long pathId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningResourceService.countByPath(
                        pathId,
                        userEmail
                )
        );
    }

    @GetMapping("/path/{pathId}/count/completed")
    public ResponseEntity<Long> countCompletedByPath(
            @PathVariable Long pathId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningResourceService.countCompletedByPath(
                        pathId,
                        userEmail
                )
        );
    }

    @GetMapping("/path/{pathId}/count/incomplete")
    public ResponseEntity<Long> countIncompleteByPath(
            @PathVariable Long pathId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                learningResourceService.countIncompleteByPath(
                        pathId,
                        userEmail
                )
        );
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long resourceId,
            @RequestParam String userEmail
    ) {
        learningResourceService.delete(
                resourceId,
                userEmail
        );

        return ResponseEntity.noContent().build();
    }
}