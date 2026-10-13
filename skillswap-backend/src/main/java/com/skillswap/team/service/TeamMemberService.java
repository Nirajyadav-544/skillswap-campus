package com.skillswap.team.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.team.dto.TeamMemberResponse;
import com.skillswap.team.entity.Team;
import com.skillswap.team.entity.TeamMember;
import com.skillswap.team.entity.TeamMemberRole;
import com.skillswap.team.repository.TeamMemberRepository;
import com.skillswap.team.repository.TeamRepository;
import com.skillswap.identity.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamMemberService(
            TeamMemberRepository teamMemberRepository,
            TeamRepository teamRepository,
            UserRepository userRepository
    ) {
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public TeamMemberResponse addMember(
            Long teamId,
            User owner,
            Long userId
    ) {
        Team team = getOwnedTeam(teamId, owner);

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        if (teamMemberRepository.existsByTeamAndUser(team, user)) {
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

        TeamMember member = new TeamMember(
                team,
                user,
                TeamMemberRole.MEMBER
        );

        return toResponse(
                teamMemberRepository.save(member)
        );
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TeamMemberResponse> getMembers(
            Long teamId
    ) {
        Team team = getTeam(teamId);

        return teamMemberRepository
                .findByTeamOrderByJoinedAtAsc(team)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public TeamMemberResponse getMember(
            Long teamId,
            Long memberId
    ) {
        Team team = getTeam(teamId);

        TeamMember member = teamMemberRepository
                .findByIdAndTeam(memberId, team)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team member not found"
                        )
                );

        return toResponse(member);
    }

    public TeamMemberResponse updateRole(
            Long teamId,
            Long memberId,
            User owner,
            TeamMemberRole role
    ) {
        if (role == null) {
            throw new BadRequestException(
                    "Member role is required"
            );
        }

        Team team = getOwnedTeam(teamId, owner);

        TeamMember member = teamMemberRepository
                .findByIdAndTeam(memberId, team)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team member not found"
                        )
                );

        if (member.getUser().getId().equals(owner.getId())) {
            throw new BadRequestException(
                    "Team owner role cannot be changed"
            );
        }

        member.setRole(role);

        return toResponse(
                teamMemberRepository.save(member)
        );
    }

    public void removeMember(
            Long teamId,
            Long memberId,
            User owner
    ) {
        Team team = getOwnedTeam(teamId, owner);

        TeamMember member = teamMemberRepository
                .findByIdAndTeam(memberId, team)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team member not found"
                        )
                );

        if (member.getUser().getId().equals(owner.getId())) {
            throw new BadRequestException(
                    "Team owner cannot be removed from the team"
            );
        }

        teamMemberRepository.delete(member);
    }

    public void leaveTeam(
            Long teamId,
            User user
    ) {
        Team team = getTeam(teamId);

        TeamMember member = teamMemberRepository
                .findByTeamAndUser(team, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "You are not a member of this team"
                        )
                );

        if (member.getRole() == TeamMemberRole.OWNER) {
            throw new BadRequestException(
                    "Team owner cannot leave the team"
            );
        }

        teamMemberRepository.delete(member);
    }

    private Team getTeam(Long teamId) {
        return teamRepository
                .findById(teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found"
                        )
                );
    }

    private Team getOwnedTeam(
            Long teamId,
            User owner
    ) {
        return teamRepository
                .findByIdAndOwner(teamId, owner)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found"
                        )
                );
    }

    private TeamMemberResponse toResponse(
            TeamMember member
    ) {
        TeamMemberResponse response =
                new TeamMemberResponse();

        response.setId(member.getId());
        response.setTeamId(
                member.getTeam().getId()
        );

        if (member.getUser() != null) {
            response.setUserId(
                    member.getUser().getId()
            );

            response.setUserEmail(
                    member.getUser().getEmail()
            );

            String firstName =
                    member.getUser().getFirstName();

            String lastName =
                    member.getUser().getLastName();

            String userName = "";

            if (firstName != null) {
                userName += firstName;
            }

            if (lastName != null) {
                if (!userName.isBlank()) {
                    userName += " ";
                }

                userName += lastName;
            }

            if (userName.isBlank()) {
                userName = member.getUser().getEmail();
            }

            response.setUserName(userName);
        }

        response.setRole(member.getRole());
        response.setJoinedAt(member.getJoinedAt());

        return response;
    }
}