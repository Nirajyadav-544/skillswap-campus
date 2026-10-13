package com.skillswap.project.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.project.dto.AddProjectMemberRequest;
import com.skillswap.project.dto.ProjectMemberResponse;
import com.skillswap.project.entity.Project;
import com.skillswap.project.entity.ProjectMember;
import com.skillswap.project.entity.ProjectMemberRole;
import com.skillswap.project.repository.ProjectMemberRepository;
import com.skillswap.project.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectMemberService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public ProjectMemberService(
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            UserRepository userRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    public ProjectMemberResponse addMember(
            Long projectId,
            AddProjectMemberRequest request,
            String userEmail
    ) {
        User owner = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateOwner(project, owner);

        if (request == null) {
            throw new BadRequestException("Member request is required");
        }

        request.normalize();

        if (request.getUserId() == null) {
            throw new BadRequestException("User ID is required");
        }

        User memberUser = userRepository
                .findById(request.getUserId())
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found: " + request.getUserId()
                        )
                );

        if (projectMemberRepository.existsByProjectIdAndUserId(
                projectId,
                memberUser.getId()
        )) {
            throw new BadRequestException(
                    "User is already a member of this project"
            );
        }

        if (!project.canAcceptMoreMembers()) {
            throw new BadRequestException(
                    "Project has reached its maximum member limit"
            );
        }

        if (project.getOwnerUser().getId().equals(memberUser.getId())) {
            throw new BadRequestException(
                    "Project owner is already a member of the project"
            );

            
        }

        ProjectMember member = new ProjectMember(
                project,
                memberUser
        );

        if (request.getRole() != null) {
            member.setRole(request.getRole());
        } else {
            member.setRole(ProjectMemberRole.CONTRIBUTOR);
        }

        member.setContribution(request.getContribution());

        ProjectMember saved = projectMemberRepository.save(member);

        return ProjectMemberResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getMembers(
            Long projectId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateParticipant(project, user);

        return projectMemberRepository
                .findByProject(project)
                .stream()
                .map(ProjectMemberResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProjectMemberResponse> getMembersPage(
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

        return projectMemberRepository
                .findByProject(project, pageable)
                .map(ProjectMemberResponse::from);
    }

    @Transactional(readOnly = true)
    public ProjectMemberResponse getMember(
            Long projectId,
            Long memberId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateParticipant(project, user);

        ProjectMember member = getMemberEntity(projectId, memberId);

        return ProjectMemberResponse.from(member);
    }

    public ProjectMemberResponse activate(
            Long projectId,
            Long memberId,
            String userEmail
    ) {
        User owner = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateOwner(project, owner);

        ProjectMember member = getMemberEntity(projectId, memberId);

        if (!member.isActive()) {
            if (!project.canAcceptMoreMembers()) {
                throw new BadRequestException(
                        "Project has reached its maximum member limit"
                );
            }

            member.activate();
            project.incrementMemberCount();
            projectRepository.save(project);
        }

        ProjectMember saved = projectMemberRepository.save(member);

        return ProjectMemberResponse.from(saved);
    }

    public ProjectMemberResponse remove(
            Long projectId,
            Long memberId,
            String userEmail
    ) {
        User owner = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateOwner(project, owner);

        ProjectMember member = getMemberEntity(projectId, memberId);

        if (member.getUser().getId().equals(
                project.getOwnerUser().getId()
        )) {
            throw new BadRequestException(
                    "Project owner cannot be removed"
            );
        }

        boolean wasActive = member.isActive();

        member.remove();

        ProjectMember saved = projectMemberRepository.save(member);

        if (wasActive) {
            project.decrementMemberCount();
            projectRepository.save(project);
        }

        return ProjectMemberResponse.from(saved);
    }

    public ProjectMemberResponse leave(
            Long projectId,
            Long memberId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        ProjectMember member = getMemberEntity(projectId, memberId);

        if (!member.getUser().getId().equals(user.getId())) {
            throw new BadRequestException(
                    "You can only leave your own project membership"
            );
        }

        if (member.getUser().getId().equals(
                project.getOwnerUser().getId()
        )) {
            throw new BadRequestException(
                    "Project owner cannot leave the project"
            );
        }

        boolean wasActive = member.isActive();

        member.leave();

        ProjectMember saved = projectMemberRepository.save(member);

        if (wasActive) {
            project.decrementMemberCount();
            projectRepository.save(project);
        }

        return ProjectMemberResponse.from(saved);
    }

    public void delete(
            Long projectId,
            Long memberId,
            String userEmail
    ) {
        User owner = getUserByEmail(userEmail);
        Project project = getProject(projectId);

        validateOwner(project, owner);

        ProjectMember member = getMemberEntity(projectId, memberId);

        if (member.getUser().getId().equals(
                project.getOwnerUser().getId()
        )) {
            throw new BadRequestException(
                    "Project owner cannot be deleted from the project"
            );
        }

        boolean wasActive = member.isActive();

        projectMemberRepository.delete(member);

        if (wasActive) {
            project.decrementMemberCount();
            projectRepository.save(project);
        }
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getMyMemberships(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return projectMemberRepository
                .findByUser(user)
                .stream()
                .map(ProjectMemberResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getMyActiveMemberships(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return projectMemberRepository
                .findByUserAndActiveTrue(user)
                .stream()
                .map(ProjectMemberResponse::from)
                .toList();
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

    private ProjectMember getMemberEntity(
            Long projectId,
            Long memberId
    ) {
        if (memberId == null) {
            throw new BadRequestException("Member ID is required");
        }

        return projectMemberRepository
                .findByProjectIdAndUserId(projectId, memberId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Project member not found: " + memberId
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
        boolean isOwner =
                project.getOwnerUser().getId().equals(user.getId());

        boolean isMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        project.getId(),
                        user.getId()
                );

        if (!isOwner && !isMember) {
            throw new BadRequestException(
                    "You are not a participant in this project"
            );
        }
    }
}