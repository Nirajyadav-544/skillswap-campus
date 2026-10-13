package com.skillswap.project.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.project.entity.Project;
import com.skillswap.project.entity.ProjectMember;
import com.skillswap.project.entity.ProjectMemberRole;
import com.skillswap.project.entity.ProjectMemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findByProject(Project project);

    Page<ProjectMember> findByProject(
            Project project,
            Pageable pageable
    );

    List<ProjectMember> findByProjectId(Long projectId);

    Page<ProjectMember> findByProjectId(
            Long projectId,
            Pageable pageable
    );

    List<ProjectMember> findByUser(User user);

    Page<ProjectMember> findByUser(
            User user,
            Pageable pageable
    );
List<ProjectMember> findByUserAndActiveTrue(User user);

    List<ProjectMember> findByUserAndStatus(
            User user,
            ProjectMemberStatus status
    );

    Page<ProjectMember> findByUserAndStatus(
            User user,
            ProjectMemberStatus status,
            Pageable pageable
    );

    List<ProjectMember> findByProjectAndStatus(
            Project project,
            ProjectMemberStatus status
    );

    Page<ProjectMember> findByProjectAndStatus(
            Project project,
            ProjectMemberStatus status,
            Pageable pageable
    );

    List<ProjectMember> findByProjectAndRole(
            Project project,
            ProjectMemberRole role
    );

    Optional<ProjectMember> findByProjectIdAndUserId(
            Long projectId,
            Long userId
    );

    Optional<ProjectMember> findByProjectAndUser(
            Project project,
            User user
    );

    boolean existsByProjectIdAndUserId(
            Long projectId,
            Long userId
    );

    boolean existsByProjectIdAndUserIdAndStatus(
            Long projectId,
            Long userId,
            ProjectMemberStatus status
    );

    long countByProject(Project project);

    long countByProjectId(Long projectId);

    long countByProjectAndStatus(
            Project project,
            ProjectMemberStatus status
    );

    long countByUser(User user);

    long countByUserAndStatus(
            User user,
            ProjectMemberStatus status
    );

    void deleteByProject(Project project);

    void deleteByProjectId(Long projectId);

    void deleteByUser(User user);
}