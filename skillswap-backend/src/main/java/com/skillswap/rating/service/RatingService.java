package com.skillswap.rating.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.rating.dto.CreateRatingRequest;
import com.skillswap.rating.dto.RatingPageResponse;
import com.skillswap.rating.dto.RatingResponse;
import com.skillswap.rating.dto.UpdateRatingRequest;
import com.skillswap.rating.entity.Rating;
import com.skillswap.rating.entity.RatingStatus;
import com.skillswap.rating.repository.RatingRepository;
import com.skillswap.session.entity.LearningSession;
import com.skillswap.session.repository.LearningSessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final LearningSessionRepository sessionRepository;

    public RatingService(
            RatingRepository ratingRepository,
            UserRepository userRepository,
            LearningSessionRepository sessionRepository
    ) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public RatingResponse create(
            CreateRatingRequest request,
            String userEmail
    ) {
        if (request == null) {
            throw new BadRequestException("Rating request is required");
        }

        request.normalize();

        User rater = getUser(userEmail);

        LearningSession session = sessionRepository
                .findById(request.getSessionId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Learning session not found"
                        ));

        User ratedUser = userRepository
                .findById(request.getRatedUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rated user not found"
                        ));

        validateRating(rater, ratedUser, session);

        if (ratingRepository
                .existsBySessionIdAndRaterUserIdAndRatedUserIdAndCategory(
                        session.getId(),
                        rater.getId(),
                        ratedUser.getId(),
                        request.getCategory()
                )) {

            throw new BadRequestException(
                    "You have already submitted this rating category for this session"
            );
        }

        Rating rating = new Rating(
                session,
                rater,
                ratedUser,
                request.getCategory(),
                request.getScore()
        );

        rating.setComment(request.getComment());
        rating.setAnonymous(request.isAnonymous());
        rating.setVerified(true);
        rating.setStatus(RatingStatus.ACTIVE);

        return RatingResponse.from(
                ratingRepository.save(rating)
        );
    }

    @Transactional(readOnly = true)
    public RatingResponse getById(
            Long ratingId,
            String userEmail
    ) {
        Rating rating = getRating(ratingId);
        User currentUser = getUser(userEmail);

        validateViewer(rating, currentUser);

        return RatingResponse.from(rating);
    }

    @Transactional(readOnly = true)
    public List<RatingResponse> getForUser(
            Long userId,
            String userEmail
    ) {
        User currentUser = getUser(userEmail);

        return ratingRepository
                .findByRatedUserIdAndStatus(
                        userId,
                        RatingStatus.ACTIVE
                )
                .stream()
                .filter(rating -> canView(rating, currentUser))
                .map(RatingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RatingResponse> getGivenByCurrentUser(
            String userEmail
    ) {
        User user = getUser(userEmail);

        return ratingRepository
                .findByRaterUserId(user.getId())
                .stream()
                .map(RatingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RatingResponse> getForSession(
            Long sessionId,
            String userEmail
    ) {
        User currentUser = getUser(userEmail);

        return ratingRepository
                .findBySessionId(sessionId)
                .stream()
                .filter(rating -> canView(rating, currentUser))
                .map(RatingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RatingPageResponse search(
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        page = Math.max(page, 0);
        size = Math.max(1, Math.min(size, 100));

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "createdAt";
        }

        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "DESC";
        }

        Sort.Direction direction =
                "ASC".equalsIgnoreCase(sortDirection)
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortBy)
        );

        Page<Rating> ratings =
                ratingRepository.findAll(pageable);

        return RatingPageResponse.from(ratings);
    }

    public RatingResponse update(
            Long ratingId,
            UpdateRatingRequest request,
            String userEmail
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Update request is required"
            );
        }

        request.normalize();

        Rating rating = getRating(ratingId);
        User currentUser = getUser(userEmail);

        validateOwner(rating, currentUser);

        if (rating.getStatus() == RatingStatus.DELETED) {
            throw new BadRequestException(
                    "Deleted ratings cannot be updated"
            );
        }

        if (request.isEmpty()) {
            throw new BadRequestException(
                    "At least one rating field must be updated"
            );
        }

        if (request.hasCategoryUpdate()
                && request.getCategory() != rating.getCategory()) {

            boolean exists =
                    ratingRepository
                            .existsBySessionIdAndRaterUserIdAndRatedUserIdAndCategory(
                                    rating.getSession().getId(),
                                    rating.getRaterUser().getId(),
                                    rating.getRatedUser().getId(),
                                    request.getCategory()
                            );

            if (exists) {
                throw new BadRequestException(
                        "A rating with this category already exists"
                );
            }

            rating.setCategory(request.getCategory());
        }

        if (request.hasScoreUpdate()) {
            validateScore(request.getScore());
            rating.setScore(request.getScore());
        }

        if (request.hasCommentUpdate()) {
            rating.setComment(request.getComment());
        }

        if (request.hasAnonymousUpdate()) {
            rating.setAnonymous(request.getAnonymous());
        }

        rating.markEdited();

        return RatingResponse.from(
                ratingRepository.save(rating)
        );
    }

    public void delete(
            Long ratingId,
            String userEmail
    ) {
        Rating rating = getRating(ratingId);
        User currentUser = getUser(userEmail);

        validateOwner(rating, currentUser);

        rating.delete();

        ratingRepository.save(rating);
    }

    public void hide(
            Long ratingId,
            String userEmail
    ) {
        Rating rating = getRating(ratingId);
        User currentUser = getUser(userEmail);

        validateOwner(rating, currentUser);

        /*
         * Rating.hide() requires a moderation reason.
         * Owner-initiated hiding has no moderation reason,
         * so use a descriptive reason here.
         */
        rating.hide("Hidden by rating author");

        ratingRepository.save(rating);
    }

    public void activate(
            Long ratingId,
            String userEmail
    ) {
        Rating rating = getRating(ratingId);
        User currentUser = getUser(userEmail);

        validateOwner(rating, currentUser);

        rating.activate();

        ratingRepository.save(rating);
    }

    @Transactional(readOnly = true)
    public long countForUser(Long userId) {
        return ratingRepository.countByRatedUserId(userId);
    }

    @Transactional(readOnly = true)
    public long countGivenByUser(Long userId) {
        /*
         * The repository does not expose countByRaterUserId().
         * Use the already available query method.
         */
        return ratingRepository
                .findByRaterUserId(userId)
                .size();
    }

    @Transactional(readOnly = true)
    public double getAverageScore(Long userId) {
        Double average =
                ratingRepository.calculateAverageScore(
                        userId,
                        RatingStatus.ACTIVE
                );

        return average != null
                ? Math.round(average * 100.0) / 100.0
                : 0.0;
    }

    private Rating getRating(Long ratingId) {

        if (ratingId == null) {
            throw new BadRequestException(
                    "Rating ID is required"
            );
        }

        return ratingRepository
                .findById(ratingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rating not found"
                        ));
    }

    private User getUser(String email) {

        if (email == null || email.isBlank()) {
            throw new BadRequestException(
                    "Authenticated user is required"
            );
        }

        return userRepository
                .findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    private void validateRating(
            User rater,
            User ratedUser,
            LearningSession session
    ) {

        if (rater.getId().equals(ratedUser.getId())) {
            throw new BadRequestException(
                    "You cannot rate yourself"
            );
        }

        if (session == null) {
            throw new BadRequestException(
                    "Learning session is required"
            );
        }

        if (session.getTeacherUser() == null
                || session.getLearnerUser() == null) {

            throw new BadRequestException(
                    "Session participants are not available"
            );
        }

        boolean raterParticipated =
                rater.getId().equals(
                        session.getTeacherUser().getId()
                )
                || rater.getId().equals(
                        session.getLearnerUser().getId()
                );

        boolean ratedParticipated =
                ratedUser.getId().equals(
                        session.getTeacherUser().getId()
                )
                || ratedUser.getId().equals(
                        session.getLearnerUser().getId()
                );

        if (!raterParticipated || !ratedParticipated) {
            throw new BadRequestException(
                    "Both users must be participants in the session"
            );
        }
    }

    private void validateOwner(
            Rating rating,
            User currentUser
    ) {

        if (rating.getRaterUser() == null
                || currentUser.getId() == null
                || !currentUser.getId().equals(
                rating.getRaterUser().getId()
        )) {

            throw new BadRequestException(
                    "Only the rating author can perform this action"
            );
        }
    }

    private void validateViewer(
            Rating rating,
            User currentUser
    ) {

        if (!canView(rating, currentUser)) {
            throw new BadRequestException(
                    "You are not allowed to view this rating"
            );
        }
    }

    private boolean canView(
            Rating rating,
            User currentUser
    ) {

        if (rating == null || currentUser == null) {
            return false;
        }

        if (rating.getRaterUser() != null
                && currentUser.getId().equals(
                rating.getRaterUser().getId()
        )) {
            return true;
        }

        if (rating.getRatedUser() != null
                && currentUser.getId().equals(
                rating.getRatedUser().getId()
        )) {
            return true;
        }

        return rating.getStatus() == RatingStatus.ACTIVE;
    }

    private void validateScore(Integer score) {

        if (score == null || score < 1 || score > 5) {
            throw new BadRequestException(
                    "Rating score must be between 1 and 5"
            );
        }
    }
}