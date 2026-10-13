package com.skillswap.team.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.team.entity.Team;
import com.skillswap.team.entity.TeamMember;
import com.skillswap.team.entity.TeamMemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByTeamOrderByJoinedAtAsc(Team team);

    Page<TeamMember> findByTeamOrderByJoinedAtAsc(
            Team team,
            Pageable pageable
    );

    Optional<TeamMember> findByTeamAndUser(
            Team team,
            User user
    );

    Optional<TeamMember> findByIdAndTeam(
            Long id,
            Team team
    );

    List<TeamMember> findByUserOrderByJoinedAtDesc(User user);

    List<TeamMember> findByTeamAndRole(
            Team team,
            TeamMemberRole role
    );

    long countByTeam(Team team);

    long countByTeamAndRole(
            Team team,
            TeamMemberRole role
    );

    boolean existsByTeamAndUser(
            Team team,
            User user
    );

    boolean existsByIdAndTeam(
            Long id,
            Team team
    );

    void deleteByTeamAndUser(
            Team team,
            User user
    );
}