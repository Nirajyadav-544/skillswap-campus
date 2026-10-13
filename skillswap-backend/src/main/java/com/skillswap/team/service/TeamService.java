package com.skillswap.team.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.team.dto.CreateTeamRequest;
import com.skillswap.team.dto.TeamResponse;
import com.skillswap.team.dto.UpdateTeamRequest;
import com.skillswap.team.entity.Team;
import com.skillswap.team.entity.TeamMember;
import com.skillswap.team.entity.TeamMemberRole;
import com.skillswap.team.entity.TeamStatus;
import com.skillswap.team.repository.TeamMemberRepository;
import com.skillswap.team.repository.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public TeamService(
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository
    ) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public TeamResponse createTeam(
            User owner,
            CreateTeamRequest request
    ) {
        if (request.getMaxMembers() == null) {
            request.setMaxMembers(5);
        }

        Team team = new Team(
                owner,
                request.getName(),
                request.getDescription(),
                request.getProjectTitle(),
                request.getProjectDescription(),
                request.getTargetSkill(),
                request.getMaxMembers()
        );

        team = teamRepository.save(team);

        TeamMember ownerMember = new TeamMember(
                team,
                owner,
                TeamMemberRole.OWNER
        );

        teamMemberRepository.save(ownerMember);

        return toResponse(team);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TeamResponse> getMyTeams(User owner) {
        return teamRepository
                .findByOwnerOrderByCreatedAtDesc(owner)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TeamResponse> getTeamsByStatus(
            TeamStatus status
    ) {
        if (status == null) {
            throw new BadRequestException("Team status is required");
        }

        return teamRepository
                .findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public TeamResponse getTeam(
            Long teamId
    ) {
        Team team = teamRepository
                .findById(teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found"
                        )
                );

        return toResponse(team);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public TeamResponse getMyTeam(
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

        return toResponse(team);
    }

    public TeamResponse updateTeam(
            Long teamId,
            User owner,
            UpdateTeamRequest request
    ) {
        Team team = teamRepository
                .findByIdAndOwner(teamId, owner)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found"
                        )
                );

        if (request.getName() != null) {
            team.setName(request.getName());
        }

        if (request.getDescription() != null) {
            team.setDescription(request.getDescription());
        }

        if (request.getProjectTitle() != null) {
            team.setProjectTitle(request.getProjectTitle());
        }

        if (request.getProjectDescription() != null) {
            team.setProjectDescription(
                    request.getProjectDescription()
            );
        }

        if (request.getTargetSkill() != null) {
            team.setTargetSkill(request.getTargetSkill());
        }

        if (request.getMaxMembers() != null) {
            long currentMembers =
                    teamMemberRepository.countByTeam(team);

            if (request.getMaxMembers() < currentMembers) {
                throw new BadRequestException(
                        "Maximum members cannot be less than current team members"
                );
            }

            team.setMaxMembers(request.getMaxMembers());
        }

        if (request.getStatus() != null) {
            team.setStatus(request.getStatus());
        }

        team = teamRepository.save(team);

        return toResponse(team);
    }

    public void deleteTeam(
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

        teamRepository.delete(team);
    }

    public TeamResponse changeStatus(
            Long teamId,
            User owner,
            TeamStatus status
    ) {
        if (status == null) {
            throw new BadRequestException(
                    "Team status is required"
            );
        }

        Team team = teamRepository
                .findByIdAndOwner(teamId, owner)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found"
                        )
                );

        team.setStatus(status);

        return toResponse(
                teamRepository.save(team)
        );
    }

    private TeamResponse toResponse(Team team) {

        TeamResponse response = new TeamResponse();

        response.setId(team.getId());

        if (team.getOwner() != null) {
            response.setOwnerId(
                    team.getOwner().getId()
            );

            String firstName = team.getOwner().getFirstName();
            String lastName = team.getOwner().getLastName();

            String ownerName = "";

            if (firstName != null) {
                ownerName += firstName;
            }

            if (lastName != null) {
                if (!ownerName.isBlank()) {
                    ownerName += " ";
                }
                ownerName += lastName;
            }

            if (ownerName.isBlank()) {
                ownerName = team.getOwner().getEmail();
            }

            response.setOwnerName(ownerName);
        }

        response.setName(team.getName());
        response.setDescription(team.getDescription());
        response.setProjectTitle(team.getProjectTitle());
        response.setProjectDescription(
                team.getProjectDescription()
        );
        response.setTargetSkill(team.getTargetSkill());
        response.setStatus(team.getStatus());
        response.setMaxMembers(team.getMaxMembers());

        response.setCurrentMembers(
                (int) teamMemberRepository.countByTeam(team)
        );

        response.setCreatedAt(team.getCreatedAt());
        response.setUpdatedAt(team.getUpdatedAt());

        return response;
    }
}