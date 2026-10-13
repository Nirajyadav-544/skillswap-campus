package com.skillswap.matching.service;

import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.matching.dto.MatchFeedbackRequest;
import com.skillswap.matching.dto.MatchFeedbackResponse;
import com.skillswap.matching.entity.MatchFeedback;
import com.skillswap.matching.entity.MatchFeedbackType;
import com.skillswap.matching.entity.MatchResult;
import com.skillswap.matching.repository.MatchFeedbackRepository;
import com.skillswap.matching.repository.MatchResultRepository;
import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MatchFeedbackService {

    private final MatchFeedbackRepository matchFeedbackRepository;
    private final MatchResultRepository matchResultRepository;
    private final UserRepository userRepository;

    public MatchFeedbackService(
            MatchFeedbackRepository matchFeedbackRepository,
            MatchResultRepository matchResultRepository,
            UserRepository userRepository
    ) {
        this.matchFeedbackRepository = matchFeedbackRepository;
        this.matchResultRepository = matchResultRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates feedback for the authenticated user.
     */
    public MatchFeedbackResponse createFeedback(
            String userEmail,
            MatchFeedbackRequest request
    ) {
        User user = getUserByEmail(userEmail);

        if (request == null) {
            throw new BadRequestException("Feedback request cannot be null");
        }

        MatchFeedbackRequest normalized = request.normalized();

        if (normalized.matchResultId() == null) {
            throw new BadRequestException("Match result ID is required");
        }

        if (normalized.feedbackType() == null) {
            throw new BadRequestException("Feedback type is required");
        }

        MatchResult matchResult = matchResultRepository
                .findById(normalized.matchResultId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Match result not found: "
                                        + normalized.matchResultId()
                        )
                );

        validateMatchOwnership(user, matchResult);

        MatchFeedback feedback = new MatchFeedback(
                user,
                matchResult,
                normalized.feedbackType()
        );

        feedback.setRating(normalized.rating());
        feedback.setComment(normalized.comment());
        feedback.setSource(normalized.source());

        if (normalized.explicitFeedback() != null) {
            feedback.setExplicitFeedback(
                    normalized.explicitFeedback()
            );
        }

        MatchFeedback saved =
                matchFeedbackRepository.save(feedback);

        updateMatchFromFeedback(matchResult, normalized.feedbackType());

        return MatchFeedbackResponse.from(saved);
    }

    /**
     * Gets feedback by ID.
     */
    @Transactional(readOnly = true)
    public MatchFeedbackResponse getFeedback(
            String userEmail,
            Long feedbackId
    ) {
        User user = getUserByEmail(userEmail);

        if (feedbackId == null) {
            throw new BadRequestException("Feedback ID is required");
        }

        MatchFeedback feedback = matchFeedbackRepository
                .findById(feedbackId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Match feedback not found: " + feedbackId
                        )
                );

        validateFeedbackOwnership(user, feedback);

        return MatchFeedbackResponse.from(feedback);
    }

    /**
     * Gets all feedback created by the authenticated user.
     */
    @Transactional(readOnly = true)
    public List<MatchFeedbackResponse> getMyFeedback(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return matchFeedbackRepository
                .findAll()
                .stream()
                .filter(feedback ->
                        feedback.getUser() != null
                                && feedback.getUser().getId() != null
                                && feedback.getUser().getId()
                                .equals(user.getId())
                )
                .map(MatchFeedbackResponse::from)
                .toList();
    }

    /**
     * Gets feedback belonging to a particular match
     * for the authenticated user.
     */
    @Transactional(readOnly = true)
    public List<MatchFeedbackResponse> getFeedbackForMatch(
            String userEmail,
            Long matchResultId
    ) {
        User user = getUserByEmail(userEmail);

        if (matchResultId == null) {
            throw new BadRequestException("Match result ID is required");
        }

        MatchResult matchResult = matchResultRepository
                .findById(matchResultId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Match result not found: "
                                        + matchResultId
                        )
                );

        validateMatchOwnership(user, matchResult);

        return matchFeedbackRepository
                .findAll()
                .stream()
                .filter(feedback ->
                        feedback.getUser() != null
                                && feedback.getUser().getId() != null
                                && feedback.getUser().getId()
                                .equals(user.getId())
                )
                .filter(feedback ->
                        feedback.getMatchResult() != null
                                && feedback.getMatchResult().getId() != null
                                && feedback.getMatchResult().getId()
                                .equals(matchResultId)
                )
                .map(MatchFeedbackResponse::from)
                .toList();
    }

    /**
     * Marks feedback as processed.
     */
    public MatchFeedbackResponse markProcessed(
            String userEmail,
            Long feedbackId
    ) {
        User user = getUserByEmail(userEmail);

        MatchFeedback feedback = matchFeedbackRepository
                .findById(feedbackId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Match feedback not found: " + feedbackId
                        )
                );

        validateFeedbackOwnership(user, feedback);

        feedback.markProcessed();

        MatchFeedback saved =
                matchFeedbackRepository.save(feedback);

        return MatchFeedbackResponse.from(saved);
    }

    /**
     * Deletes feedback created by the authenticated user.
     */
    public void deleteFeedback(
            String userEmail,
            Long feedbackId
    ) {
        User user = getUserByEmail(userEmail);

        MatchFeedback feedback = matchFeedbackRepository
                .findById(feedbackId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Match feedback not found: " + feedbackId
                        )
                );

        validateFeedbackOwnership(user, feedback);

        matchFeedbackRepository.delete(feedback);
    }

    private User getUserByEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new BadRequestException(
                    "Authenticated user email is required"
            );
        }

        return userRepository
                .findByEmail(email.trim().toLowerCase())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }

    private void validateMatchOwnership(
            User user,
            MatchResult matchResult
    ) {
        if (matchResult == null) {
            throw new ResourceNotFoundException(
                    "Match result not found"
            );
        }

        if (matchResult.getRequesterUser() == null
                || matchResult.getRequesterUser().getId() == null
                || user.getId() == null
                || !matchResult.getRequesterUser()
                .getId()
                .equals(user.getId())) {

            throw new BadRequestException(
                    "You can only provide feedback for your own matches"
            );
        }
    }

    private void validateFeedbackOwnership(
            User user,
            MatchFeedback feedback
    ) {
        if (feedback.getUser() == null
                || feedback.getUser().getId() == null
                || user.getId() == null
                || !feedback.getUser()
                .getId()
                .equals(user.getId())) {

            throw new BadRequestException(
                    "You can only access your own feedback"
            );
        }
    }

    private void updateMatchFromFeedback(
            MatchResult matchResult,
            MatchFeedbackType feedbackType
    ) {
        if (feedbackType == null || matchResult == null) {
            return;
        }

        switch (feedbackType) {

            case INTERESTED ->
                    matchResult.markInterested();

            case DISMISSED,
                 NOT_INTERESTED ->
                    matchResult.markDismissed();

            default -> {
                // HELPFUL, RELEVANT,
                // NOT_HELPFUL and NOT_RELEVANT
                // do not change match status.
            }
        }

        matchResultRepository.save(matchResult);
    }
}