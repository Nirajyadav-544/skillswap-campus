package com.skillswap.team.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.team.dto.TeamInvitationRequest;
import com.skillswap.team.dto.TeamInvitationResponse;
import com.skillswap.team.entity.Team;
import com.skillswap.team.entity.TeamInvitation;
import com.skillswap.team.repository.TeamInvitationRepository;
import com.skillswap.team.repository.TeamMemberRepository;
import com.skillswap.team.repository.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamInvitationService {

    private final TeamInvitationRepository invitationRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    public TeamInvitationService(
            TeamInvitationRepository invitationRepository,
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            UserRepository userRepository
    ) {
        this.invitationRepository = invitationRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
    }

    public TeamInvitationResponse sendInvitation(
            Long teamId,
            User inviter,
            TeamInvitationRequest request
    ) {
        Team team = teamRepository
                .findByIdAndOwner(teamId, inviter)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found"
                        )
                );

        User invitee = userRepository
                .findById(request.getInviteeUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Invitee user not found"
                        )
                );

        if (invitee.getId().equals(inviter.getId())) {
            throw new BadRequestException(
                    "You cannot invite yourself"
            );
        }

        if (teamMemberRepository.existsByTeamAndUser(team, invitee)) {
            throw new BadRequestException(
                    "User is already a member of this team"
            );
        }

        long currentMembers =
                teamMemberRepository.countByTeam(team);

        if (currentMembers >= team.getMaxMembers()) {
            throw new BadRequestException(
                    "Team has reached its maximum member capacity"
            );
        }

        boolean pendingInvitation =
                invitationRepository
                        .existsByTeamAndInviteeAndStatus(
                                team,
                                invitee,
                                "PENDING"
                        );

        if (pendingInvitation) {
            throw new BadRequestException(
                    "A pending invitation already exists for this user"
            );
        }

        TeamInvitation invitation =
                new TeamInvitation(
                        team,
                        inviter,
                        invitee,
                        request.getMessage()
                );

        return toResponse(
                invitationRepository.save(invitation)
        );
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TeamInvitationResponse> getReceivedInvitations(
            User invitee
    ) {
        return invitationRepository
                .findByInviteeOrderByCreatedAtDesc(invitee)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TeamInvitationResponse> getSentInvitations(
            User inviter
    ) {
        return invitationRepository
                .findByInviterOrderByCreatedAtDesc(inviter)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TeamInvitationResponse> getTeamInvitations(
            Long teamId,
            User owner
    ) {
        Team team = teamRepository
                .findByIdAndOwner(teamId, owner)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found"
                        )
                );

        return invitationRepository
                .findByTeamOrderByCreatedAtDesc(team)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TeamInvitationResponse acceptInvitation(
            Long invitationId,
            User invitee
    ) {
        TeamInvitation invitation =
                invitationRepository
                        .findByIdAndInvitee(
                                invitationId,
                                invitee
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Invitation not found"
                                )
                        );

        validatePending(invitation);

        Team team = invitation.getTeam();

        if (teamMemberRepository.existsByTeamAndUser(
                team,
                invitee
        )) {
            throw new BadRequestException(
                    "You are already a member of this team"
            );
        }

        long currentMembers =
                teamMemberRepository.countByTeam(team);

        if (currentMembers >= team.getMaxMembers()) {
            throw new BadRequestException(
                    "Team has reached its maximum member capacity"
            );
        }

        invitation.accept();

        return toResponse(
                invitationRepository.save(invitation)
        );
    }

    public TeamInvitationResponse rejectInvitation(
            Long invitationId,
            User invitee
    ) {
        TeamInvitation invitation =
                invitationRepository
                        .findByIdAndInvitee(
                                invitationId,
                                invitee
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Invitation not found"
                                )
                        );

        validatePending(invitation);

        invitation.reject();

        return toResponse(
                invitationRepository.save(invitation)
        );
    }

    public void cancelInvitation(
            Long invitationId,
            User inviter
    ) {
        TeamInvitation invitation =
                invitationRepository
                        .findById(invitationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Invitation not found"
                                )
                        );

        if (!invitation.getInviter().getId()
                .equals(inviter.getId())) {
            throw new BadRequestException(
                    "You are not allowed to cancel this invitation"
            );
        }

        validatePending(invitation);

        invitation.setStatus("CANCELLED");
        invitation.setRespondedAt(
                java.time.LocalDateTime.now()
        );

        invitationRepository.save(invitation);
    }

    private void validatePending(
            TeamInvitation invitation
    ) {
        if (!"PENDING".equals(invitation.getStatus())) {
            throw new BadRequestException(
                    "Invitation is no longer pending"
            );
        }
    }

    private TeamInvitationResponse toResponse(
            TeamInvitation invitation
    ) {
        TeamInvitationResponse response =
                new TeamInvitationResponse();

        response.setId(invitation.getId());

        if (invitation.getTeam() != null) {
            response.setTeamId(
                    invitation.getTeam().getId()
            );
            response.setTeamName(
                    invitation.getTeam().getName()
            );
        }

        if (invitation.getInviter() != null) {
            response.setInviterId(
                    invitation.getInviter().getId()
            );
            response.setInviterName(
                    getUserName(invitation.getInviter())
            );
        }

        if (invitation.getInvitee() != null) {
            response.setInviteeId(
                    invitation.getInvitee().getId()
            );
            response.setInviteeName(
                    getUserName(invitation.getInvitee())
            );
        }

        response.setMessage(invitation.getMessage());
        response.setStatus(invitation.getStatus());
        response.setCreatedAt(invitation.getCreatedAt());
        response.setRespondedAt(invitation.getRespondedAt());

        return response;
    }

    private String getUserName(User user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        String name = "";

        if (firstName != null) {
            name += firstName;
        }

        if (lastName != null) {
            if (!name.isBlank()) {
                name += " ";
            }

            name += lastName;
        }

        if (name.isBlank()) {
            name = user.getEmail();
        }

        return name;
    }
}