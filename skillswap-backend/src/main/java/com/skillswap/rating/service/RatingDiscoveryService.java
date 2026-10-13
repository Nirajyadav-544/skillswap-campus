package com.skillswap.rating.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.rating.dto.RatingPageResponse;
import com.skillswap.rating.dto.RatingResponse;
import com.skillswap.rating.dto.RatingSearchRequest;
import com.skillswap.rating.dto.UserRatingSummaryResponse;
import com.skillswap.rating.entity.Rating;
import com.skillswap.rating.entity.RatingCategory;
import com.skillswap.rating.entity.RatingStatus;
import com.skillswap.rating.repository.RatingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class RatingDiscoveryService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    public RatingDiscoveryService(
            RatingRepository ratingRepository,
            UserRepository userRepository
    ) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    public RatingPageResponse search(
            RatingSearchRequest request,
            String userEmail
    ) {
        if (request == null) {
            request = new RatingSearchRequest();
        }

        request.normalize();

        User currentUser = getUser(userEmail);

        Pageable pageable = buildPageable(request);

        Page<Rating> page;

        if (request.getRatedUserId() != null) {

            page = ratingRepository.findByRatedUserIdAndStatus(
                    request.getRatedUserId(),
                    RatingStatus.ACTIVE,
                    pageable
            );

        } else if (request.getRaterUserId() != null) {

            page = ratingRepository.findByRaterUserId(
                    request.getRaterUserId(),
                    pageable
            );

        } else if (request.getSessionId() != null) {

            page = ratingRepository.findBySessionId(
                    request.getSessionId(),
                    pageable
            );

        } else if (request.getCategory() != null) {

            List<Rating> ratings =
                    ratingRepository.findByCategory(
                            request.getCategory()
                    );

            ratings = ratings.stream()
                    .filter(this::isActive)
                    .toList();

            page = toPage(ratings, pageable);

        } else if (request.getKeyword() != null
                && !request.getKeyword().isBlank()) {

            page = ratingRepository.searchUserRatings(
                    currentUser.getId(),
                    RatingStatus.ACTIVE,
                    request.getKeyword(),
                    pageable
            );

        } else {

            page = ratingRepository.findByStatus(
                    RatingStatus.ACTIVE,
                    pageable
            );
        }

        List<Rating> filtered = page.getContent();

        filtered = applyFilters(filtered, request);

        if (filtered.size() != page.getContent().size()) {
            page = toPage(filtered, pageable);
        }

        return RatingPageResponse.from(page);
    }

    /**
     * Backward-compatible overload.
     *
     * Existing controller calls this version with:
     * userId, page, size, sortBy
     */
    public RatingPageResponse getUserRatings(
            Long userId,
            int page,
            int size,
            String sortBy
    ) {
        return getUserRatings(
                userId,
                page,
                size,
                sortBy,
                "desc"
        );
    }

    public RatingPageResponse getUserRatings(
            Long userId,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        validateUserId(userId);

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(
                Math.max(size, 1),
                100
        );

        Sort.Direction direction =
                "asc".equalsIgnoreCase(sortDirection)
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        String safeSortBy =
                normalizeSortField(sortBy);

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(direction, safeSortBy)
        );

        Page<Rating> result =
                ratingRepository.findByRatedUserIdAndStatus(
                        userId,
                        RatingStatus.ACTIVE,
                        pageable
                );

        return RatingPageResponse.from(result);
    }

    public RatingPageResponse getRatingsGivenByUser(
            Long userId,
            int page,
            int size
    ) {
        validateUserId(userId);

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(
                Math.max(size, 1),
                100
        );

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        Page<Rating> result =
                ratingRepository.findByRaterUserId(
                        userId,
                        pageable
                );

        return RatingPageResponse.from(result);
    }

    public RatingPageResponse getSessionRatings(
            Long sessionId,
            int page,
            int size
    ) {
        if (sessionId == null || sessionId <= 0) {
            throw new BadRequestException(
                    "Session ID must be valid"
            );
        }

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(
                Math.max(size, 1),
                100
        );

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        Page<Rating> result =
                ratingRepository.findBySessionId(
                        sessionId,
                        pageable
                );

        return RatingPageResponse.from(result);
    }

    public List<RatingResponse> getRecentRatingsForUser(
            Long userId,
            int limit
    ) {
        validateUserId(userId);

        int safeLimit = Math.min(
                Math.max(limit, 1),
                50
        );

        Pageable pageable = PageRequest.of(
                0,
                safeLimit,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        return ratingRepository
                .findByRatedUserIdAndStatus(
                        userId,
                        RatingStatus.ACTIVE,
                        pageable
                )
                .getContent()
                .stream()
                .map(RatingResponse::from)
                .toList();
    }

    public List<RatingResponse> getTopRatingsForUser(
            Long userId,
            int limit
    ) {
        validateUserId(userId);

        int safeLimit = Math.min(
                Math.max(limit, 1),
                50
        );

        Pageable pageable = PageRequest.of(
                0,
                safeLimit,
                Sort.by(
                        Sort.Direction.DESC,
                        "score"
                ).and(
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                )
        );

        return ratingRepository
                .findByRatedUserIdAndStatus(
                        userId,
                        RatingStatus.ACTIVE,
                        pageable
                )
                .getContent()
                .stream()
                .map(RatingResponse::from)
                .toList();
    }

    public List<RatingResponse> getRatingsByCategory(
            RatingCategory category,
            int limit
    ) {
        if (category == null) {
            throw new BadRequestException(
                    "Rating category is required"
            );
        }

        int safeLimit = Math.min(
                Math.max(limit, 1),
                100
        );

        return ratingRepository
                .findByCategory(category)
                .stream()
                .filter(this::isActive)
                .sorted(
                        Comparator.comparing(
                                Rating::getCreatedAt,
                                Comparator.nullsLast(
                                        Comparator.reverseOrder()
                                )
                        )
                )
                .limit(safeLimit)
                .map(RatingResponse::from)
                .toList();
    }

    public RatingPageResponse getUserRatingsByScore(
            Long userId,
            Integer minimumScore,
            Integer maximumScore,
            int page,
            int size
    ) {
        validateUserId(userId);

        int safeMinimum =
                minimumScore == null
                        ? 1
                        : minimumScore;

        int safeMaximum =
                maximumScore == null
                        ? 5
                        : maximumScore;

        if (safeMinimum < 1) {
            safeMinimum = 1;
        }

        if (safeMaximum > 5) {
            safeMaximum = 5;
        }

        if (safeMinimum > safeMaximum) {
            throw new BadRequestException(
                    "Minimum score cannot be greater than maximum score"
            );
        }

        int safePage = Math.max(page, 0);

        int safeSize = Math.min(
                Math.max(size, 1),
                100
        );

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        List<Rating> allRatings =
                ratingRepository.findByRatedUserIdAndStatus(
                        userId,
                        RatingStatus.ACTIVE
                );

        final int minScore = safeMinimum;
        final int maxScore = safeMaximum;

        List<Rating> filtered =
                allRatings.stream()
                        .filter(this::isActive)
                        .filter(rating ->
                                rating.getScore() != null
                                        && rating.getScore() >= minScore
                                        && rating.getScore() <= maxScore
                        )
                        .toList();

        return RatingPageResponse.from(
                toPage(filtered, pageable)
        );
    }

    public UserRatingSummaryResponse getUserRatingSummary(
            Long userId
    ) {
        validateUserId(userId);

        long totalRatings =
                ratingRepository.countByRatedUserId(userId);

        Double averageScore =
                ratingRepository.calculateAverageScore(
                        userId,
                        RatingStatus.ACTIVE
                );

        if (averageScore == null) {
            averageScore = 0.0;
        }

        List<Rating> ratings =
                ratingRepository.findByRatedUserIdAndStatus(
                        userId,
                        RatingStatus.ACTIVE
                );

        long oneStar = ratings.stream()
                .filter(r ->
                        r.getScore() != null
                                && r.getScore() == 1
                )
                .count();

        long twoStar = ratings.stream()
                .filter(r ->
                        r.getScore() != null
                                && r.getScore() == 2
                )
                .count();

        long threeStar = ratings.stream()
                .filter(r ->
                        r.getScore() != null
                                && r.getScore() == 3
                )
                .count();

        long fourStar = ratings.stream()
                .filter(r ->
                        r.getScore() != null
                                && r.getScore() == 4
                )
                .count();

        long fiveStar = ratings.stream()
                .filter(r ->
                        r.getScore() != null
                                && r.getScore() == 5
                )
                .count();

        long verifiedCount = ratings.stream()
                .filter(Rating::isVerified)
                .count();

        long commentCount = ratings.stream()
                .filter(this::hasComment)
                .count();

        double teachingAverage =
                calculateCategoryAverage(
                        ratings,
                        RatingCategory.TEACHING
                );

        double learningAverage =
                calculateCategoryAverage(
                        ratings,
                        RatingCategory.LEARNING
                );

        double communicationAverage =
                calculateCategoryAverage(
                        ratings,
                        RatingCategory.COMMUNICATION
                );

        double knowledgeAverage =
                calculateCategoryAverage(
                        ratings,
                        RatingCategory.KNOWLEDGE
                );

        double professionalismAverage =
                calculateCategoryAverage(
                        ratings,
                        RatingCategory.PROFESSIONALISM
                );

        double punctualityAverage =
                calculateCategoryAverage(
                        ratings,
                        RatingCategory.PUNCTUALITY
                );

        double collaborationAverage =
                calculateCategoryAverage(
                        ratings,
                        RatingCategory.COLLABORATION
                );

        return new UserRatingSummaryResponse(
                userId,
                totalRatings,
                averageScore,
                teachingAverage,
                learningAverage,
                communicationAverage,
                knowledgeAverage,
                professionalismAverage,
                punctualityAverage,
                collaborationAverage,
                oneStar,
                twoStar,
                threeStar,
                fourStar,
                fiveStar,
                verifiedCount,
                commentCount
        );
    }

    public List<RatingResponse> getVerifiedRatingsForUser(
            Long userId,
            int limit
    ) {
        validateUserId(userId);

        int safeLimit = Math.min(
                Math.max(limit, 1),
                100
        );

        List<Rating> ratings =
                ratingRepository.findByRatedUserIdAndStatus(
                        userId,
                        RatingStatus.ACTIVE
                );

        return ratings.stream()
                .filter(Rating::isVerified)
                .sorted(
                        Comparator.comparing(
                                Rating::getCreatedAt,
                                Comparator.nullsLast(
                                        Comparator.reverseOrder()
                                )
                        )
                )
                .limit(safeLimit)
                .map(RatingResponse::from)
                .toList();
    }

    public List<RatingResponse> getRatingsWithComments(
            Long userId,
            int limit
    ) {
        validateUserId(userId);

        int safeLimit = Math.min(
                Math.max(limit, 1),
                100
        );

        List<Rating> ratings =
                ratingRepository.findByRatedUserIdAndStatus(
                        userId,
                        RatingStatus.ACTIVE
                );

        return ratings.stream()
                .filter(this::hasComment)
                .sorted(
                        Comparator.comparing(
                                Rating::getCreatedAt,
                                Comparator.nullsLast(
                                        Comparator.reverseOrder()
                                )
                        )
                )
                .limit(safeLimit)
                .map(RatingResponse::from)
                .toList();
    }

    private List<Rating> applyFilters(
            List<Rating> ratings,
            RatingSearchRequest request
    ) {
        return ratings.stream()
                .filter(this::isActive)
                .filter(rating ->
                        request.getMinimumScore() == null
                                || (
                                rating.getScore() != null
                                        && rating.getScore()
                                        >= request.getMinimumScore()
                        )
                )
                .filter(rating ->
                        request.getMaximumScore() == null
                                || (
                                rating.getScore() != null
                                        && rating.getScore()
                                        <= request.getMaximumScore()
                        )
                )
                .filter(rating ->
                        request.getVerified() == null
                                || rating.isVerified()
                                == request.getVerified()
                )
                .filter(rating ->
                        request.getAnonymous() == null
                                || rating.isAnonymous()
                                == request.getAnonymous()
                )
                .filter(rating ->
                        !Boolean.TRUE.equals(
                                request.getWithComments()
                        )
                                || hasComment(rating)
                )
                .toList();
    }

    private boolean hasComment(Rating rating) {
        return rating != null
                && rating.getComment() != null
                && !rating.getComment().isBlank();
    }

    private double calculateCategoryAverage(
            List<Rating> ratings,
            RatingCategory category
    ) {
        return ratings.stream()
                .filter(rating ->
                        rating.getCategory() == category
                )
                .filter(rating ->
                        rating.getScore() != null
                )
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);
    }

    private boolean isActive(Rating rating) {
        return rating != null
                && rating.getStatus() == RatingStatus.ACTIVE;
    }

    private Page<Rating> toPage(
            List<Rating> ratings,
            Pageable pageable
    ) {
        int start = (int) pageable.getOffset();

        if (start >= ratings.size()) {
            return new PageImpl<>(
                    List.of(),
                    pageable,
                    ratings.size()
            );
        }

        int end = Math.min(
                start + pageable.getPageSize(),
                ratings.size()
        );

        return new PageImpl<>(
                ratings.subList(start, end),
                pageable,
                ratings.size()
        );
    }

    private Pageable buildPageable(
            RatingSearchRequest request
    ) {
        int page = Math.max(
                request.getPage(),
                0
        );

        int size = Math.min(
                Math.max(request.getSize(), 1),
                100
        );

        Sort.Direction direction =
                "asc".equalsIgnoreCase(
                        request.getSortDirection()
                )
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        String sortField =
                normalizeSortField(
                        request.getSortBy()
                );

        return PageRequest.of(
                page,
                size,
                Sort.by(
                        direction,
                        sortField
                )
        );
    }

    private String normalizeSortField(
            String sortBy
    ) {
        if (sortBy == null || sortBy.isBlank()) {
            return "createdAt";
        }

        String normalized =
                sortBy.trim().toLowerCase(
                        Locale.ROOT
                );

        return switch (normalized) {
            case "score" -> "score";
            case "createdat", "created_at" -> "createdAt";
            case "updatedat", "updated_at" -> "updatedAt";
            case "category" -> "category";
            default -> "createdAt";
        };
    }

    private User getUser(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException(
                    "User email is required"
            );
        }

        return userRepository.findByEmail(
                        email.trim().toLowerCase(
                                Locale.ROOT
                        )
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found"
                        )
                );
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException(
                    "User ID must be valid"
            );
        }

        if (!userRepository.existsById(userId)) {
            throw new BadRequestException(
                    "User not found"
            );
        }
    }
}