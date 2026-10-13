package com.skillswap.learning.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.learning.dto.LearningPageResponse;
import com.skillswap.learning.dto.LearningProgressResponse;
import com.skillswap.learning.dto.UpdateLearningProgressRequest;
import com.skillswap.learning.entity.LearningPath;
import com.skillswap.learning.entity.LearningProgress;
import com.skillswap.learning.entity.LearningProgressStatus;
import com.skillswap.learning.entity.LearningResource;
import com.skillswap.learning.repository.LearningPathRepository;
import com.skillswap.learning.repository.LearningProgressRepository;
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
public class LearningProgressService {

    private final LearningProgressRepository learningProgressRepository;
    private final LearningPathRepository learningPathRepository;
    private final LearningResourceRepository learningResourceRepository;
    private final UserRepository userRepository;

    public LearningProgressService(
            LearningProgressRepository learningProgressRepository,
            LearningPathRepository learningPathRepository,
            LearningResourceRepository learningResourceRepository,
            UserRepository userRepository
    ) {
        this.learningProgressRepository = learningProgressRepository;
        this.learningPathRepository = learningPathRepository;
        this.learningResourceRepository = learningResourceRepository;
        this.userRepository = userRepository;
    }

    public LearningProgressResponse start(
            Long resourceId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        LearningResource resource =
                getResource(resourceId, user);

        LearningProgress progress =
                learningProgressRepository
                        .findByLearningResourceIdAndUserId(
                                resourceId,
                                user.getId()
                        )
                        .orElse(null);

        if (progress == null) {
            progress = new LearningProgress(
                    resource.getLearningPath(),
                    resource,
                    user
            );
        }

        progress.start();

        return LearningProgressResponse.from(
                learningProgressRepository.save(progress)
        );
    }

    public LearningProgressResponse update(
            Long resourceId,
            UpdateLearningProgressRequest request,
            String userEmail
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Learning progress request is required"
            );
        }

        request.normalize();

        if (request.isEmpty()) {
            throw new BadRequestException(
                    "No fields provided for progress update"
            );
        }

        User user = getUser(userEmail);

        LearningResource resource =
                getResource(resourceId, user);

        LearningProgress progress =
                getOrCreateProgress(resource, user);

        if (request.hasProgressUpdate()) {
            progress.updateProgress(
                    request.getProgressPercentage()
            );
        }

        if (request.hasTimeUpdate()) {
            progress.addTimeSpent(
                    request.getTimeSpentMinutes()
            );
        }

        if (request.hasNotesUpdate()) {
            progress.setNotes(request.getNotes());
        }

        if (request.marksCompleted()) {
            progress.complete();
            resource.markCompleted();
            learningResourceRepository.save(resource);
        }

        return LearningProgressResponse.from(
                learningProgressRepository.save(progress)
        );
    }

    public LearningProgressResponse complete(
            Long resourceId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        LearningResource resource =
                getResource(resourceId, user);

        LearningProgress progress =
                getOrCreateProgress(resource, user);

        progress.complete();

        resource.markCompleted();

        learningResourceRepository.save(resource);

        return LearningProgressResponse.from(
                learningProgressRepository.save(progress)
        );
    }

    public LearningProgressResponse reset(
            Long resourceId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        LearningResource resource =
                getResource(resourceId, user);

        LearningProgress progress =
                getOrCreateProgress(resource, user);

        progress.reset();

        resource.markIncomplete();

        learningResourceRepository.save(resource);

        return LearningProgressResponse.from(
                learningProgressRepository.save(progress)
        );
    }

    public LearningProgressResponse skip(
            Long resourceId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        LearningResource resource =
                getResource(resourceId, user);

        LearningProgress progress =
                getOrCreateProgress(resource, user);

        progress.skip();

        return LearningProgressResponse.from(
                learningProgressRepository.save(progress)
        );
    }

    @Transactional(readOnly = true)
    public LearningProgressResponse getByResource(
            Long resourceId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        getResource(resourceId, user);

        LearningProgress progress =
                learningProgressRepository
                        .findByLearningResourceIdAndUserId(
                                resourceId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Learning progress not found"
                                ));

        return LearningProgressResponse.from(progress);
    }

    @Transactional(readOnly = true)
    public LearningProgressResponse getByPathAndResource(
            Long pathId,
            Long resourceId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        learningPathRepository
                .findByIdAndUser(pathId, user)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Learning path not found"
                        ));

        LearningProgress progress =
                learningProgressRepository
                        .findByLearningPathIdAndLearningResourceIdAndUserId(
                                pathId,
                                resourceId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Learning progress not found"
                                ));

        return LearningProgressResponse.from(progress);
    }

    @Transactional(readOnly = true)
    public List<LearningProgressResponse> getMyProgress(
            String userEmail
    ) {
        User user = getUser(userEmail);

        return learningProgressRepository
                .findByUser(user)
                .stream()
                .map(LearningProgressResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public LearningPageResponse<LearningProgressResponse> getMyProgress(
            String userEmail,
            int page,
            int size
    ) {
        User user = getUser(userEmail);

        Pageable pageable = createPageable(page, size);

        Page<LearningProgress> result =
                learningProgressRepository.findByUser(
                        user,
                        pageable
                );

        return toPageResponse(result);
    }

    @Transactional(readOnly = true)
    public List<LearningProgressResponse> getMyProgressByStatus(
            String userEmail,
            LearningProgressStatus status
    ) {
        if (status == null) {
            throw new BadRequestException(
                    "Progress status is required"
            );
        }

        User user = getUser(userEmail);

        return learningProgressRepository
                .findByUserIdAndStatus(user.getId(), status)
                .stream()
                .map(LearningProgressResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public LearningPageResponse<LearningProgressResponse> getMyProgressByStatus(
            String userEmail,
            LearningProgressStatus status,
            int page,
            int size
    ) {
        if (status == null) {
            throw new BadRequestException(
                    "Progress status is required"
            );
        }

        User user = getUser(userEmail);

        Pageable pageable = createPageable(page, size);

        Page<LearningProgress> result =
                learningProgressRepository.findByUserIdAndStatus(
                        user.getId(),
                        status,
                        pageable
                );

        return toPageResponse(result);
    }

    @Transactional(readOnly = true)
    public List<LearningProgressResponse> getPathProgress(
            Long pathId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        learningPathRepository
                .findByIdAndUser(pathId, user)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Learning path not found"
                        ));

        return learningProgressRepository
                .findByLearningPathId(pathId)
                .stream()
                .map(LearningProgressResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countMyProgress(String userEmail) {
        User user = getUser(userEmail);

        return learningProgressRepository.countByUserId(
                user.getId()
        );
    }

    @Transactional(readOnly = true)
    public long countMyProgressByStatus(
            String userEmail,
            LearningProgressStatus status
    ) {
        if (status == null) {
            throw new BadRequestException(
                    "Progress status is required"
            );
        }

        User user = getUser(userEmail);

        return learningProgressRepository.countByUserIdAndStatus(
                user.getId(),
                status
        );
    }

    private LearningProgress getOrCreateProgress(
            LearningResource resource,
            User user
    ) {
        return learningProgressRepository
                .findByLearningResourceIdAndUserId(
                        resource.getId(),
                        user.getId()
                )
                .orElseGet(() ->
                        new LearningProgress(
                                resource.getLearningPath(),
                                resource,
                                user
                        ));
    }

    private LearningResource getResource(
            Long resourceId,
            User user
    ) {
        if (resourceId == null) {
            throw new BadRequestException(
                    "Learning resource id is required"
            );
        }

        return learningResourceRepository
                .findByIdAndUser(resourceId, user)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Learning resource not found"
                        ));
    }

    private User getUser(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new BadRequestException(
                    "User email is required"
            );
        }

        String normalizedEmail =
                userEmail.trim().toLowerCase(Locale.ROOT);

        return userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found"
                        ));
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
                        "updatedAt"
                )
        );
    }

    private LearningPageResponse<LearningProgressResponse> toPageResponse(
            Page<LearningProgress> page
    ) {
        List<LearningProgressResponse> content =
                page.getContent()
                        .stream()
                        .map(LearningProgressResponse::from)
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