package com.skillswap.team.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.team.entity.Team;
import com.skillswap.team.entity.TeamStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByOwnerOrderByCreatedAtDesc(User owner);

    Page<Team> findByOwnerOrderByCreatedAtDesc(
            User owner,
            Pageable pageable
    );

    List<Team> findByStatusOrderByCreatedAtDesc(TeamStatus status);

    Page<Team> findByStatusOrderByCreatedAtDesc(
            TeamStatus status,
            Pageable pageable
    );

    Optional<Team> findByIdAndOwner(Long id, User owner);

    long countByOwner(User owner);

    long countByStatus(TeamStatus status);

    boolean existsByIdAndOwner(Long id, User owner);
}