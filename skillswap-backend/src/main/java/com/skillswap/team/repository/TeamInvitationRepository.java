package com.skillswap.team.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.team.entity.Team;
import com.skillswap.team.entity.TeamInvitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamInvitationRepository
        extends JpaRepository<TeamInvitation, Long> {

    List<TeamInvitation> findByTeamOrderByCreatedAtDesc(Team team);

    Page<TeamInvitation> findByTeamOrderByCreatedAtDesc(
            Team team,
            Pageable pageable
    );

    List<TeamInvitation> findByInviterOrderByCreatedAtDesc(User inviter);

    List<TeamInvitation> findByInviteeOrderByCreatedAtDesc(User invitee);

    Page<TeamInvitation> findByInviteeOrderByCreatedAtDesc(
            User invitee,
            Pageable pageable
    );

    Optional<TeamInvitation> findByIdAndInvitee(
            Long id,
            User invitee
    );

    Optional<TeamInvitation> findByIdAndTeam(
            Long id,
            Team team
    );

    Optional<TeamInvitation> findByTeamAndInviteeAndStatus(
            Team team,
            User invitee,
            String status
    );

    List<TeamInvitation> findByTeamAndStatusOrderByCreatedAtDesc(
            Team team,
            String status
    );

    List<TeamInvitation> findByInviteeAndStatusOrderByCreatedAtDesc(
            User invitee,
            String status
    );

    long countByTeam(Team team);

    long countByTeamAndStatus(
            Team team,
            String status
    );

    long countByInviteeAndStatus(
            User invitee,
            String status
    );

    boolean existsByTeamAndInviteeAndStatus(
            Team team,
            User invitee,
            String status
    );
}