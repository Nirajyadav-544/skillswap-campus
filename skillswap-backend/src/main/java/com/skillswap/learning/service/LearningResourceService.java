package com.skillswap.learning.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.learning.dto.CreateLearningResourceRequest;
import com.skillswap.learning.dto.LearningPageResponse;
import com.skillswap.learning.dto.LearningResourceResponse;
import com.skillswap.learning.dto.UpdateLearningResourceRequest;
import com.skillswap.learning.entity.LearningPath;
import com.skillswap.learning.entity.LearningResource;
import com.skillswap.learning.entity.LearningResourceType;
import com.skillswap.learning.repository.LearningPathRepository;
import com.skillswap.learning.repository.LearningResourceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class LearningResourceService {

    private final LearningResourceRepository learningResourceRepository;
    private final LearningPathRepository learningPathRepository;
    private final UserRepository userRepository;

    public LearningResourceService(
            LearningResourceRepository learningResourceRepository,
            LearningPathRepository learningPathRepository,
            UserRepository userRepository
    ) {
        this.learningResourceRepository = learningResourceRepository;
        this.learningPathRepository = learningPathRepository;
        this.userRepository = userRepository;
    }

    public LearningResourceResponse create(
            CreateLearningResourceRequest request,
            String userEmail
    ) {
        if (request == null) {
            throw new BadRequestException("Learning resource request is required");
        }

        request.normalize();

        if (request.getLearningPathId() == null) {
            throw new BadRequestException("Learning path id is required");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BadRequestException("Learning resource title is required");
        }

        if (request.getResourceType() == null) {
            throw new BadRequestException("Resource type is required");
        }

        if (request.getUrl() == null || request.getUrl().isBlank()) {
            throw new BadRequestException("Resource URL is required");
        }

        User user = getUser(userEmail);

        LearningPath path = learningPathRepository
                .findByIdAndUser(request.getLearningPathId(), user)
                .orElseThrow(() ->
                        new BadRequestException("Learning path not found"));

        if (learningResourceRepository
                .existsByLearningPathIdAndTitleIgnoreCase(
                        path.getId(),
                        request.getTitle()
                )) {
            throw new BadRequestException(
                    "A resource with this title already exists in the learning path"
            );
        }

        LearningResource resource = new LearningResource(
                path,
                user,
                request.getTitle(),
                request.getResourceType(),
                request.getUrl()
        );

        resource.setDescription(request.getDescription());
        resource.setSkillName(request.getSkillName());
        resource.setStepNumber(request.getStepNumber());
        resource.setEstimatedMinutes(request.getEstimatedMinutes());
        resource.setRequired(request.isRequired());
        resource.setNotes(request.getNotes());

        resource.normalize();

        return LearningResourceResponse.from(
                learningResourceRepository.save(resource)
        );
    }

    @Transactional(readOnly = true)
    public LearningResourceResponse getById(
            Long resourceId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        LearningResource resource = learningResourceRepository
                .findByIdAndUser(resourceId, user)
                .orElseThrow(() ->
                        new BadRequestException("Learning resource not found"));

        return LearningResourceResponse.from(resource);
    }

    @Transactional(readOnly = true)
    public List<LearningResourceResponse> getByPath(
            Long pathId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        learningPathRepository
                .findByIdAndUser(pathId, user)
                .orElseThrow(() ->
                        new BadRequestException("Learning path not found"));

        return learningResourceRepository
                .findByLearningPathIdOrderByStepNumberAsc(pathId)
                .stream()
                .map(LearningResourceResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public LearningPageResponse<LearningResourceResponse> getByPath(
            Long pathId,
            String userEmail,
            int page,
            int size
    ) {
        User user = getUser(userEmail);

        learningPathRepository
                .findByIdAndUser(pathId, user)
                .orElseThrow(() ->
                        new BadRequestException("Learning path not found"));

        Pageable pageable = createPageable(page, size);

        Page<LearningResource> result =
                learningResourceRepository.findByLearningPathId(
                        pathId,
                        pageable
                );

        return toPageResponse(result);
    }

    @Transactional(readOnly = true)
    public List<LearningResourceResponse> getMyResources(
            String userEmail
    ) {
        User user = getUser(userEmail);

        return learningResourceRepository
                .findByUser(user)
                .stream()
                .map(LearningResourceResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LearningResourceResponse> getMyResourcesByType(
            String userEmail,
            LearningResourceType resourceType
    ) {
        if (resourceType == null) {
            throw new BadRequestException("Resource type is required");
        }

        User user = getUser(userEmail);

        return learningResourceRepository
                .findByUser(user)
                .stream()
                .filter(resource ->
                        resource.getResourceType() == resourceType
                )
                .map(LearningResourceResponse::from)
                .toList();
    }

    public LearningResourceResponse update(
            Long resourceId,
            UpdateLearningResourceRequest request,
            String userEmail
    ) {
        if (request == null) {
            throw new BadRequestException("Update request is required");
        }

        request.normalize();

        if (request.isEmpty()) {
            throw new BadRequestException("No fields provided for update");
        }

        User user = getUser(userEmail);

        LearningResource resource = learningResourceRepository
                .findByIdAndUser(resourceId, user)
                .orElseThrow(() ->
                        new BadRequestException("Learning resource not found"));

        if (request.hasBasicInformationUpdate()) {
            if (request.getTitle() != null) {
                resource.setTitle(request.getTitle());
            }

            if (request.getDescription() != null) {
                resource.setDescription(request.getDescription());
            }

            if (request.getResourceType() != null) {
                resource.setResourceType(request.getResourceType());
            }

            if (request.getUrl() != null) {
                resource.setUrl(request.getUrl());
            }

            if (request.getSkillName() != null) {
                resource.setSkillName(request.getSkillName());
            }

            if (request.getRequired() != null) {
                resource.setRequired(request.getRequired());
            }
        }

        if (request.hasScheduleUpdate()) {
            if (request.getStepNumber() != null) {
                resource.setStepNumber(request.getStepNumber());
            }

            if (request.getEstimatedMinutes() != null) {
                resource.setEstimatedMinutes(
                        request.getEstimatedMinutes()
                );
            }
        }

        if (request.hasCompletionUpdate()) {
            if (Boolean.TRUE.equals(request.getCompleted())) {
                resource.markCompleted();
            } else if (Boolean.FALSE.equals(request.getCompleted())) {
                resource.markIncomplete();
            }
        }

        if (request.hasNotesUpdate()) {
            resource.setNotes(request.getNotes());
        }

        resource.normalize();

        return LearningResourceResponse.from(
                learningResourceRepository.save(resource)
        );
    }

    public LearningResourceResponse markCompleted(
            Long resourceId,
            String userEmail
    ) {
        LearningResource resource =
                getOwnedResource(resourceId, userEmail);

        resource.markCompleted();

        return LearningResourceResponse.from(
                learningResourceRepository.save(resource)
        );
    }

    public LearningResourceResponse markIncomplete(
            Long resourceId,
            String userEmail
    ) {
        LearningResource resource =
                getOwnedResource(resourceId, userEmail);

        resource.markIncomplete();

        return LearningResourceResponse.from(
                learningResourceRepository.save(resource)
        );
    }

    public LearningResourceResponse toggleCompleted(
            Long resourceId,
            String userEmail
    ) {
        LearningResource resource =
                getOwnedResource(resourceId, userEmail);

        resource.toggleCompleted();

        return LearningResourceResponse.from(
                learningResourceRepository.save(resource)
        );
    }

    public void delete(
            Long resourceId,
            String userEmail
    ) {
        LearningResource resource =
                getOwnedResource(resourceId, userEmail);

        learningResourceRepository.delete(resource);
    }

    @Transactional(readOnly = true)
    public long countByPath(
            Long pathId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        learningPathRepository
                .findByIdAndUser(pathId, user)
                .orElseThrow(() ->
                        new BadRequestException("Learning path not found"));

        return learningResourceRepository.countByLearningPathId(pathId);
    }

    @Transactional(readOnly = true)
    public long countCompletedByPath(
            Long pathId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        learningPathRepository
                .findByIdAndUser(pathId, user)
                .orElseThrow(() ->
                        new BadRequestException("Learning path not found"));

        return learningResourceRepository
                .countByLearningPathIdAndCompletedTrue(pathId);
    }

    @Transactional(readOnly = true)
    public long countIncompleteByPath(
            Long pathId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        learningPathRepository
                .findByIdAndUser(pathId, user)
                .orElseThrow(() ->
                        new BadRequestException("Learning path not found"));

        return learningResourceRepository
                .countByLearningPathIdAndCompletedFalse(pathId);
    }

    private LearningResource getOwnedResource(
            Long resourceId,
            String userEmail
    ) {
        if (resourceId == null) {
            throw new BadRequestException("Learning resource id is required");
        }

        User user = getUser(userEmail);

        return learningResourceRepository
                .findByIdAndUser(resourceId, user)
                .orElseThrow(() ->
                        new BadRequestException("Learning resource not found"));
    }

    private User getUser(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new BadRequestException("User email is required");
        }

        String normalizedEmail =
                userEmail.trim().toLowerCase(Locale.ROOT);

        return userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new BadRequestException("User not found"));
    }

    private Pageable createPageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        return PageRequest.of(
                safePage,
                safeSize,
                Sort.by(
                        Sort.Direction.ASC,
                        "stepNumber"
                )
        );
    }

    private LearningPageResponse<LearningResourceResponse> toPageResponse(
            Page<LearningResource> page
    ) {
        List<LearningResourceResponse> content =
                page.getContent()
                        .stream()
                        .map(LearningResourceResponse::from)
                        .toList();

        return LearningPageResponse.of(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}