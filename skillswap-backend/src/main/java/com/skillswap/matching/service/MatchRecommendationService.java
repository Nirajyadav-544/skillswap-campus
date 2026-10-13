package com.skillswap.matching.service;

import com.skillswap.matching.dto.MatchRecommendationResponse;
import com.skillswap.matching.dto.MatchResponse;
import com.skillswap.matching.entity.MatchResult;
import com.skillswap.matching.repository.MatchResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class MatchRecommendationService {

    private final MatchingService matchingService;
    private final MatchPreferenceService matchPreferenceService;
    private final MatchResultRepository matchResultRepository;

    public MatchRecommendationService(
            MatchingService matchingService,
            MatchPreferenceService matchPreferenceService,
            MatchResultRepository matchResultRepository
    ) {
        this.matchingService = matchingService;
        this.matchPreferenceService = matchPreferenceService;
        this.matchResultRepository = matchResultRepository;
    }

    /**
     * Generates recommendations using the user's configured
     * matching preferences.
     */
    public List<MatchRecommendationResponse> generateRecommendations(
            String userEmail
    ) {
        var preferences =
                matchPreferenceService.getOrCreate(
                        userEmail
                );

        if (!preferences.active()) {
            return List.of();
        }

        int limit =
                Math.max(
                        1,
                        preferences.maximumRecommendations()
                );

        List<MatchResponse> matches =
                matchingService.generateMatches(
                        userEmail,
                        limit
                );

        return matches.stream()
                .filter(match ->
                        match.overallScore()
                                >= preferences.minimumMatchScore()
                )
                .map(this::toRecommendation)
                .sorted(
                        Comparator.comparingDouble(
                                MatchRecommendationResponse::
                                        overallScore
                        ).reversed()
                )
                .limit(limit)
                .toList();
    }

    /**
     * Generates a preview without requiring existing
     * recommendation records.
     */
    @Transactional(readOnly = true)
    public List<MatchRecommendationResponse> previewRecommendations(
            String userEmail
    ) {
        var preferences =
                matchPreferenceService.getOrCreate(
                        userEmail
                );

        if (!preferences.active()) {
            return List.of();
        }

        int limit =
                Math.max(
                        1,
                        preferences.maximumRecommendations()
                );

        List<MatchResponse> matches =
                matchingService.previewMatches(
                        userEmail,
                        limit
                );

        return matches.stream()
                .filter(match ->
                        match.overallScore()
                                >= preferences.minimumMatchScore()
                )
                .map(this::toRecommendation)
                .sorted(
                        Comparator.comparingDouble(
                                MatchRecommendationResponse::
                                        overallScore
                        ).reversed()
                )
                .limit(limit)
                .toList();
    }

    /**
     * Gets stored recommendations for the current user.
     */
    @Transactional(readOnly = true)
    public List<MatchRecommendationResponse> getStoredRecommendations(
            String userEmail
    ) {
        List<MatchResult> matches =
                matchResultRepository.findAll();

        return matches.stream()
                .filter(match ->
                        match != null
                                && match.getRequesterUser() != null
                                && match.getRequesterUser()
                                .getEmail() != null
                                && match.getRequesterUser()
                                .getEmail()
                                .equalsIgnoreCase(
                                        userEmail
                                )
                )
                .filter(MatchResult::isDisplayable)
                .sorted(
                        Comparator.comparingDouble(
                                MatchResult::getOverallScore
                        ).reversed()
                )
                .map(MatchRecommendationResponse::from)
                .toList();
    }

    /**
     * Gets a single stored recommendation.
     */
    @Transactional(readOnly = true)
    public MatchRecommendationResponse getRecommendation(
            String userEmail,
            Long matchId
    ) {
        MatchResult matchResult =
                matchResultRepository.findById(matchId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Match not found: "
                                                + matchId
                                )
                        );

        if (matchResult.getRequesterUser() == null
                || matchResult.getRequesterUser()
                .getEmail() == null
                || !matchResult.getRequesterUser()
                .getEmail()
                .equalsIgnoreCase(userEmail)) {

            throw new IllegalArgumentException(
                    "This recommendation does not belong "
                            + "to the current user"
            );
        }

        return MatchRecommendationResponse.from(
                matchResult
        );
    }

    /**
     * Marks a recommendation as viewed.
     */
    public MatchRecommendationResponse viewRecommendation(
            String userEmail,
            Long matchId
    ) {
        MatchResponse response =
                matchingService.markViewed(
                        userEmail,
                        matchId
                );

        return toRecommendation(response);
    }

    /**
     * Marks a recommendation as interested.
     */
    public MatchRecommendationResponse interested(
            String userEmail,
            Long matchId
    ) {
        MatchResponse response =
                matchingService.markInterested(
                        userEmail,
                        matchId
                );

        return toRecommendation(response);
    }

    /**
     * Dismisses a recommendation.
     */
    public MatchRecommendationResponse dismiss(
            String userEmail,
            Long matchId
    ) {
        MatchResponse response =
                matchingService.dismissMatch(
                        userEmail,
                        matchId
                );

        return toRecommendation(response);
    }

    private MatchRecommendationResponse toRecommendation(
            MatchResponse response
    ) {
        return new MatchRecommendationResponse(
                response.id(),
                response.candidateUserId(),
                response.candidateFirstName(),
                response.candidateLastName(),
                response.candidateEmail(),
                response.status(),
                response.algorithm(),
                response.overallScore(),
                response.matchingSkillCount(),
                response.viewed(),
                response.interested(),
                response.dismissed(),
                response.explanation(),
                response.generatedAt(),
                response.expiresAt()
        );
    }
}