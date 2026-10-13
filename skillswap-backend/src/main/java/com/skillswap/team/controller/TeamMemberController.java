package com.skillswap.team.controller;

import com.skillswap.common.exception.UnauthorizedException;
import com.skillswap.common.response.ApiResponse;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.team.dto.AddTeamMemberRequest;
import com.skillswap.team.dto.TeamMemberResponse;
import com.skillswap.team.entity.TeamMemberRole;
import com.skillswap.team.service.TeamMemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;
    private final UserRepository userRepository;

    public TeamMemberController(
            TeamMemberService teamMemberService,
            UserRepository userRepository
    ) {
        this.teamMemberService = teamMemberService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> addMember(
            Authentication authentication,
            @PathVariable Long teamId,
            @Valid @RequestBody AddTeamMemberRequest request
    ) {
        User user = getUser(authentication.getName());

        TeamMemberResponse response =
                teamMemberService.addMember(
                        teamId,
                        user,
                        request.getUserId()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team member added successfully",
                        response
                )
        );
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<List<TeamMemberResponse>>> getMembers(
            @PathVariable Long teamId
    ) {
        List<TeamMemberResponse> response =
                teamMemberService.getMembers(teamId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team members retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> getMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId
    ) {
        TeamMemberResponse response =
                teamMemberService.getMember(
                        teamId,
                        memberId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team member retrieved successfully",
                        response
                )
        );
    }

    @PatchMapping("/{teamId}/members/{memberId}/role")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> updateRole(
            Authentication authentication,
            @PathVariable Long teamId,
            @PathVariable Long memberId,
            @RequestParam TeamMemberRole role
    ) {
        User user = getUser(authentication.getName());

        TeamMemberResponse response =
                teamMemberService.updateRole(
                        teamId,
                        memberId,
                        user,
                        role
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team member role updated successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            Authentication authentication,
            @PathVariable Long teamId,
            @PathVariable Long memberId
    ) {
        User user = getUser(authentication.getName());

        teamMemberService.removeMember(
                teamId,
                memberId,
                user
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team member removed successfully"
                )
        );
    }

    @DeleteMapping("/{teamId}/members/me")
    public ResponseEntity<ApiResponse<Void>> leaveTeam(
            Authentication authentication,
            @PathVariable Long teamId
    ) {
        User user = getUser(authentication.getName());

        teamMemberService.leaveTeam(
                teamId,
                user
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "You have left the team successfully"
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