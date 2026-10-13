package com.skillswap.rating.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.rating.dto.ReviewRequest;
import com.skillswap.rating.dto.ReviewResponse;
import com.skillswap.rating.entity.Rating;
import com.skillswap.rating.entity.Review;
import com.skillswap.rating.repository.RatingRepository;
import com.skillswap.rating.repository.ReviewRepository;
import com.skillswap.session.entity.LearningSession;
import com.skillswap.session.repository.LearningSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final LearningSessionRepository sessionRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            RatingRepository ratingRepository,
            UserRepository userRepository,
            LearningSessionRepository sessionRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public ReviewResponse create(
            ReviewRequest request,
            String userEmail
    ) {

        if (request == null) {
            throw new BadRequestException(
                    "Review request is required"
            );
        }

        request.normalize();

        User author = getUser(userEmail);

        Rating rating = null;

        if (request.getRatingId() != null) {
            rating = ratingRepository
                    .findById(request.getRatingId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Rating not found"
                            ));
        }

        LearningSession session;

        if (request.getSessionId() != null) {

            session = sessionRepository
                    .findById(request.getSessionId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Learning session not found"
                            ));

        } else if (rating != null
                && rating.getSession() != null) {

            session = rating.getSession();

        } else {

            throw new BadRequestException(
                    "Session ID or Rating ID is required"
            );
        }

        User targetUser;

        if (request.getTargetUserId() != null) {

            targetUser = userRepository
                    .findById(request.getTargetUserId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Target user not found"
                            ));

        } else if (rating != null
                && rating.getRatedUser() != null) {

            targetUser = rating.getRatedUser();

        } else {

            throw new BadRequestException(
                    "Target user ID or Rating ID is required"
            );
        }

        validateReview(
                author,
                targetUser,
                session,
                rating
        );

        if (rating != null
                && reviewRepository.existsByRatingId(
                rating.getId()
        )) {

            throw new BadRequestException(
                    "A review already exists for this rating"
            );
        }

        Review review = new Review(
                rating,
                session,
                author,
                targetUser,
                request.getContent()
        );

        review.setAnonymous(
                request.isAnonymous()
        );

        return ReviewResponse.from(
                reviewRepository.save(review)
        );
    }

    @Transactional(readOnly = true)
    public ReviewResponse getById(
            Long reviewId,
            String userEmail
    ) {

        Review review = getReview(reviewId);
        User currentUser = getUser(userEmail);

        validateViewer(review, currentUser);

        return ReviewResponse.from(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getForUser(
            Long userId,
            String userEmail
    ) {

        User currentUser = getUser(userEmail);

        return reviewRepository
                .findByTargetUserIdAndVisibleTrue(userId)
                .stream()
                .filter(review ->
                        canView(review, currentUser)
                )
                .map(ReviewResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getWrittenByCurrentUser(
            String userEmail
    ) {

        User author = getUser(userEmail);

        return reviewRepository
                .findByAuthorUserId(author.getId())
                .stream()
                .map(ReviewResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getForSession(
            Long sessionId,
            String userEmail
    ) {

        User currentUser = getUser(userEmail);

        return reviewRepository
                .findBySessionId(sessionId)
                .stream()
                .filter(review ->
                        canView(review, currentUser)
                )
                .map(ReviewResponse::from)
                .toList();
    }

    public ReviewResponse update(
            Long reviewId,
            String content,
            Boolean anonymous,
            String userEmail
    ) {

        Review review = getReview(reviewId);
        User currentUser = getUser(userEmail);

        validateOwner(review, currentUser);

        if (review.isDeleted()) {
            throw new BadRequestException(
                    "Deleted reviews cannot be updated"
            );
        }

        if (content == null || content.isBlank()) {
            throw new BadRequestException(
                    "Review content is required"
            );
        }

        content = content.trim();

        if (content.length() > 2000) {
            throw new BadRequestException(
                    "Review content must not exceed 2000 characters"
            );
        }

        review.setContent(content);

        if (anonymous != null) {
            review.setAnonymous(anonymous);
        }

        review.markEdited();

        return ReviewResponse.from(
                reviewRepository.save(review)
        );
    }

    public void delete(
            Long reviewId,
            String userEmail
    ) {

        Review review = getReview(reviewId);
        User currentUser = getUser(userEmail);

        validateOwner(review, currentUser);

        review.delete();

        reviewRepository.save(review);
    }

    public void hide(
            Long reviewId,
            String userEmail
    ) {

        Review review = getReview(reviewId);
        User currentUser = getUser(userEmail);

        validateOwner(review, currentUser);

        /*
         * Review.hide() requires a moderation reason.
         * Owner-initiated hiding gets a descriptive reason.
         */
        review.hide("Hidden by review author");

        reviewRepository.save(review);
    }

    public void show(
            Long reviewId,
            String userEmail
    ) {

        Review review = getReview(reviewId);
        User currentUser = getUser(userEmail);

        validateOwner(review, currentUser);

        review.show();

        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public long countForUser(Long userId) {
        return reviewRepository.countByTargetUserId(userId);
    }

    @Transactional(readOnly = true)
    public long countWrittenByUser(Long userId) {
        return reviewRepository.countByAuthorUserId(userId);
    }

    private Review getReview(Long reviewId) {

        if (reviewId == null) {
            throw new BadRequestException(
                    "Review ID is required"
            );
        }

        return reviewRepository
                .findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review not found"
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

    private void validateReview(
            User author,
            User targetUser,
            LearningSession session,
            Rating rating
    ) {

        if (author.getId().equals(targetUser.getId())) {
            throw new BadRequestException(
                    "You cannot review yourself"
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

        boolean authorParticipated =
                author.getId().equals(
                        session.getTeacherUser().getId()
                )
                || author.getId().equals(
                        session.getLearnerUser().getId()
                );

        boolean targetParticipated =
                targetUser.getId().equals(
                        session.getTeacherUser().getId()
                )
                || targetUser.getId().equals(
                        session.getLearnerUser().getId()
                );

        if (!authorParticipated || !targetParticipated) {
            throw new BadRequestException(
                    "Both users must be participants in the session"
            );
        }

        if (rating != null) {

            if (rating.getSession() == null
                    || !rating.getSession().getId().equals(
                    session.getId()
            )) {

                throw new BadRequestException(
                        "Rating and review must belong to the same session"
                );
            }

            if (rating.getRaterUser() == null
                    || !rating.getRaterUser().getId().equals(
                    author.getId()
            )) {

                throw new BadRequestException(
                        "Only the rating author can create its review"
                );
            }

            if (rating.getRatedUser() == null
                    || !rating.getRatedUser().getId().equals(
                    targetUser.getId()
            )) {

                throw new BadRequestException(
                        "Review target must match the rating target"
                );
            }
        }
    }

    private void validateOwner(
            Review review,
            User currentUser
    ) {

        if (review.getAuthorUser() == null
                || !currentUser.getId().equals(
                review.getAuthorUser().getId()
        )) {

            throw new BadRequestException(
                    "Only the review author can perform this action"
            );
        }
    }

    private void validateViewer(
            Review review,
            User currentUser
    ) {

        if (!canView(review, currentUser)) {
            throw new BadRequestException(
                    "You are not allowed to view this review"
            );
        }
    }

    private boolean canView(
            Review review,
            User currentUser
    ) {

        if (review == null || currentUser == null) {
            return false;
        }

        if (review.getAuthorUser() != null
                && currentUser.getId().equals(
                review.getAuthorUser().getId()
        )) {
            return true;
        }

        if (review.getTargetUser() != null
                && currentUser.getId().equals(
                review.getTargetUser().getId()
        )) {
            return true;
        }

        return review.isVisibleToUsers();
    }
}