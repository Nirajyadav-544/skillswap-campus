package com.skillswap.matching.repository;

import com.skillswap.matching.entity.MatchPreference;
import com.skillswap.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for user matching preferences.
 *
 * Each user can have at most one MatchPreference because
 * MatchPreference.user is configured as a unique one-to-one relation.
 */
public interface MatchPreferenceRepository
        extends JpaRepository<MatchPreference, Long> {

    /**
     * Finds the preference configuration belonging to a user.
     */
    Optional<MatchPreference> findByUser(User user);

    /**
     * Finds preferences directly by user ID.
     */
    Optional<MatchPreference> findByUser_Id(Long userId);

    /**
     * Checks whether a user already has a preference configuration.
     */
    boolean existsByUser(User user);

    /**
     * Checks whether a user ID already has a preference configuration.
     */
    boolean existsByUser_Id(Long userId);

    /**
     * Returns all active preference configurations.
     */
    List<MatchPreference> findByActiveTrue();

    /**
     * Returns all inactive preference configurations.
     */
    List<MatchPreference> findByActiveFalse();

    /**
     * Finds active preferences whose minimum required score
     * is less than or equal to the supplied score.
     */
    List<MatchPreference> findByActiveTrueAndMinimumMatchScoreLessThanEqual(
            double score
    );

    /**
     * Deletes the preference configuration belonging to a user.
     */
    void deleteByUser(User user);

    /**
     * Deletes the preference configuration belonging to a user ID.
     */
    void deleteByUser_Id(Long userId);
}