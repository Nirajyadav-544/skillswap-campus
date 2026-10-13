package com.skillswap.project.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.project.dto.CreateProjectRequest;
import com.skillswap.project.dto.ProjectResponse;
import com.skillswap.project.dto.ProjectSummaryResponse;
import com.skillswap.project.dto.UpdateProjectRequest;
import com.skillswap.project.entity.Project;
import com.skillswap.project.entity.ProjectStatus;
import com.skillswap.project.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public ProjectResponse create(
            CreateProjectRequest request,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        request.normalize();

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BadRequestException("Project title is required");
        }

        Project project = new Project(
                user,
                request.getTitle(),
                request.getDescription()
        );

        project.setSkillName(request.getSkillName());
        project.setCategory(request.getCategory());
        project.setMaxMembers(request.getMaxMembers());
        project.setStartDate(request.getStartDate());
        project.setTargetEndDate(request.getTargetEndDate());
        project.setRequirements(request.getRequirements());
        project.setGoals(request.getGoals());
        project.setGithubUrl(request.getGithubUrl());
        project.setDemoUrl(request.getDemoUrl());
        project.setProjectUrl(request.getProjectUrl());

        if (project.getMaxMembers() == null || project.getMaxMembers() < 1) {
            project.setMaxMembers(10);
        }

        Project saved = projectRepository.save(project);

        return ProjectResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(
            Long projectId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new BadRequestException("Project not found: " + projectId)
                );

        return ProjectResponse.from(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryResponse> getMyProjects(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return projectRepository
                .findByOwnerUser(user)
                .stream()
                .map(ProjectSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryResponse> getMyProjectsByStatus(
            String userEmail,
            ProjectStatus status
    ) {
        User user = getUserByEmail(userEmail);

        return projectRepository
                .findByOwnerUserAndStatus(user, status)
                .stream()
                .map(ProjectSummaryResponse::from)
                .toList();
    }

    public ProjectResponse update(
            Long projectId,
            UpdateProjectRequest request,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        Project project = getOwnedProject(projectId, user);

        request.normalize();

        if (request.getTitle() != null) {
            project.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }

        if (request.getSkillName() != null) {
            project.setSkillName(request.getSkillName());
        }

        if (request.getCategory() != null) {
            project.setCategory(request.getCategory());
        }

        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }

        if (request.getMaxMembers() != null) {
            if (request.getMaxMembers() < project.getCurrentMembers()) {
                throw new BadRequestException(
                        "Max members cannot be less than current members"
                );
            }

            project.setMaxMembers(request.getMaxMembers());
        }

        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }

        if (request.getTargetEndDate() != null) {
            project.setTargetEndDate(request.getTargetEndDate());
        }

        if (request.getRequirements() != null) {
            project.setRequirements(request.getRequirements());
        }

        if (request.getGoals() != null) {
            project.setGoals(request.getGoals());
        }

        if (request.getGithubUrl() != null) {
            project.setGithubUrl(request.getGithubUrl());
        }

        if (request.getDemoUrl() != null) {
            project.setDemoUrl(request.getDemoUrl());
        }

        if (request.getProjectUrl() != null) {
            project.setProjectUrl(request.getProjectUrl());
        }

        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse open(
            Long projectId,
            String userEmail
    ) {
        Project project = getOwnedProjectByEmail(projectId, userEmail);

        project.open();

        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse start(
            Long projectId,
            String userEmail
    ) {
        Project project = getOwnedProjectByEmail(projectId, userEmail);

        project.start();

        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse putOnHold(
            Long projectId,
            String userEmail
    ) {
        Project project = getOwnedProjectByEmail(projectId, userEmail);

        project.putOnHold();

        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse resume(
            Long projectId,
            String userEmail
    ) {
        Project project = getOwnedProjectByEmail(projectId, userEmail);

        project.resume();

        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse complete(
            Long projectId,
            String userEmail
    ) {
        Project project = getOwnedProjectByEmail(projectId, userEmail);

        project.complete();

        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse cancel(
            Long projectId,
            String userEmail
    ) {
        Project project = getOwnedProjectByEmail(projectId, userEmail);

        project.cancel();

        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse archive(
            Long projectId,
            String userEmail
    ) {
        Project project = getOwnedProjectByEmail(projectId, userEmail);

        project.archive();

        return ProjectResponse.from(projectRepository.save(project));
    }

    public void delete(
            Long projectId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        Project project = getOwnedProject(projectId, user);

        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectSummaryResponse> getMyProjectsPage(
            String userEmail,
            int page,
            int size
    ) {
        User user = getUserByEmail(userEmail);

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100)
        );

        return projectRepository
                .findByOwnerUser(user, pageable)
                .map(ProjectSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public long countMyProjects(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return projectRepository.countByOwnerUser(user);
    }

    @Transactional(readOnly = true)
    public long countMyProjectsByStatus(
            String userEmail,
            ProjectStatus status
    ) {
        User user = getUserByEmail(userEmail);

        return projectRepository.countByOwnerUserAndStatus(
                user,
                status
        );
    }

    private User getUserByEmail(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new BadRequestException("User email is required");
        }

        String normalizedEmail = userEmail.trim().toLowerCase();

        return userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found: " + normalizedEmail
                        )
                );
    }

    private Project getOwnedProjectByEmail(
            Long projectId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return getOwnedProject(projectId, user);
    }

    private Project getOwnedProject(
            Long projectId,
            User user
    ) {
        if (projectId == null) {
            throw new BadRequestException("Project ID is required");
        }

        return projectRepository
                .findByIdAndOwnerUser(projectId, user)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Project not found or you are not the owner: "
                                        + projectId
                        )
                );
    }
}