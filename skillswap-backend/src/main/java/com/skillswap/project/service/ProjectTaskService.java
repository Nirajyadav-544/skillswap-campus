package com.skillswap.project.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.project.dto.CreateProjectTaskRequest;
import com.skillswap.project.dto.ProjectTaskResponse;
import com.skillswap.project.dto.UpdateProjectTaskRequest;
import com.skillswap.project.entity.Project;
import com.skillswap.project.entity.ProjectMember;
import com.skillswap.project.entity.ProjectTask;
import com.skillswap.project.entity.ProjectTaskPriority;
import com.skillswap.project.repository.ProjectMemberRepository;
import com.skillswap.project.repository.ProjectRepository;
import com.skillswap.project.repository.ProjectTaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectTaskService {

    private final ProjectRepository projectRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public ProjectTaskService(
            ProjectRepository projectRepository,
            ProjectTaskRepository projectTaskRepository,
            ProjectMemberRepository projectMemberRepository,
            UserRepository userRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    public ProjectTaskResponse create(
            Long projectId,
            CreateProjectTaskRequest request,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateCanManageTasks(project, user);

        request.normalize();

        ProjectTask task = new ProjectTask(
                project,
                request.getTitle()
        );

        task.setDescription(request.getDescription());
        task.setPriority(
                request.getPriority() != null
                        ? request.getPriority()
                        : ProjectTaskPriority.MEDIUM
        );
        task.setDueDate(request.getDueDate());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setNotes(request.getNotes());

        if (request.getAssigneeUserId() != null) {
            User assignee = getUserById(request.getAssigneeUserId());

            validateProjectMember(project, assignee);

            task.setAssigneeUser(assignee);
        }

        return ProjectTaskResponse.from(
                projectTaskRepository.save(task)
        );
    }

    @Transactional(readOnly = true)
    public List<ProjectTaskResponse> getTasks(
            Long projectId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateParticipant(project, user);

        return projectTaskRepository
                .findByProject(project)
                .stream()
                .map(ProjectTaskResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProjectTaskResponse> getTasksPage(
            Long projectId,
            String userEmail,
            int page,
            int size
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateParticipant(project, user);

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100)
        );

        return projectTaskRepository
                .findByProject(project, pageable)
                .map(ProjectTaskResponse::from);
    }

    @Transactional(readOnly = true)
    public ProjectTaskResponse getById(
            Long projectId,
            Long taskId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateParticipant(project, user);

        ProjectTask task = getTask(projectId, taskId);

        return ProjectTaskResponse.from(task);
    }

    public ProjectTaskResponse update(
            Long projectId,
            Long taskId,
            UpdateProjectTaskRequest request,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateCanManageTasks(project, user);

        ProjectTask task = getTask(projectId, taskId);

        request.normalize();

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        if (request.getEstimatedHours() != null) {
            task.setEstimatedHours(request.getEstimatedHours());
        }

        if (request.getActualHours() != null) {
            task.setActualHours(request.getActualHours());
        }

        if (request.getNotes() != null) {
            task.setNotes(request.getNotes());
        }

        if (request.getAssigneeUserId() != null) {
            User assignee = getUserById(request.getAssigneeUserId());

            validateProjectMember(project, assignee);

            task.setAssigneeUser(assignee);
        }

        return ProjectTaskResponse.from(
                projectTaskRepository.save(task)
        );
    }

    public ProjectTaskResponse start(
            Long projectId,
            Long taskId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);
        ProjectTask task = getTask(projectId, taskId);

        validateTaskActor(project, task, user);

        task.start();

        return ProjectTaskResponse.from(
                projectTaskRepository.save(task)
        );
    }

    public ProjectTaskResponse block(
            Long projectId,
            Long taskId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);
        ProjectTask task = getTask(projectId, taskId);

        validateTaskActor(project, task, user);

        task.block();

        return ProjectTaskResponse.from(
                projectTaskRepository.save(task)
        );
    }

    public ProjectTaskResponse sendForReview(
            Long projectId,
            Long taskId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);
        ProjectTask task = getTask(projectId, taskId);

        validateTaskActor(project, task, user);

        task.sendForReview();

        return ProjectTaskResponse.from(
                projectTaskRepository.save(task)
        );
    }

    public ProjectTaskResponse complete(
            Long projectId,
            Long taskId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);
        ProjectTask task = getTask(projectId, taskId);

        validateTaskActor(project, task, user);

        task.complete();

        return ProjectTaskResponse.from(
                projectTaskRepository.save(task)
        );
    }

    public ProjectTaskResponse cancel(
            Long projectId,
            Long taskId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);
        ProjectTask task = getTask(projectId, taskId);

        validateCanManageTasks(project, user);

        task.cancel();

        return ProjectTaskResponse.from(
                projectTaskRepository.save(task)
        );
    }

    public ProjectTaskResponse reopen(
            Long projectId,
            Long taskId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);
        ProjectTask task = getTask(projectId, taskId);

        validateCanManageTasks(project, user);

        task.reopen();

        return ProjectTaskResponse.from(
                projectTaskRepository.save(task)
        );
    }

    public ProjectTaskResponse assign(
            Long projectId,
            Long taskId,
            Long assigneeUserId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateCanManageTasks(project, user);

        ProjectTask task = getTask(projectId, taskId);

        User assignee = getUserById(assigneeUserId);

        validateProjectMember(project, assignee);

        task.setAssigneeUser(assignee);

        return ProjectTaskResponse.from(
                projectTaskRepository.save(task)
        );
    }

    public void delete(
            Long projectId,
            Long taskId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateCanManageTasks(project, user);

        ProjectTask task = getTask(projectId, taskId);

        projectTaskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public List<ProjectTaskResponse> getMyAssignedTasks(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return projectTaskRepository
                .findByAssigneeUser(user)
                .stream()
                .map(ProjectTaskResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectTaskResponse> getProjectTasksByPriority(
            Long projectId,
            ProjectTaskPriority priority,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateParticipant(project, user);

        return projectTaskRepository
                .findByProjectAndPriority(project, priority)
                .stream()
                .map(ProjectTaskResponse::from)
                .toList();
    }

    private ProjectTask getTask(
            Long projectId,
            Long taskId
    ) {
        if (taskId == null) {
            throw new BadRequestException("Task ID is required");
        }

        return projectTaskRepository
                .findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Project task not found: " + taskId
                        )
                );
    }

    private Project getProject(Long projectId) {
        if (projectId == null) {
            throw new BadRequestException("Project ID is required");
        }

        return projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Project not found: " + projectId
                        )
                );
    }

    private User getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("User email is required");
        }

        String normalizedEmail = email.trim().toLowerCase();

        return userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found: " + normalizedEmail
                        )
                );
    }

    private User getUserById(Long userId) {
        if (userId == null) {
            throw new BadRequestException("User ID is required");
        }

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found: " + userId
                        )
                );
    }

    private void validateOwner(
            Project project,
            User user
    ) {
        if (!project.getOwnerUser().getId().equals(user.getId())) {
            throw new BadRequestException(
                    "Only the project owner can perform this action"
            );
        }
    }

    private void validateParticipant(
            Project project,
            User user
    ) {
        boolean owner =
                project.getOwnerUser().getId().equals(user.getId());

        boolean member =
                projectMemberRepository.existsByProjectIdAndUserId(
                        project.getId(),
                        user.getId()
                );

        if (!owner && !member) {
            throw new BadRequestException(
                    "You are not a participant in this project"
            );
        }
    }

    private void validateProjectMember(
            Project project,
            User user
    ) {
        boolean owner =
                project.getOwnerUser().getId().equals(user.getId());

        boolean member =
                projectMemberRepository.existsByProjectIdAndUserId(
                        project.getId(),
                        user.getId()
                );

        if (!owner && !member) {
            throw new BadRequestException(
                    "User is not a member of this project"
            );
        }
    }

    private void validateCanManageTasks(
            Project project,
            User user
    ) {
        validateParticipant(project, user);
    }

    private void validateTaskActor(
            Project project,
            ProjectTask task,
            User user
    ) {
        boolean owner =
                project.getOwnerUser().getId().equals(user.getId());

        boolean assignee =
                task.getAssigneeUser() != null
                        && task.getAssigneeUser()
                        .getId()
                        .equals(user.getId());

        boolean member =
                projectMemberRepository.existsByProjectIdAndUserId(
                        project.getId(),
                        user.getId()
                );

        if (!owner && !assignee && !member) {
            throw new BadRequestException(
                    "You are not authorized to modify this task"
            );
        }
    }
}