package com.skillswap.matching.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.matching.dto.MatchPreferenceRequest;
import com.skillswap.matching.dto.MatchPreferenceResponse;
import com.skillswap.matching.entity.MatchPreference;
import com.skillswap.matching.repository.MatchPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MatchPreferenceService {

    private final MatchPreferenceRepository matchPreferenceRepository;
    private final UserRepository userRepository;

    public MatchPreferenceService(
            MatchPreferenceRepository matchPreferenceRepository,
            UserRepository userRepository
    ) {
        this.matchPreferenceRepository = matchPreferenceRepository;
        this.userRepository = userRepository;
    }

    /**
     * Gets the user's preferences.
     *
     * If preferences do not exist, default preferences are created.
     */
    public MatchPreferenceResponse getOrCreate(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        MatchPreference preference = matchPreferenceRepository
                .findByUser(user)
                .orElseGet(() -> {
                    MatchPreference created = new MatchPreference();
                    created.setUser(user);
                    return matchPreferenceRepository.save(created);
                });

        return MatchPreferenceResponse.from(preference);
    }

    /**
     * Gets existing preferences without creating defaults.
     */
    @Transactional(readOnly = true)
    public MatchPreferenceResponse get(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        MatchPreference preference = matchPreferenceRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Match preferences not found for user"
                        )
                );

        return MatchPreferenceResponse.from(preference);
    }

    /**
     * Creates or updates the user's match preferences.
     */
    public MatchPreferenceResponse createOrUpdate(
            String userEmail,
            MatchPreferenceRequest request
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Match preference request cannot be null"
            );
        }

        User user = getUserByEmail(userEmail);

        MatchPreference preference = matchPreferenceRepository
                .findByUser(user)
                .orElseGet(() -> {
                    MatchPreference created = new MatchPreference();
                    created.setUser(user);
                    return created;
                });

        applyRequest(preference, request);

        if (preference.getUser() == null) {
            preference.setUser(user);
        }

        MatchPreference saved =
                matchPreferenceRepository.save(preference);

        return MatchPreferenceResponse.from(saved);
    }

    /**
     * Updates only the explicitly supplied preference values.
     */
    public MatchPreferenceResponse update(
            String userEmail,
            MatchPreferenceRequest request
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Match preference request cannot be null"
            );
        }

        User user = getUserByEmail(userEmail);

        MatchPreference preference = matchPreferenceRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Match preferences not found for user"
                        )
                );

        applyNonNullValues(preference, request);

        MatchPreference saved =
                matchPreferenceRepository.save(preference);

        return MatchPreferenceResponse.from(saved);
    }

    /**
     * Activates match preferences.
     */
    public MatchPreferenceResponse activate(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        MatchPreference preference = matchPreferenceRepository
                .findByUser(user)
                .orElseGet(() -> {
                    MatchPreference created = new MatchPreference();
                    created.setUser(user);
                    return created;
                });

        preference.setActive(true);

        MatchPreference saved =
                matchPreferenceRepository.save(preference);

        return MatchPreferenceResponse.from(saved);
    }

    /**
     * Deactivates match preferences.
     */
    public MatchPreferenceResponse deactivate(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        MatchPreference preference = matchPreferenceRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Match preferences not found for user"
                        )
                );

        preference.setActive(false);

        MatchPreference saved =
                matchPreferenceRepository.save(preference);

        return MatchPreferenceResponse.from(saved);
    }

    /**
     * Deletes the user's match preferences.
     */
    public void delete(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        if (!matchPreferenceRepository.existsByUser(user)) {
            throw new ResourceNotFoundException(
                    "Match preferences not found for user"
            );
        }

        matchPreferenceRepository.deleteByUser(user);
    }

    /**
     * Finds the user using the authenticated email.
     */
    private User getUserByEmail(
            String email
    ) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException(
                    "User email is required"
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        return userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: "
                                        + normalizedEmail
                        )
                );
    }

    /**
     * Applies all non-null request values.
     */
    private void applyRequest(
            MatchPreference preference,
            MatchPreferenceRequest request
    ) {
        MatchPreferenceRequest normalized =
                request.normalized();

        if (normalized == null) {
            return;
        }

        if (normalized.active() != null) {
            preference.setActive(
                    normalized.active()
            );
        }

        if (normalized.minimumMatchScore() != null) {
            preference.setMinimumMatchScore(
                    normalized.minimumMatchScore()
            );
        }

        if (normalized.minimumProficiency() != null) {
            preference.setMinimumProficiency(
                    normalized.minimumProficiency()
            );
        }

        if (normalized.preferredCategory() != null) {
            preference.setPreferredCategory(
                    normalized.preferredCategory()
            );
        }

        if (normalized.minimumTeacherExperience() != null) {
            preference.setMinimumTeacherExperience(
                    normalized.minimumTeacherExperience()
            );
        }

        if (normalized.preferVerifiedUsers() != null) {
            preference.setPreferVerifiedUsers(
                    normalized.preferVerifiedUsers()
            );
        }

        if (normalized.preferExperiencedTeachers() != null) {
            preference.setPreferExperiencedTeachers(
                    normalized.preferExperiencedTeachers()
            );
        }

        if (normalized.preferRatedUsers() != null) {
            preference.setPreferRatedUsers(
                    normalized.preferRatedUsers()
            );
        }

        if (normalized.preferCompleteProfiles() != null) {
            preference.setPreferCompleteProfiles(
                    normalized.preferCompleteProfiles()
            );
        }

        if (normalized.allowOnline() != null) {
            preference.setAllowOnline(
                    normalized.allowOnline()
            );
        }

        if (normalized.allowOffline() != null) {
            preference.setAllowOffline(
                    normalized.allowOffline()
            );
        }

        if (normalized.maximumRecommendations() != null) {
            preference.setMaximumRecommendations(
                    normalized.maximumRecommendations()
            );
        }

        if (normalized.preferSameCollege() != null) {
            preference.setPreferSameCollege(
                    normalized.preferSameCollege()
            );
        }

        if (normalized.preferSameDepartment() != null) {
            preference.setPreferSameDepartment(
                    normalized.preferSameDepartment()
            );
        }

        if (normalized.preferSameCourse() != null) {
            preference.setPreferSameCourse(
                    normalized.preferSameCourse()
            );
        }

        if (normalized.preferredLocation() != null) {
            preference.setPreferredLocation(
                    normalized.preferredLocation()
            );
        }

        if (normalized.preferredAvailability() != null) {
            preference.setPreferredAvailability(
                    normalized.preferredAvailability()
            );
        }
    }

    /**
     * Applies only explicitly supplied values.
     *
     * MatchPreferenceRequest uses nullable fields so PATCH-style
     * updates do not overwrite existing preferences accidentally.
     */
    private void applyNonNullValues(
            MatchPreference preference,
            MatchPreferenceRequest request
    ) {
        applyRequest(preference, request);
    }
}