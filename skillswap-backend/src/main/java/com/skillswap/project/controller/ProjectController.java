package com.skillswap.project.controller;

import com.skillswap.project.dto.CreateProjectRequest;
import com.skillswap.project.dto.ProjectPageResponse;
import com.skillswap.project.dto.ProjectResponse;
import com.skillswap.project.dto.ProjectSummaryResponse;
import com.skillswap.project.dto.UpdateProjectRequest;
import com.skillswap.project.entity.ProjectStatus;
import com.skillswap.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody CreateProjectRequest request,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                projectService.create(request, userEmail)
        );
    }

    @GetMapping("/{projectId:\\d+}")
    public ResponseEntity<ProjectResponse> getById(
            @PathVariable Long projectId,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                projectService.getById(projectId, userEmail)
        );
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ProjectSummaryResponse>> getMyProjects(
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                projectService.getMyProjects(userEmail)
        );
    }

    @GetMapping("/mine/status/{status}")
    public ResponseEntity<List<ProjectSummaryResponse>> getMyProjectsByStatus(
            @PathVariable ProjectStatus status,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                projectService.getMyProjectsByStatus(userEmail, status)
        );
    }

    @GetMapping("/mine/page")
    public ResponseEntity<Page<ProjectSummaryResponse>> getMyProjectsPage(
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                projectService.getMyProjectsPage(userEmail, page, size)
        );
    }

    @PatchMapping("/{projectId:\\d+}")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectRequest request,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                projectService.update(projectId, request, userEmail)
        );
    }

    @PatchMapping("/{projectId:\\d+}/open")
    public ResponseEntity<ProjectResponse> open(
            @PathVariable Long projectId,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                projectService.open(projectId, userEmail)
        );
    }

    @PatchMapping("/{projectId:\\d+}/start")
    public ResponseEntity<ProjectResponse> start(
            @PathVariable Long projectId,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                projectService.start(projectId, userEmail)
        );
    }

    @PatchMapping("/{projectId:\\d+}/hold")
    public ResponseEntity<ProjectResponse> putOnHold(
            @PathVariable Long projectId,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                projectService.putOnHold(projectId, userEmail)
        );
    }

    @PatchMapping("/{projectId:\\d+}/resume")
    public ResponseEntity<ProjectResponse> resume(
            @PathVariable Long projectId,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                projectService.resume(projectId, userEmail)
        );
    }

    @PatchMapping("/{projectId:\\d+}/complete")
    public ResponseEntity<ProjectResponse> complete(
            @PathVariable Long projectId,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                projectService.complete(projectId, userEmail)
        );
    }

    @PatchMapping("/{projectId:\\d+}/cancel")
    public ResponseEntity<ProjectResponse> cancel(
            @PathVariable Long projectId,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                projectService.cancel(projectId, userEmail)
        );
    }

    @PatchMapping("/{projectId:\\d+}/archive")
    public ResponseEntity<ProjectResponse> archive(
            @PathVariable Long projectId,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                projectService.archive(projectId, userEmail)
        );
    }

    @DeleteMapping("/{projectId:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable Long projectId,
            @RequestParam String userEmail) {

        projectService.delete(projectId, userEmail);

        return ResponseEntity.noContent().build();
    }
}