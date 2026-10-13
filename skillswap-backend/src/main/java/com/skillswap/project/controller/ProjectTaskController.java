package com.skillswap.project.controller;

import com.skillswap.project.dto.CreateProjectTaskRequest;
import com.skillswap.project.dto.ProjectTaskResponse;
import com.skillswap.project.dto.UpdateProjectTaskRequest;
import com.skillswap.project.entity.ProjectTaskPriority;
import com.skillswap.project.service.ProjectTaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectTaskController {

    private final ProjectTaskService projectTaskService;

    public ProjectTaskController(
            ProjectTaskService projectTaskService
    ) {
        this.projectTaskService = projectTaskService;
    }

    @PostMapping("/{projectId:\\d+}/tasks")
    public ResponseEntity<ProjectTaskResponse> create(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateProjectTaskRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        projectTaskService.create(
                                projectId,
                                request,
                                userEmail
                        )
                );
    }

    @GetMapping("/{projectId:\\d+}/tasks")
    public ResponseEntity<List<ProjectTaskResponse>> getTasks(
            @PathVariable Long projectId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectTaskService.getTasks(
                        projectId,
                        userEmail
                )
        );
    }

    @GetMapping("/{projectId:\\d+}/tasks/page")
    public ResponseEntity<Page<ProjectTaskResponse>> getTasksPage(
            @PathVariable Long projectId,
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                projectTaskService.getTasksPage(
                        projectId,
                        userEmail,
                        page,
                        size
                )
        );
    }

    @GetMapping("/{projectId:\\d+}/tasks/{taskId:\\d+}")
    public ResponseEntity<ProjectTaskResponse> getById(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectTaskService.getById(
                        projectId,
                        taskId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{projectId:\\d+}/tasks/{taskId:\\d+}")
    public ResponseEntity<ProjectTaskResponse> update(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateProjectTaskRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectTaskService.update(
                        projectId,
                        taskId,
                        request,
                        userEmail
                )
        );
    }

    @PatchMapping("/{projectId:\\d+}/tasks/{taskId:\\d+}/start")
    public ResponseEntity<ProjectTaskResponse> start(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectTaskService.start(
                        projectId,
                        taskId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{projectId:\\d+}/tasks/{taskId:\\d+}/block")
    public ResponseEntity<ProjectTaskResponse> block(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectTaskService.block(
                        projectId,
                        taskId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{projectId:\\d+}/tasks/{taskId:\\d+}/review")
    public ResponseEntity<ProjectTaskResponse> sendForReview(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectTaskService.sendForReview(
                        projectId,
                        taskId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{projectId:\\d+}/tasks/{taskId:\\d+}/complete")
    public ResponseEntity<ProjectTaskResponse> complete(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectTaskService.complete(
                        projectId,
                        taskId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{projectId:\\d+}/tasks/{taskId:\\d+}/cancel")
    public ResponseEntity<ProjectTaskResponse> cancel(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectTaskService.cancel(
                        projectId,
                        taskId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{projectId:\\d+}/tasks/{taskId:\\d+}/reopen")
    public ResponseEntity<ProjectTaskResponse> reopen(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectTaskService.reopen(
                        projectId,
                        taskId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{projectId:\\d+}/tasks/{taskId:\\d+}/assign/{assigneeUserId:\\d+}")
    public ResponseEntity<ProjectTaskResponse> assign(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long assigneeUserId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectTaskService.assign(
                        projectId,
                        taskId,
                        assigneeUserId,
                        userEmail
                )
        );
    }

    @GetMapping("/tasks/assigned")
    public ResponseEntity<List<ProjectTaskResponse>> getMyAssignedTasks(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectTaskService.getMyAssignedTasks(userEmail)
        );
    }

    @GetMapping("/{projectId:\\d+}/tasks/priority/{priority}")
    public ResponseEntity<List<ProjectTaskResponse>> getTasksByPriority(
            @PathVariable Long projectId,
            @PathVariable ProjectTaskPriority priority,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectTaskService.getProjectTasksByPriority(
                        projectId,
                        priority,
                        userEmail
                )
        );
    }

    @DeleteMapping("/{projectId:\\d+}/tasks/{taskId:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam String userEmail
    ) {
        projectTaskService.delete(
                projectId,
                taskId,
                userEmail
        );

        return ResponseEntity.noContent().build();
    }
}