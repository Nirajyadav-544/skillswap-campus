package com.skillswap.matching.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.matching.dto.MatchScoreBreakdownResponse;
import com.skillswap.matching.entity.MatchAlgorithm;
import com.skillswap.matching.entity.MatchResult;
import com.skillswap.matching.entity.MatchScore;
import com.skillswap.matching.repository.MatchResultRepository;
import com.skillswap.matching.repository.MatchScoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MatchScoreService {

    private final MatchScoreRepository matchScoreRepository;
    private final MatchResultRepository matchResultRepository;

    public MatchScoreService(
            MatchScoreRepository matchScoreRepository,
            MatchResultRepository matchResultRepository
    ) {
        this.matchScoreRepository = matchScoreRepository;
        this.matchResultRepository = matchResultRepository;
    }

    /**
     * Gets the detailed score breakdown for a match result.
     */
    @Transactional(readOnly = true)
    public MatchScoreBreakdownResponse getByMatchResultId(
            Long matchResultId
    ) {
        validateId(matchResultId, "Match result ID");

        MatchScore score = matchScoreRepository
                .findByMatchResultId(matchResultId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Match score not found for match result: "
                                        + matchResultId
                        )
                );

        return MatchScoreBreakdownResponse.from(score);
    }

    /**
     * Gets a score directly by its ID.
     */
    @Transactional(readOnly = true)
    public MatchScoreBreakdownResponse getById(
            Long scoreId
    ) {
        validateId(scoreId, "Match score ID");

        MatchScore score = matchScoreRepository
                .findById(scoreId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Match score not found: " + scoreId
                        )
                );

        return MatchScoreBreakdownResponse.from(score);
    }

    /**
     * Creates or recalculates the score for a MatchResult.
     *
     * The individual score components are expected to be populated
     * by the matching engine before this method is called.
     */
    public MatchScoreBreakdownResponse saveScore(
            Long matchResultId,
            double skillScore,
            double skillTypeScore,
            double proficiencyScore,
            double experienceScore,
            double profileScore,
            double availabilityScore,
            double locationScore,
            double verificationScore,
            double ratingScore,
            double collegeScore,
            double departmentScore,
            double courseScore,
            int matchingSkillCount,
            int strongMatchingSkillCount,
            MatchAlgorithm algorithm,
            String explanation
    ) {
        validateId(matchResultId, "Match result ID");

        MatchResult matchResult = matchResultRepository
                .findById(matchResultId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Match result not found: " + matchResultId
                        )
                );

        MatchScore score = matchScoreRepository
                .findByMatchResultId(matchResultId)
                .orElseGet(() -> new MatchScore(matchResult));

        score.setMatchResult(matchResult);

        score.setSkillScore(skillScore);
        score.setSkillTypeScore(skillTypeScore);
        score.setProficiencyScore(proficiencyScore);
        score.setExperienceScore(experienceScore);
        score.setProfileScore(profileScore);
        score.setAvailabilityScore(availabilityScore);
        score.setLocationScore(locationScore);
        score.setVerificationScore(verificationScore);
        score.setRatingScore(ratingScore);
        score.setCollegeScore(collegeScore);
        score.setDepartmentScore(departmentScore);
        score.setCourseScore(courseScore);

        score.setMatchingSkillCount(
                Math.max(0, matchingSkillCount)
        );

        score.setStrongMatchingSkillCount(
                Math.max(0, strongMatchingSkillCount)
        );

        score.setAlgorithm(
                algorithm != null
                        ? algorithm
                        : MatchAlgorithm.RULE_BASED
        );

        score.setScoreExplanation(explanation);

        score.calculateWeightedOverallScore();

        score.synchronizeWithMatchResult();

        MatchScore saved = matchScoreRepository.save(score);

        return MatchScoreBreakdownResponse.from(saved);
    }

    /**
     * Recalculates an existing score using its current components.
     */
    public MatchScoreBreakdownResponse recalculate(
            Long scoreId
    ) {
        validateId(scoreId, "Match score ID");

        MatchScore score = matchScoreRepository
                .findById(scoreId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Match score not found: " + scoreId
                        )
                );

        score.calculateWeightedOverallScore();
        score.synchronizeWithMatchResult();

        MatchScore saved = matchScoreRepository.save(score);

        return MatchScoreBreakdownResponse.from(saved);
    }

    /**
     * Synchronizes an existing score with its MatchResult.
     */
    public MatchScoreBreakdownResponse synchronize(
            Long scoreId
    ) {
        validateId(scoreId, "Match score ID");

        MatchScore score = matchScoreRepository
                .findById(scoreId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Match score not found: " + scoreId
                        )
                );

        score.synchronizeWithMatchResult();

        MatchScore saved = matchScoreRepository.save(score);

        return MatchScoreBreakdownResponse.from(saved);
    }

    /**
     * Gets all scores generated by a particular algorithm.
     */
    @Transactional(readOnly = true)
    public List<MatchScoreBreakdownResponse> getByAlgorithm(
            MatchAlgorithm algorithm
    ) {
        if (algorithm == null) {
            throw new BadRequestException(
                    "Match algorithm is required"
            );
        }

        return matchScoreRepository
                .findAllByAlgorithmOrderByOverallScoreDesc(algorithm)
                .stream()
                .map(MatchScoreBreakdownResponse::from)
                .toList();
    }

    /**
     * Gets paginated scores generated by an algorithm.
     */
    @Transactional(readOnly = true)
    public Page<MatchScoreBreakdownResponse> getByAlgorithm(
            MatchAlgorithm algorithm,
            Pageable pageable
    ) {
        if (algorithm == null) {
            throw new BadRequestException(
                    "Match algorithm is required"
            );
        }

        if (pageable == null) {
            throw new BadRequestException(
                    "Pageable is required"
            );
        }

        return matchScoreRepository
                .findAllByAlgorithmOrderByOverallScoreDesc(
                        algorithm,
                        pageable
                )
                .map(MatchScoreBreakdownResponse::from);
    }

    /**
     * Gets scores above a minimum overall score.
     */
    @Transactional(readOnly = true)
    public List<MatchScoreBreakdownResponse> getAboveScore(
            double minimumScore
    ) {
        validateScore(minimumScore);

        return matchScoreRepository
                .findAllByOverallScoreGreaterThanEqualOrderByOverallScoreDesc(
                        minimumScore
                )
                .stream()
                .map(MatchScoreBreakdownResponse::from)
                .toList();
    }

    /**
     * Gets paginated scores above a minimum overall score.
     */
    @Transactional(readOnly = true)
    public Page<MatchScoreBreakdownResponse> getAboveScore(
            double minimumScore,
            Pageable pageable
    ) {
        validateScore(minimumScore);

        if (pageable == null) {
            throw new BadRequestException(
                    "Pageable is required"
            );
        }

        return matchScoreRepository
                .findAllByOverallScoreGreaterThanEqualOrderByOverallScoreDesc(
                        minimumScore,
                        pageable
                )
                .map(MatchScoreBreakdownResponse::from);
    }

    /**
     * Gets scores having at least the specified number of matching skills.
     */
    @Transactional(readOnly = true)
    public List<MatchScoreBreakdownResponse> getByMatchingSkillCount(
            int minimumMatchingSkills
    ) {
        if (minimumMatchingSkills < 0) {
            throw new BadRequestException(
                    "Minimum matching skill count cannot be negative"
            );
        }

        return matchScoreRepository
                .findAllByMatchingSkillCountGreaterThanEqualOrderByOverallScoreDesc(
                        minimumMatchingSkills
                )
                .stream()
                .map(MatchScoreBreakdownResponse::from)
                .toList();
    }

    /**
     * Gets the strongest scores.
     */
    @Transactional(readOnly = true)
    public List<MatchScoreBreakdownResponse> getTopScores() {

        return matchScoreRepository
                .findTop20ByOrderByOverallScoreDesc()
                .stream()
                .map(MatchScoreBreakdownResponse::from)
                .toList();
    }

    /**
     * Gets the strongest skill-compatible scores.
     */
    @Transactional(readOnly = true)
    public List<MatchScoreBreakdownResponse> getTopSkillScores() {

        return matchScoreRepository
                .findTop20ByOrderBySkillScoreDesc()
                .stream()
                .map(MatchScoreBreakdownResponse::from)
                .toList();
    }

    /**
     * Gets scores with strong skill compatibility.
     */
    @Transactional(readOnly = true)
    public List<MatchScoreBreakdownResponse> getBySkillScore(
            double minimumSkillScore
    ) {
        validateScore(minimumSkillScore);

        return matchScoreRepository
                .findAllBySkillScoreGreaterThanEqualOrderByOverallScoreDesc(
                        minimumSkillScore
                )
                .stream()
                .map(MatchScoreBreakdownResponse::from)
                .toList();
    }

    /**
     * Gets scores with strong proficiency compatibility.
     */
    @Transactional(readOnly = true)
    public List<MatchScoreBreakdownResponse> getByProficiencyScore(
            double minimumProficiencyScore
    ) {
        validateScore(minimumProficiencyScore);

        return matchScoreRepository
                .findAllByProficiencyScoreGreaterThanEqualOrderByOverallScoreDesc(
                        minimumProficiencyScore
                )
                .stream()
                .map(MatchScoreBreakdownResponse::from)
                .toList();
    }

    /**
     * Gets scores with strong experience compatibility.
     */
    @Transactional(readOnly = true)
    public List<MatchScoreBreakdownResponse> getByExperienceScore(
            double minimumExperienceScore
    ) {
        validateScore(minimumExperienceScore);

        return matchScoreRepository
                .findAllByExperienceScoreGreaterThanEqualOrderByOverallScoreDesc(
                        minimumExperienceScore
                )
                .stream()
                .map(MatchScoreBreakdownResponse::from)
                .toList();
    }

    /**
     * Gets scores with strong profile compatibility.
     */
    @Transactional(readOnly = true)
    public List<MatchScoreBreakdownResponse> getByProfileScore(
            double minimumProfileScore
    ) {
        validateScore(minimumProfileScore);

        return matchScoreRepository
                .findAllByProfileScoreGreaterThanEqualOrderByOverallScoreDesc(
                        minimumProfileScore
                )
                .stream()
                .map(MatchScoreBreakdownResponse::from)
                .toList();
    }

    /**
     * Counts scores generated by an algorithm.
     */
    @Transactional(readOnly = true)
    public long countByAlgorithm(
            MatchAlgorithm algorithm
    ) {
        if (algorithm == null) {
            throw new BadRequestException(
                    "Match algorithm is required"
            );
        }

        return matchScoreRepository.countByAlgorithm(algorithm);
    }

    /**
     * Counts scores above a minimum overall score.
     */
    @Transactional(readOnly = true)
    public long countAboveScore(
            double minimumScore
    ) {
        validateScore(minimumScore);

        return matchScoreRepository
                .countByOverallScoreGreaterThanEqual(
                        minimumScore
                );
    }

    /**
     * Counts scores containing matching skills.
     */
    @Transactional(readOnly = true)
    public long countWithMatchingSkills(
            int minimumMatchingSkills
    ) {
        if (minimumMatchingSkills < 0) {
            throw new BadRequestException(
                    "Minimum matching skill count cannot be negative"
            );
        }

        return matchScoreRepository
                .countByMatchingSkillCountGreaterThanEqual(
                        minimumMatchingSkills
                );
    }

    /**
     * Deletes a score by score ID.
     */
    public void delete(
            Long scoreId
    ) {
        validateId(scoreId, "Match score ID");

        if (!matchScoreRepository.existsById(scoreId)) {
            throw new ResourceNotFoundException(
                    "Match score not found: " + scoreId
            );
        }

        matchScoreRepository.deleteById(scoreId);
    }

    /**
     * Deletes the score belonging to a MatchResult.
     */
    public void deleteByMatchResultId(
            Long matchResultId
    ) {
        validateId(matchResultId, "Match result ID");

        matchScoreRepository.deleteByMatchResultId(
                matchResultId
        );
    }

    /**
     * Checks whether a score exists for a MatchResult.
     */
    @Transactional(readOnly = true)
    public boolean existsForMatchResult(
            Long matchResultId
    ) {
        validateId(matchResultId, "Match result ID");

        return matchScoreRepository.existsByMatchResultId(
                matchResultId
        );
    }

    private void validateId(
            Long id,
            String fieldName
    ) {
        if (id == null || id <= 0) {
            throw new BadRequestException(
                    fieldName + " must be a positive number"
            );
        }
    }

    private void validateScore(
            double score
    ) {
        if (Double.isNaN(score)
                || Double.isInfinite(score)) {
            throw new BadRequestException(
                    "Score must be a valid number"
            );
        }

        if (score < 0.0 || score > 100.0) {
            throw new BadRequestException(
                    "Score must be between 0 and 100"
            );
        }
    }
}