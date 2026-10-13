package com.skillswap.learning.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.learning.dto.CreateLearningPathRequest;
import com.skillswap.learning.dto.LearningPageResponse;
import com.skillswap.learning.dto.LearningPathResponse;
import com.skillswap.learning.dto.LearningPathSummaryResponse;
import com.skillswap.learning.dto.UpdateLearningPathRequest;
import com.skillswap.learning.entity.LearningPath;
import com.skillswap.learning.entity.LearningPathStatus;
import com.skillswap.learning.repository.LearningPathRepository;
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
public class LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final UserRepository userRepository;

    public LearningPathService(
            LearningPathRepository learningPathRepository,
            UserRepository userRepository
    ) {
        this.learningPathRepository = learningPathRepository;
        this.userRepository = userRepository;
    }

    public LearningPathResponse create(
            CreateLearningPathRequest request,
            String userEmail
    ) {
        if (request == null) {
            throw new BadRequestException("Learning path request is required");
        }

        request.normalize();

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BadRequestException("Learning path title is required");
        }

        if (request.getSkillName() == null || request.getSkillName().isBlank()) {
            throw new BadRequestException("Skill name is required");
        }

        User user = getUser(userEmail);

        LearningPath path = new LearningPath(
                user,
                request.getTitle(),
                request.getSkillName()
        );

        path.setDescription(request.getDescription());
        path.setTargetLevel(request.getTargetLevel());

        if (request.getEstimatedHours() != null) {
            path.setEstimatedHours(request.getEstimatedHours());
        }

        if (request.getTotalSteps() != null) {
            path.setTotalSteps(request.getTotalSteps());
        }

        path.setStartDate(request.getStartDate());
        path.setTargetCompletionDate(request.getTargetCompletionDate());

        if (request.getTotalSteps() != null && request.getTotalSteps() > 0) {
            path.setCurrentStep(0);
        }

        path.normalize();

        return LearningPathResponse.from(
                learningPathRepository.save(path)
        );
    }

    @Transactional(readOnly = true)
    public LearningPathResponse getById(
            Long pathId,
            String userEmail
    ) {
        if (pathId == null) {
            throw new BadRequestException("Learning path id is required");
        }

        User user = getUser(userEmail);

        LearningPath path = learningPathRepository
                .findByIdAndUser(pathId, user)
                .orElseThrow(() ->
                        new BadRequestException("Learning path not found"));

        return LearningPathResponse.from(path);
    }

    @Transactional(readOnly = true)
    public List<LearningPathSummaryResponse> getMyPaths(
            String userEmail
    ) {
        User user = getUser(userEmail);

        return learningPathRepository
                .findByUser(user)
                .stream()
                .map(LearningPathSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public LearningPageResponse<LearningPathSummaryResponse> getMyPaths(
            String userEmail,
            int page,
            int size
    ) {
        User user = getUser(userEmail);

        Pageable pageable = createPageable(page, size);

        Page<LearningPath> result =
                learningPathRepository.findByUser(user, pageable);

        return toPageResponse(result);
    }

    @Transactional(readOnly = true)
    public List<LearningPathSummaryResponse> getMyPathsByStatus(
            String userEmail,
            LearningPathStatus status
    ) {
        if (status == null) {
            throw new BadRequestException("Learning path status is required");
        }

        User user = getUser(userEmail);

        return learningPathRepository
                .findByUserAndStatus(user, status)
                .stream()
                .map(LearningPathSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public LearningPageResponse<LearningPathSummaryResponse> getMyPathsByStatus(
            String userEmail,
            LearningPathStatus status,
            int page,
            int size
    ) {
        if (status == null) {
            throw new BadRequestException("Learning path status is required");
        }

        User user = getUser(userEmail);

        Pageable pageable = createPageable(page, size);

        Page<LearningPath> result =
                learningPathRepository.findByUserAndStatus(
                        user,
                        status,
                        pageable
                );

        return toPageResponse(result);
    }

    public LearningPathResponse update(
            Long pathId,
            UpdateLearningPathRequest request,
            String userEmail
    ) {
        if (pathId == null) {
            throw new BadRequestException("Learning path id is required");
        }

        if (request == null) {
            throw new BadRequestException("Update request is required");
        }

        request.normalize();

        if (request.isEmpty()) {
            throw new BadRequestException("No fields provided for update");
        }

        User user = getUser(userEmail);

        LearningPath path = learningPathRepository
                .findByIdAndUser(pathId, user)
                .orElseThrow(() ->
                        new BadRequestException("Learning path not found"));

        if (request.hasBasicInformationUpdate()) {
            if (request.getTitle() != null) {
                path.setTitle(request.getTitle());
            }

            if (request.getDescription() != null) {
                path.setDescription(request.getDescription());
            }

            if (request.getSkillName() != null) {
                path.setSkillName(request.getSkillName());
            }

            if (request.getTargetLevel() != null) {
                path.setTargetLevel(request.getTargetLevel());
            }

            if (request.getEstimatedHours() != null) {
                path.setEstimatedHours(request.getEstimatedHours());
            }

            if (request.getTotalSteps() != null) {
                path.setTotalSteps(request.getTotalSteps());
            }
        }

        if (request.hasStepUpdate()) {
            if (request.getCurrentStep() != null) {
                path.setCurrentStep(request.getCurrentStep());
            }

            if (request.getTotalSteps() != null) {
                path.setTotalSteps(request.getTotalSteps());
            }
        }

        if (request.hasScheduleUpdate()) {
            if (request.getStartDate() != null) {
                path.setStartDate(request.getStartDate());
            }

            if (request.getTargetCompletionDate() != null) {
                path.setTargetCompletionDate(
                        request.getTargetCompletionDate()
                );
            }
        }

        path.normalize();

        return LearningPathResponse.from(
                learningPathRepository.save(path)
        );
    }

    public LearningPathResponse activate(
            Long pathId,
            String userEmail
    ) {
        LearningPath path = getOwnedPath(pathId, userEmail);

        path.activate();

        return LearningPathResponse.from(
                learningPathRepository.save(path)
        );
    }

    public LearningPathResponse pause(
            Long pathId,
            String userEmail
    ) {
        LearningPath path = getOwnedPath(pathId, userEmail);

        path.pause();

        return LearningPathResponse.from(
                learningPathRepository.save(path)
        );
    }

    public LearningPathResponse resume(
            Long pathId,
            String userEmail
    ) {
        LearningPath path = getOwnedPath(pathId, userEmail);

        path.resume();

        return LearningPathResponse.from(
                learningPathRepository.save(path)
        );
    }

    public LearningPathResponse complete(
            Long pathId,
            String userEmail
    ) {
        LearningPath path = getOwnedPath(pathId, userEmail);

        path.complete();

        return LearningPathResponse.from(
                learningPathRepository.save(path)
        );
    }

    public LearningPathResponse archive(
            Long pathId,
            String userEmail
    ) {
        LearningPath path = getOwnedPath(pathId, userEmail);

        path.archive();

        return LearningPathResponse.from(
                learningPathRepository.save(path)
        );
    }

    public LearningPathResponse advanceStep(
            Long pathId,
            String userEmail
    ) {
        LearningPath path = getOwnedPath(pathId, userEmail);

        path.advanceStep();

        return LearningPathResponse.from(
                learningPathRepository.save(path)
        );
    }

    public void delete(
            Long pathId,
            String userEmail
    ) {
        LearningPath path = getOwnedPath(pathId, userEmail);

        learningPathRepository.delete(path);
    }

    @Transactional(readOnly = true)
    public long countMyPaths(
            String userEmail
    ) {
        User user = getUser(userEmail);

        return learningPathRepository.countByUser(user);
    }

    @Transactional(readOnly = true)
    public long countMyPathsByStatus(
            String userEmail,
            LearningPathStatus status
    ) {
        if (status == null) {
            throw new BadRequestException("Learning path status is required");
        }

        User user = getUser(userEmail);

        return learningPathRepository.countByUserIdAndStatus(
                user.getId(),
                status
        );
    }

    private LearningPath getOwnedPath(
            Long pathId,
            String userEmail
    ) {
        if (pathId == null) {
            throw new BadRequestException("Learning path id is required");
        }

        User user = getUser(userEmail);

        return learningPathRepository
                .findByIdAndUser(pathId, user)
                .orElseThrow(() ->
                        new BadRequestException("Learning path not found"));
    }

    private User getUser(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new BadRequestException("User email is required");
        }

        String normalizedEmail = userEmail
                .trim()
                .toLowerCase(Locale.ROOT);

        return userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new BadRequestException("User not found"));
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

    private LearningPageResponse<LearningPathSummaryResponse> toPageResponse(
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
}