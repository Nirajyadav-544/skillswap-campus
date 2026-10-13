package com.skillswap.project.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.project.entity.Project;
import com.skillswap.project.entity.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwnerUser(User ownerUser);

    Page<Project> findByOwnerUser(User ownerUser, Pageable pageable);

    List<Project> findByStatus(ProjectStatus status);

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    List<Project> findByOwnerUserAndStatus(
            User ownerUser,
            ProjectStatus status
    );

    Page<Project> findByOwnerUserAndStatus(
            User ownerUser,
            ProjectStatus status,
            Pageable pageable
    );

    List<Project> findBySkillNameIgnoreCase(String skillName);

    Page<Project> findBySkillNameIgnoreCase(
            String skillName,
            Pageable pageable
    );

    List<Project> findByCategoryIgnoreCase(String category);

    Page<Project> findByCategoryIgnoreCase(
            String category,
            Pageable pageable
    );

    Optional<Project> findByIdAndOwnerUser(
            Long id,
            User ownerUser
    );

    Optional<Project> findByIdAndOwnerUserId(
            Long id,
            Long ownerUserId
    );

    long countByOwnerUser(User ownerUser);

    long countByOwnerUserAndStatus(
            User ownerUser,
            ProjectStatus status
    );

    long countByStatus(ProjectStatus status);

    long countBySkillNameIgnoreCase(String skillName);

    boolean existsByIdAndOwnerUser(
            Long id,
            User ownerUser
    );

    void deleteByOwnerUser(User ownerUser);
}