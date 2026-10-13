package com.skillswap.project.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.project.entity.Project;
import com.skillswap.project.entity.ProjectTask;
import com.skillswap.project.entity.ProjectTaskPriority;
import com.skillswap.project.entity.ProjectTaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    List<ProjectTask> findByProject(Project project);

    Page<ProjectTask> findByProject(
            Project project,
            Pageable pageable
    );

    List<ProjectTask> findByProjectId(Long projectId);

    Page<ProjectTask> findByProjectId(
            Long projectId,
            Pageable pageable
    );

    List<ProjectTask> findByProjectAndStatus(
            Project project,
            ProjectTaskStatus status
    );

    Page<ProjectTask> findByProjectAndStatus(
            Project project,
            ProjectTaskStatus status,
            Pageable pageable
    );

    List<ProjectTask> findByProjectAndPriority(
            Project project,
            ProjectTaskPriority priority
    );

    List<ProjectTask> findByAssigneeUser(User assigneeUser);

    Page<ProjectTask> findByAssigneeUser(
            User assigneeUser,
            Pageable pageable
    );

    List<ProjectTask> findByAssigneeUserAndStatus(
            User assigneeUser,
            ProjectTaskStatus status
    );

    Page<ProjectTask> findByAssigneeUserAndStatus(
            User assigneeUser,
            ProjectTaskStatus status,
            Pageable pageable
    );

    List<ProjectTask> findByProjectIdAndAssigneeUserId(
            Long projectId,
            Long assigneeUserId
    );

    List<ProjectTask> findByProjectIdAndDueDateBefore(
            Long projectId,
            LocalDate dueDate
    );

    List<ProjectTask> findByProjectIdAndDueDateBetween(
            Long projectId,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<ProjectTask> findByIdAndProjectId(
            Long id,
            Long projectId
    );

    Optional<ProjectTask> findByIdAndAssigneeUserId(
            Long id,
            Long assigneeUserId
    );

    long countByProject(Project project);

    long countByProjectAndStatus(
            Project project,
            ProjectTaskStatus status
    );

    long countByAssigneeUser(User assigneeUser);

    long countByAssigneeUserAndStatus(
            User assigneeUser,
            ProjectTaskStatus status
    );

    boolean existsByIdAndProjectId(
            Long id,
            Long projectId
    );

    void deleteByProject(Project project);

    void deleteByProjectId(Long projectId);

    void deleteByAssigneeUser(User assigneeUser);
}