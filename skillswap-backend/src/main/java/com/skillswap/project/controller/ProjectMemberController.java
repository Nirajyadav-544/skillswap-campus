
package com.skillswap.project.controller;

import com.skillswap.project.dto.AddProjectMemberRequest;
import com.skillswap.project.dto.ProjectMemberResponse;
import com.skillswap.project.service.ProjectMemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @PostMapping("/{projectId:\\d+}/members")
    public ResponseEntity<ProjectMemberResponse> addMember(
            @PathVariable Long projectId,
            @Valid @RequestBody AddProjectMemberRequest request,
            @RequestParam String userEmail
    ) {
        ProjectMemberResponse response =
                projectMemberService.addMember(
                        projectId,
                        request,
                        userEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{projectId:\\d+}/members")
    public ResponseEntity<List<ProjectMemberResponse>> getMembers(
            @PathVariable Long projectId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectMemberService.getMembers(
                        projectId,
                        userEmail
                )
        );
    }

    @GetMapping("/{projectId:\\d+}/members/{memberId:\\d+}")
    public ResponseEntity<ProjectMemberResponse> getMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectMemberService.getMember(
                        projectId,
                        memberId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{projectId:\\d+}/members/{memberId:\\d+}/activate")
    public ResponseEntity<ProjectMemberResponse> activate(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectMemberService.activate(
                        projectId,
                        memberId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{projectId:\\d+}/members/{memberId:\\d+}/remove")
    public ResponseEntity<ProjectMemberResponse> remove(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectMemberService.remove(
                        projectId,
                        memberId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{projectId:\\d+}/members/{memberId:\\d+}/leave")
    public ResponseEntity<ProjectMemberResponse> leave(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                projectMemberService.leave(
                        projectId,
                        memberId,
                        userEmail
                )
        );
    }

    @DeleteMapping("/{projectId:\\d+}/members/{memberId:\\d+}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestParam String userEmail
    ) {
        projectMemberService.delete(
                projectId,
                memberId,
                userEmail
        );

        return ResponseEntity.noContent().build();
    }
}

