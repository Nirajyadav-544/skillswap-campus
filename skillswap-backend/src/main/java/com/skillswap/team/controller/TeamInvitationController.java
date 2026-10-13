package com.skillswap.team.controller;

import com.skillswap.common.exception.UnauthorizedException;
import com.skillswap.common.response.ApiResponse;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.team.dto.TeamInvitationRequest;
import com.skillswap.team.dto.TeamInvitationResponse;
import com.skillswap.team.service.TeamInvitationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamInvitationController {

    private final TeamInvitationService teamInvitationService;
    private final UserRepository userRepository;

    public TeamInvitationController(
            TeamInvitationService teamInvitationService,
            UserRepository userRepository
    ) {
        this.teamInvitationService = teamInvitationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{teamId}/invitations")
    public ResponseEntity<ApiResponse<TeamInvitationResponse>> sendInvitation(
            Authentication authentication,
            @PathVariable Long teamId,
            @Valid @RequestBody TeamInvitationRequest request
    ) {
        User user = getUser(authentication.getName());

        TeamInvitationResponse response =
                teamInvitationService.sendInvitation(
                        teamId,
                        user,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team invitation sent successfully",
                        response
                )
        );
    }

    @GetMapping("/invitations/received")
    public ResponseEntity<ApiResponse<List<TeamInvitationResponse>>> getReceivedInvitations(
            Authentication authentication
    ) {
        User user = getUser(authentication.getName());

        List<TeamInvitationResponse> response =
                teamInvitationService.getReceivedInvitations(user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Received invitations retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/invitations/sent")
    public ResponseEntity<ApiResponse<List<TeamInvitationResponse>>> getSentInvitations(
            Authentication authentication
    ) {
        User user = getUser(authentication.getName());

        List<TeamInvitationResponse> response =
                teamInvitationService.getSentInvitations(user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Sent invitations retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/{teamId}/invitations")
    public ResponseEntity<ApiResponse<List<TeamInvitationResponse>>> getTeamInvitations(
            Authentication authentication,
            @PathVariable Long teamId
    ) {
        User user = getUser(authentication.getName());

        List<TeamInvitationResponse> response =
                teamInvitationService.getTeamInvitations(
                        teamId,
                        user
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team invitations retrieved successfully",
                        response
                )
        );
    }

    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<ApiResponse<TeamInvitationResponse>> acceptInvitation(
            Authentication authentication,
            @PathVariable Long invitationId
    ) {
        User user = getUser(authentication.getName());

        TeamInvitationResponse response =
                teamInvitationService.acceptInvitation(
                        invitationId,
                        user
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team invitation accepted successfully",
                        response
                )
        );
    }

    @PostMapping("/invitations/{invitationId}/reject")
    public ResponseEntity<ApiResponse<TeamInvitationResponse>> rejectInvitation(
            Authentication authentication,
            @PathVariable Long invitationId
    ) {
        User user = getUser(authentication.getName());

        TeamInvitationResponse response =
                teamInvitationService.rejectInvitation(
                        invitationId,
                        user
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team invitation rejected successfully",
                        response
                )
        );
    }

    @DeleteMapping("/invitations/{invitationId}")
    public ResponseEntity<ApiResponse<Void>> cancelInvitation(
            Authentication authentication,
            @PathVariable Long invitationId
    ) {
        User user = getUser(authentication.getName());

        teamInvitationService.cancelInvitation(
                invitationId,
                user
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Team invitation cancelled successfully"
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