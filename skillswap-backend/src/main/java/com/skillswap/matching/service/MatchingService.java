package com.skillswap.matching.service;

import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.matching.dto.MatchResponse;
import com.skillswap.matching.entity.MatchAlgorithm;
import com.skillswap.matching.entity.MatchResult;
import com.skillswap.matching.repository.MatchResultRepository;
import com.skillswap.skill.entity.Skill;
import com.skillswap.skill.entity.SkillType;
import com.skillswap.skill.entity.UserSkill;
import com.skillswap.skill.repository.UserSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MatchingService {

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final MatchResultRepository matchResultRepository;

    public MatchingService(
            UserRepository userRepository,
            UserSkillRepository userSkillRepository,
            MatchResultRepository matchResultRepository
    ) {
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
        this.matchResultRepository = matchResultRepository;
    }

    /**
     * Generates rule-based matches for the authenticated user.
     *
     * The first version focuses on skill compatibility.
     */
    public List<MatchResponse> generateMatches(
            String userEmail,
            int limit
    ) {
        User requester = getUserByEmail(userEmail);

        int safeLimit = Math.max(1, Math.min(limit, 100));

        List<UserSkill> requesterSkills =
                getActiveUserSkills(requester);

        if (requesterSkills.isEmpty()) {
            return List.of();
        }

        List<User> candidates =
                userRepository.findAll();

        List<MatchResult> generatedMatches = new ArrayList<>();

        for (User candidate : candidates) {

            if (candidate == null
                    || candidate.getId() == null
                    || requester.getId() == null
                    || candidate.getId().equals(requester.getId())) {
                continue;
            }

            List<UserSkill> candidateSkills =
                    getActiveUserSkills(candidate);

            if (candidateSkills.isEmpty()) {
                continue;
            }

            MatchCalculation calculation =
                    calculateMatch(
                            requesterSkills,
                            candidateSkills
                    );

            if (calculation.matchingSkillCount() <= 0) {
                continue;
            }

            MatchResult result =
                    new MatchResult(
                            requester,
                            candidate,
                            calculation.score(),
                            MatchAlgorithm.RULE_BASED
                    );

            result.setMatchingSkillCount(
                    calculation.matchingSkillCount()
            );

            result.setMatchReason(
                    calculation.reason()
            );

            generatedMatches.add(result);
        }

        generatedMatches.sort(
                Comparator.comparingDouble(
                        MatchResult::getOverallScore
                ).reversed()
        );

        List<MatchResult> limited =
                generatedMatches.stream()
                        .limit(safeLimit)
                        .toList();

        List<MatchResponse> responses = new ArrayList<>();

        for (MatchResult result : limited) {

            MatchResult saved =
                    matchResultRepository.save(result);

            responses.add(
                    MatchResponse.from(saved)
            );
        }

        return responses;
    }

    /**
     * Calculates fresh matches without persisting them.
     */
    @Transactional(readOnly = true)
    public List<MatchResponse> previewMatches(
            String userEmail,
            int limit
    ) {
        User requester = getUserByEmail(userEmail);

        int safeLimit = Math.max(1, Math.min(limit, 100));

        List<UserSkill> requesterSkills =
                getActiveUserSkills(requester);

        if (requesterSkills.isEmpty()) {
            return List.of();
        }

        List<MatchResponse> responses = new ArrayList<>();

        for (User candidate : userRepository.findAll()) {

            if (candidate == null
                    || candidate.getId() == null
                    || requester.getId() == null
                    || candidate.getId().equals(requester.getId())) {
                continue;
            }

            List<UserSkill> candidateSkills =
                    getActiveUserSkills(candidate);

            if (candidateSkills.isEmpty()) {
                continue;
            }

            MatchCalculation calculation =
                    calculateMatch(
                            requesterSkills,
                            candidateSkills
                    );

            if (calculation.matchingSkillCount() <= 0) {
                continue;
            }

            MatchResult result =
                    new MatchResult(
                            requester,
                            candidate,
                            calculation.score(),
                            MatchAlgorithm.RULE_BASED
                    );

            result.setMatchingSkillCount(
                    calculation.matchingSkillCount()
            );

            result.setMatchReason(
                    calculation.reason()
            );

            responses.add(
                    MatchResponse.from(result)
            );
        }

        responses.sort(
                Comparator.comparingDouble(
                        MatchResponse::overallScore
                ).reversed()
        );

        return responses.stream()
                .limit(safeLimit)
                .toList();
    }

    /**
     * Gets an existing match.
     */
    @Transactional(readOnly = true)
    public MatchResponse getMatch(
            String userEmail,
            Long matchId
    ) {
        User user = getUserByEmail(userEmail);

        MatchResult matchResult =
                matchResultRepository.findById(matchId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Match not found: " + matchId
                                )
                        );

        validateRequester(user, matchResult);

        return MatchResponse.from(matchResult);
    }

    /**
     * Marks an existing match as viewed.
     */
    public MatchResponse markViewed(
            String userEmail,
            Long matchId
    ) {
        User user = getUserByEmail(userEmail);

        MatchResult matchResult =
                matchResultRepository.findById(matchId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Match not found: " + matchId
                                )
                        );

        validateRequester(user, matchResult);

        matchResult.markViewed();

        return MatchResponse.from(
                matchResultRepository.save(matchResult)
        );
    }

    /**
     * Marks a match as interested.
     */
    public MatchResponse markInterested(
            String userEmail,
            Long matchId
    ) {
        User user = getUserByEmail(userEmail);

        MatchResult matchResult =
                matchResultRepository.findById(matchId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Match not found: " + matchId
                                )
                        );

        validateRequester(user, matchResult);

        matchResult.markInterested();

        return MatchResponse.from(
                matchResultRepository.save(matchResult)
        );
    }

    /**
     * Dismisses a match.
     */
    public MatchResponse dismissMatch(
            String userEmail,
            Long matchId
    ) {
        User user = getUserByEmail(userEmail);

        MatchResult matchResult =
                matchResultRepository.findById(matchId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Match not found: " + matchId
                                )
                        );

        validateRequester(user, matchResult);

        matchResult.markDismissed();

        return MatchResponse.from(
                matchResultRepository.save(matchResult)
        );
    }

    private User getUserByEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "User email is required"
            );
        }

        return userRepository
                .findByEmail(email.trim().toLowerCase())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    private List<UserSkill> getActiveUserSkills(User user) {

        List<UserSkill> all =
                userSkillRepository.findAll();

        if (all == null || all.isEmpty()) {
            return List.of();
        }

        return all.stream()
                .filter(skill ->
                        skill != null
                                && skill.getUser() != null
                                && skill.getUser().getId() != null
                                && user.getId() != null
                                && skill.getUser()
                                .getId()
                                .equals(user.getId())
                )
                .filter(UserSkill::isActive)
                .toList();
    }

    private void validateRequester(
            User user,
            MatchResult matchResult
    ) {
        if (matchResult.getRequesterUser() == null
                || matchResult.getRequesterUser().getId() == null
                || user.getId() == null
                || !matchResult.getRequesterUser()
                .getId()
                .equals(user.getId())) {

            throw new IllegalArgumentException(
                    "This match does not belong to the current user"
            );
        }
    }

    private MatchCalculation calculateMatch(
            List<UserSkill> requesterSkills,
            List<UserSkill> candidateSkills
    ) {
        int matchingSkills = 0;
        double totalScore = 0.0;
        Set<String> matchedNames = new HashSet<>();

        for (UserSkill requesterSkill : requesterSkills) {

            if (requesterSkill.getSkill() == null) {
                continue;
            }

            Skill requesterSkillEntity =
                    requesterSkill.getSkill();

            String requesterName =
                    normalizeSkillName(
                            requesterSkillEntity.getName()
                    );

            if (requesterName == null) {
                continue;
            }

            for (UserSkill candidateSkill : candidateSkills) {

                if (candidateSkill.getSkill() == null) {
                    continue;
                }

                Skill candidateSkillEntity =
                        candidateSkill.getSkill();

                String candidateName =
                        normalizeSkillName(
                                candidateSkillEntity.getName()
                        );

                if (!requesterName.equals(candidateName)) {
                    continue;
                }

                /*
                 * A useful exchange normally happens when one
                 * person wants to learn something the other can teach.
                 */
                boolean exchangeCompatible =
                        isExchangeCompatible(
                                requesterSkill,
                                candidateSkill
                        );

                if (!exchangeCompatible) {
                    continue;
                }

                if (!matchedNames.add(requesterName)) {
                    continue;
                }

                matchingSkills++;

                totalScore +=
                        calculateSkillScore(
                                requesterSkill,
                                candidateSkill
                        );

                break;
            }
        }

        if (matchingSkills == 0) {
            return new MatchCalculation(
                    0.0,
                    0,
                    null
            );
        }

        double score =
                totalScore / matchingSkills;

        score = Math.min(100.0, score);

        String reason =
                "Matched on "
                        + matchingSkills
                        + " compatible skill"
                        + (matchingSkills == 1 ? "" : "s");

        return new MatchCalculation(
                score,
                matchingSkills,
                reason
        );
    }

    private boolean isExchangeCompatible(
            UserSkill requesterSkill,
            UserSkill candidateSkill
    ) {
        SkillType requesterType =
                requesterSkill.getSkillType();

        SkillType candidateType =
                candidateSkill.getSkillType();

        if (requesterType == null
                || candidateType == null) {
            return true;
        }

        boolean requesterNeeds =
                requesterType == SkillType.LEARN
                        || requesterType == SkillType.BOTH;

        boolean candidateCanTeach =
                candidateType == SkillType.TEACH
                        || candidateType == SkillType.BOTH;

        return requesterNeeds && candidateCanTeach;
    }

    private double calculateSkillScore(
            UserSkill requesterSkill,
            UserSkill candidateSkill
    ) {
        double score = 60.0;

        if (requesterSkill.getProficiencyLevel() != null
                && candidateSkill.getProficiencyLevel() != null) {

            int difference =
                    Math.abs(
                            requesterSkill
                                    .getProficiencyLevel()
                                    .ordinal()
                                    -
                            candidateSkill
                                    .getProficiencyLevel()
                                    .ordinal()
                    );

            if (difference == 0) {
                score += 10.0;
            } else if (difference == 1) {
                score += 15.0;
            } else {
                score += 20.0;
            }
        }

    double experience =
        candidateSkill.getYearsOfExperience();

score += Math.min(
        10.0,
        Math.max(0.0, experience * 2.0)
);

        return Math.min(100.0, score);
    }

    private String normalizeSkillName(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value
                .trim()
                .toLowerCase();
    }

    private record MatchCalculation(
            double score,
            int matchingSkillCount,
            String reason
    ) {
    }
}