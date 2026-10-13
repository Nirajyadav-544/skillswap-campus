package com.skillswap.learning.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.learning.dto.LearningPageResponse;
import com.skillswap.learning.dto.LearningPathSummaryResponse;
import com.skillswap.learning.dto.LearningResourceResponse;
import com.skillswap.learning.entity.LearningPath;
import com.skillswap.learning.entity.LearningPathStatus;
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

@Service
@Transactional(readOnly = true)
public class LearningDiscoveryService {

    private final LearningPathRepository learningPathRepository;
    private final LearningResourceRepository learningResourceRepository;

    public LearningDiscoveryService(
            LearningPathRepository learningPathRepository,
            LearningResourceRepository learningResourceRepository
    ) {
        this.learningPathRepository = learningPathRepository;
        this.learningResourceRepository = learningResourceRepository;
    }

    public List<LearningPathSummaryResponse> searchPaths(
            String skillName
    ) {
        if (skillName == null || skillName.isBlank()) {
            throw new BadRequestException(
                    "Skill name is required"
            );
        }

        return learningPathRepository
                .findBySkillNameContainingIgnoreCase(
                        skillName.trim()
                )
                .stream()
                .map(LearningPathSummaryResponse::from)
                .toList();
    }

    public LearningPageResponse<LearningPathSummaryResponse> searchPaths(
            String skillName,
            int page,
            int size
    ) {
        if (skillName == null || skillName.isBlank()) {
            throw new BadRequestException(
                    "Skill name is required"
            );
        }

        Pageable pageable = createPageable(page, size);

        Page<LearningPath> result =
                learningPathRepository
                        .findBySkillNameContainingIgnoreCase(
                                skillName.trim(),
                                pageable
                        );

        return toPathPage(result);
    }

    public List<LearningPathSummaryResponse> getActivePaths() {
        return learningPathRepository
                .findByStatus(LearningPathStatus.ACTIVE)
                .stream()
                .map(LearningPathSummaryResponse::from)
                .toList();
    }

    public LearningPageResponse<LearningPathSummaryResponse> getActivePaths(
            int page,
            int size
    ) {
        Pageable pageable = createPageable(page, size);

        Page<LearningPath> result =
                learningPathRepository.findByStatus(
                        LearningPathStatus.ACTIVE,
                        pageable
                );

        return toPathPage(result);
    }

    public List<LearningResourceResponse> searchResources(
            String skillName
    ) {
        if (skillName == null || skillName.isBlank()) {
            throw new BadRequestException(
                    "Skill name is required"
            );
        }

        return learningResourceRepository
                .findBySkillNameContainingIgnoreCase(
                        skillName.trim()
                )
                .stream()
                .map(LearningResourceResponse::from)
                .toList();
    }

    public LearningPageResponse<LearningResourceResponse> searchResources(
            String skillName,
            int page,
            int size
    ) {
        if (skillName == null || skillName.isBlank()) {
            throw new BadRequestException(
                    "Skill name is required"
            );
        }

        Pageable pageable = createPageable(page, size);

        Page<LearningResource> result =
                learningResourceRepository
                        .findBySkillNameContainingIgnoreCase(
                                skillName.trim(),
                                pageable
                        );

        return toResourcePage(result);
    }

    public List<LearningResourceResponse> getResourcesByType(
            LearningResourceType resourceType
    ) {
        if (resourceType == null) {
            throw new BadRequestException(
                    "Resource type is required"
            );
        }

        return learningResourceRepository
                .findByResourceType(resourceType)
                .stream()
                .map(LearningResourceResponse::from)
                .toList();
    }

    public LearningPageResponse<LearningResourceResponse> getResourcesByType(
            LearningResourceType resourceType,
            int page,
            int size
    ) {
        if (resourceType == null) {
            throw new BadRequestException(
                    "Resource type is required"
            );
        }

        Pageable pageable = createPageable(page, size);

        Page<LearningResource> result =
                learningResourceRepository.findByResourceType(
                        resourceType,
                        pageable
                );

        return toResourcePage(result);
    }

    public List<LearningResourceResponse> getIncompleteResources(
            Long pathId
    ) {
        if (pathId == null) {
            throw new BadRequestException(
                    "Learning path id is required"
            );
        }

        return learningResourceRepository
                .findByLearningPathIdAndCompletedFalse(pathId)
                .stream()
                .map(LearningResourceResponse::from)
                .toList();
    }

    public List<LearningResourceResponse> getRequiredIncompleteResources(
            Long pathId
    ) {
        if (pathId == null) {
            throw new BadRequestException(
                    "Learning path id is required"
            );
        }

        return learningResourceRepository
                .findByLearningPathIdAndRequiredTrueAndCompletedFalse(
                        pathId
                )
                .stream()
                .map(LearningResourceResponse::from)
                .toList();
    }

    public List<LearningResourceResponse> getRequiredResources(
            Long pathId
    ) {
        if (pathId == null) {
            throw new BadRequestException(
                    "Learning path id is required"
            );
        }

        return learningResourceRepository
                .findByLearningPathIdAndRequiredTrue(pathId)
                .stream()
                .map(LearningResourceResponse::from)
                .toList();
    }

    private Pageable createPageable(
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        return PageRequest.of(
                safePage,
                safeSize,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );
    }

    private LearningPageResponse<LearningPathSummaryResponse> toPathPage(
            Page<LearningPath> page
    ) {
        List<LearningPathSummaryResponse> content =
                page.getContent()
                        .stream()
                        .map(LearningPathSummaryResponse::from)
                        .toList();

        return LearningPageResponse.of(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private LearningPageResponse<LearningResourceResponse> toResourcePage(
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