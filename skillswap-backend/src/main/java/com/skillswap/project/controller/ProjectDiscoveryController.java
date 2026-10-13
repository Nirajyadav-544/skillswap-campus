package com.skillswap.project.controller;

import com.skillswap.project.dto.ProjectPageResponse;
import com.skillswap.project.dto.ProjectSearchRequest;
import com.skillswap.project.dto.ProjectSummaryResponse;
import com.skillswap.project.entity.ProjectStatus;
import com.skillswap.project.service.ProjectDiscoveryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/discovery")
public class ProjectDiscoveryController {

    private final ProjectDiscoveryService projectDiscoveryService;

    public ProjectDiscoveryController(
            ProjectDiscoveryService projectDiscoveryService
    ) {
        this.projectDiscoveryService = projectDiscoveryService;
    }

    @GetMapping
    public ResponseEntity<ProjectPageResponse> search(
            @Valid ProjectSearchRequest request
    ) {
        return ResponseEntity.ok(
                projectDiscoveryService.search(request)
        );
    }

    @GetMapping("/skill")
    public ResponseEntity<ProjectPageResponse> searchBySkill(
            @RequestParam String skillName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                projectDiscoveryService.searchBySkill(
                        skillName,
                        page,
                        size
                )
        );
    }

    @GetMapping("/category")
    public ResponseEntity<ProjectPageResponse> searchByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                projectDiscoveryService.searchByCategory(
                        category,
                        page,
                        size
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ProjectPageResponse> searchByStatus(
            @PathVariable ProjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                projectDiscoveryService.searchByStatus(
                        status,
                        page,
                        size
                )
        );
    }

    @GetMapping("/owner/{ownerUserId}")
    public ResponseEntity<ProjectPageResponse> searchByOwner(
            @PathVariable Long ownerUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                projectDiscoveryService.searchByOwner(
                        ownerUserId,
                        page,
                        size
                )
        );
    }

    @GetMapping("/open")
    public ResponseEntity<List<ProjectSummaryResponse>> getOpenProjects(
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(
                projectDiscoveryService.getOpenProjects(limit)
        );
    }

    @GetMapping("/by-skill")
    public ResponseEntity<List<ProjectSummaryResponse>> getProjectsBySkill(
            @RequestParam String skillName
    ) {
        return ResponseEntity.ok(
                projectDiscoveryService.getProjectsBySkill(skillName)
        );
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<ProjectSummaryResponse>> getProjectsByCategory(
            @RequestParam String category
    ) {
        return ResponseEntity.ok(
                projectDiscoveryService.getProjectsByCategory(category)
        );
    }

    @GetMapping("/skill/count")
    public ResponseEntity<Long> countBySkill(
            @RequestParam String skillName
    ) {
        return ResponseEntity.ok(
                projectDiscoveryService.countBySkill(skillName)
        );
    }
}