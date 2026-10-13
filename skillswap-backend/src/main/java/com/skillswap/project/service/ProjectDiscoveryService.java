package com.skillswap.project.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.project.dto.ProjectPageResponse;
import com.skillswap.project.dto.ProjectSearchRequest;
import com.skillswap.project.dto.ProjectSummaryResponse;
import com.skillswap.project.entity.Project;
import com.skillswap.project.entity.ProjectStatus;
import com.skillswap.project.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProjectDiscoveryService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectDiscoveryService(
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public ProjectPageResponse search(
            ProjectSearchRequest request
    ) {
        request.normalize();

        int page = Math.max(request.getPage(), 0);
        int size = Math.min(
                Math.max(request.getSize(), 1),
                100
        );

        Pageable pageable = PageRequest.of(page, size);

        Page<Project> result;

        if (request.getOwnerUserId() != null) {
            result = searchByOwner(
                    request,
                    pageable
            );
        } else if (request.getSkillName() != null
                && !request.getSkillName().isBlank()) {

            result = searchBySkill(
                    request,
                    pageable
            );
        } else if (request.getCategory() != null
                && !request.getCategory().isBlank()) {

            result = searchByCategory(
                    request,
                    pageable
            );
        } else if (request.getStatus() != null) {

            result = projectRepository.findByStatus(
                    request.getStatus(),
                    pageable
            );
        } else {
            result = projectRepository.findByStatus(
                    ProjectStatus.OPEN,
                    pageable
            );
        }

        return toPageResponse(result);
    }

    public ProjectPageResponse searchBySkill(
            String skillName,
            int page,
            int size
    ) {
        if (skillName == null || skillName.isBlank()) {
            throw new BadRequestException(
                    "Skill name is required"
            );
        }

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100)
        );

        Page<Project> result =
                projectRepository.findBySkillNameIgnoreCase(
                        skillName.trim(),
                        pageable
                );

        return toPageResponse(result);
    }

    public ProjectPageResponse searchByCategory(
            String category,
            int page,
            int size
    ) {
        if (category == null || category.isBlank()) {
            throw new BadRequestException(
                    "Category is required"
            );
        }

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100)
        );

        Page<Project> result =
                projectRepository.findByCategoryIgnoreCase(
                        category.trim(),
                        pageable
                );

        return toPageResponse(result);
    }

    public ProjectPageResponse searchByStatus(
            ProjectStatus status,
            int page,
            int size
    ) {
        if (status == null) {
            throw new BadRequestException(
                    "Project status is required"
            );
        }

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100)
        );

        Page<Project> result =
                projectRepository.findByStatus(
                        status,
                        pageable
                );

        return toPageResponse(result);
    }

    public ProjectPageResponse searchByOwner(
            Long ownerUserId,
            int page,
            int size
    ) {
        if (ownerUserId == null) {
            throw new BadRequestException(
                    "Owner user ID is required"
            );
        }

        User owner = userRepository
                .findById(ownerUserId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found: " + ownerUserId
                        )
                );

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100)
        );

        Page<Project> result =
                projectRepository.findByOwnerUser(
                        owner,
                        pageable
                );

        return toPageResponse(result);
    }

    public List<ProjectSummaryResponse> getOpenProjects(
            int limit
    ) {
        int safeLimit = Math.min(
                Math.max(limit, 1),
                100
        );

        return projectRepository
                .findByStatus(ProjectStatus.OPEN)
                .stream()
                .limit(safeLimit)
                .map(ProjectSummaryResponse::from)
                .toList();
    }

    public List<ProjectSummaryResponse> getProjectsBySkill(
            String skillName
    ) {
        if (skillName == null || skillName.isBlank()) {
            throw new BadRequestException(
                    "Skill name is required"
            );
        }

        return projectRepository
                .findBySkillNameIgnoreCase(skillName.trim())
                .stream()
                .map(ProjectSummaryResponse::from)
                .toList();
    }

    public List<ProjectSummaryResponse> getProjectsByCategory(
            String category
    ) {
        if (category == null || category.isBlank()) {
            throw new BadRequestException(
                    "Category is required"
            );
        }

        return projectRepository
                .findByCategoryIgnoreCase(category.trim())
                .stream()
                .map(ProjectSummaryResponse::from)
                .toList();
    }

    public long countBySkill(
            String skillName
    ) {
        if (skillName == null || skillName.isBlank()) {
            throw new BadRequestException(
                    "Skill name is required"
            );
        }

        return projectRepository.countBySkillNameIgnoreCase(
                skillName.trim()
        );
    }

    private ProjectPageResponse toPageResponse(
            Page<Project> page
    ) {
        Page<ProjectSummaryResponse> responsePage =
                page.map(ProjectSummaryResponse::from);

        return ProjectPageResponse.from(responsePage);
    }

    private Page<Project> searchByOwner(
            ProjectSearchRequest request,
            Pageable pageable
    ) {
        User owner = userRepository
                .findById(request.getOwnerUserId())
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found: "
                                        + request.getOwnerUserId()
                        )
                );

        if (request.getStatus() != null) {
            return projectRepository.findByOwnerUserAndStatus(
                    owner,
                    request.getStatus(),
                    pageable
            );
        }

        return projectRepository.findByOwnerUser(
                owner,
                pageable
        );
    }

    private Page<Project> searchBySkill(
            ProjectSearchRequest request,
            Pageable pageable
    ) {
        return projectRepository.findBySkillNameIgnoreCase(
                request.getSkillName().trim(),
                pageable
        );
    }

    private Page<Project> searchByCategory(
            ProjectSearchRequest request,
            Pageable pageable
    ) {
        return projectRepository.findByCategoryIgnoreCase(
                request.getCategory().trim(),
                pageable
        );
    }
}