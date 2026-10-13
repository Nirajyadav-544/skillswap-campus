package com.skillswap.team.controller;

import com.skillswap.common.exception.UnauthorizedException;
import com.skillswap.common.response.ApiResponse;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.team.dto.CreateTeamRequest;
import com.skillswap.team.dto.TeamResponse;
import com.skillswap.team.dto.UpdateTeamRequest;
import com.skillswap.team.entity.TeamStatus;
import com.skillswap.team.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamService teamService;
    private final UserRepository userRepository;

    public TeamController(
            TeamService teamService,
            UserRepository userRepository
    ) {
        this.teamService = teamService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(
            Authentication authentication,
            @Valid @RequestBody CreateTeamRequest request
    ) {
        User user = getUser(authentication.getName());

        TeamResponse response =
                teamService.createTeam(user, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team created successfully",
                        response
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getMyTeams(
            Authentication authentication
    ) {
        User user = getUser(authentication.getName());

        List<TeamResponse> response =
                teamService.getMyTeams(user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Teams retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getTeamsByStatus(
            @PathVariable TeamStatus status
    ) {
        List<TeamResponse> response =
                teamService.getTeamsByStatus(status);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Teams retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeam(
            @PathVariable Long teamId
    ) {
        TeamResponse response =
                teamService.getTeam(teamId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/my/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponse>> getMyTeam(
            Authentication authentication,
            @PathVariable Long teamId
    ) {
        User user = getUser(authentication.getName());

        TeamResponse response =
                teamService.getMyTeam(teamId, user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team retrieved successfully",
                        response
                )
        );
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(
            Authentication authentication,
            @PathVariable Long teamId,
            @Valid @RequestBody UpdateTeamRequest request
    ) {
        User user = getUser(authentication.getName());

        TeamResponse response =
                teamService.updateTeam(
                        teamId,
                        user,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team updated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{teamId}/status")
    public ResponseEntity<ApiResponse<TeamResponse>> changeStatus(
            Authentication authentication,
            @PathVariable Long teamId,
            @RequestParam TeamStatus status
    ) {
        User user = getUser(authentication.getName());

        TeamResponse response =
                teamService.changeStatus(
                        teamId,
                        user,
                        status
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team status updated successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(
            Authentication authentication,
            @PathVariable Long teamId
    ) {
        User user = getUser(authentication.getName());

        teamService.deleteTeam(teamId, user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team deleted successfully"
                )
        );
    }

    private User getUser(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "Authenticated user not found"
                        )
                );
    }
}