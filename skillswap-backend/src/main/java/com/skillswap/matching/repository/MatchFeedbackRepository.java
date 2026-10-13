package com.skillswap.matching.repository;

import com.skillswap.matching.entity.MatchFeedback;
import com.skillswap.matching.entity.MatchFeedbackType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for MatchFeedback persistence and analytics.
 *
 * Feedback will eventually provide valuable historical information
 * for improving the recommendation engine.
 */
public interface MatchFeedbackRepository
        extends JpaRepository<MatchFeedback, Long> {

    /**
     * Finds all feedback submitted by a user.
     */
    List<MatchFeedback>
    findAllByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    /**
     * Finds paginated feedback submitted by a user.
     */
    Page<MatchFeedback>
    findAllByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    /**
     * Finds feedback for a particular match.
     */
    List<MatchFeedback>
    findAllByMatchResultIdOrderByCreatedAtDesc(
            Long matchResultId
    );

    /**
     * Finds feedback of a specific type from a user.
     */
    List<MatchFeedback>
    findAllByUserIdAndFeedbackTypeOrderByCreatedAtDesc(
            Long userId,
            MatchFeedbackType feedbackType
    );

    /**
     * Finds all feedback of a specific type.
     */
    List<MatchFeedback>
    findAllByFeedbackTypeOrderByCreatedAtDesc(
            MatchFeedbackType feedbackType
    );

    /**
     * Finds all unprocessed feedback.
     *
     * This will later be useful for an analytics or ML pipeline.
     */
    List<MatchFeedback>
    findAllByProcessedFalseOrderByCreatedAtAsc();

    /**
     * Finds unprocessed feedback with pagination.
     */
    Page<MatchFeedback>
    findAllByProcessedFalseOrderByCreatedAtAsc(
            Pageable pageable
    );

    /**
     * Finds processed feedback.
     */
    List<MatchFeedback>
    findAllByProcessedTrueOrderByCreatedAtDesc();

    /**
     * Finds feedback created after a particular timestamp.
     */
    List<MatchFeedback>
    findAllByCreatedAtAfterOrderByCreatedAtDesc(
            LocalDateTime after
    );

    /**
     * Finds feedback created between two timestamps.
     */
    List<MatchFeedback>
    findAllByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Finds explicit feedback.
     */
    List<MatchFeedback>
    findAllByExplicitFeedbackTrueOrderByCreatedAtDesc();

    /**
     * Finds implicit feedback.
     */
    List<MatchFeedback>
    findAllByExplicitFeedbackFalseOrderByCreatedAtDesc();

    /**
     * Counts feedback submitted by a user.
     */
    long countByUserId(
            Long userId
    );

    /**
     * Counts feedback for a particular match.
     */
    long countByMatchResultId(
            Long matchResultId
    );

    /**
     * Counts feedback of a specific type.
     */
    long countByFeedbackType(
            MatchFeedbackType feedbackType
    );

    /**
     * Counts unprocessed feedback.
     */
    long countByProcessedFalse();

    /**
     * Counts processed feedback.
     */
    long countByProcessedTrue();

    /**
     * Counts explicit feedback.
     */
    long countByExplicitFeedbackTrue();

    /**
     * Counts feedback with a numeric rating.
     */
    long countByRatingIsNotNull();

    /**
     * Finds feedback with a rating greater than or equal to
     * the supplied value.
     */
    List<MatchFeedback>
    findAllByRatingGreaterThanEqualOrderByCreatedAtDesc(
            Integer minimumRating
    );

    /**
     * Finds feedback with a rating below or equal to the supplied value.
     */
    List<MatchFeedback>
    findAllByRatingLessThanEqualOrderByCreatedAtDesc(
            Integer maximumRating
    );

    /**
     * Deletes feedback belonging to a match.
     */
    void deleteAllByMatchResultId(
            Long matchResultId
    );

    /**
     * Deletes feedback submitted by a user.
     */
    void deleteAllByUserId(
            Long userId
    );
}