package com.skillswap.skill.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.skill.dto.SkillResponse;
import com.skillswap.skill.dto.SkillSummaryResponse;
import com.skillswap.skill.dto.UserSkillSummaryResponse;
import com.skillswap.skill.entity.ProficiencyLevel;
import com.skillswap.skill.entity.Skill;
import com.skillswap.skill.entity.SkillCategory;
import com.skillswap.skill.entity.SkillType;
import com.skillswap.skill.repository.SkillRepository;
import com.skillswap.skill.repository.UserSkillRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@Transactional
public class SkillDiscoveryService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillService skillService;
    private final SkillCategoryService skillCategoryService;

    public SkillDiscoveryService(
            SkillRepository skillRepository,
            UserSkillRepository userSkillRepository,
            SkillService skillService,
            SkillCategoryService skillCategoryService
    ) {
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.skillService = skillService;
        this.skillCategoryService = skillCategoryService;
    }

    // =========================================================
    // SEARCH SKILLS
    // =========================================================

    @Transactional
    public List<SkillSummaryResponse> searchSkills(
            String query
    ) {

        String normalizedQuery =
                normalizeQuery(query);

        if (normalizedQuery.isBlank()) {

            return skillService.getPopularSkills();
        }

        return skillRepository
                .searchActiveSkills(normalizedQuery)
                .stream()
                .map(SkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // PAGINATED SEARCH
    // =========================================================

    @Transactional
    public Page<SkillSummaryResponse> searchSkills(
            String query,
            int page,
            int size
    ) {

        page = normalizePage(page);
        size = normalizeSize(size);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.ASC,
                                "name"
                        )
                );

        String normalizedQuery =
                normalizeQuery(query);

        if (normalizedQuery.isBlank()) {

            return skillRepository
                    .findByActiveTrue(pageable)
                    .map(SkillSummaryResponse::from);
        }

        return skillRepository
                .searchActiveSkills(
                        normalizedQuery,
                        pageable
                )
                .map(SkillSummaryResponse::from);
    }

    // =========================================================
    // DISCOVER TEACHERS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    discoverTeachers(
            Long skillId
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        /*
         * Make sure the skill actually exists before performing
         * discovery.
         */
        Skill skill =
                skillService.getSkillEntity(skillId);

        if (!skill.isActive()) {

            throw new BadRequestException(
                    "Cannot discover teachers for an inactive skill"
            );
        }

        return userSkillRepository
                .findTeachersBySkillId(skillId)
                .stream()
                .map(UserSkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // DISCOVER LEARNERS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    discoverLearners(
            Long skillId
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        Skill skill =
                skillService.getSkillEntity(skillId);

        if (!skill.isActive()) {

            throw new BadRequestException(
                    "Cannot discover learners for an inactive skill"
            );
        }

        return userSkillRepository
                .findLearnersBySkillId(skillId)
                .stream()
                .map(UserSkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // VERIFIED TEACHERS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    discoverVerifiedTeachers(
            Long skillId
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        validateActiveSkill(skillId);

        return userSkillRepository
                .findVerifiedTeachersBySkillId(skillId)
                .stream()
                .map(UserSkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // EXPERIENCED TEACHERS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    discoverExperiencedTeachers(
            Long skillId
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        validateActiveSkill(skillId);

        return userSkillRepository
                .findExperiencedTeachersBySkillId(skillId)
                .stream()
                .map(UserSkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // HIGHEST RATED TEACHERS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    discoverHighestRatedTeachers(
            Long skillId
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        validateActiveSkill(skillId);

        return userSkillRepository
                .findHighestRatedTeachersBySkillId(skillId)
                .stream()
                .map(UserSkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // DISCOVER BY CATEGORY
    // =========================================================

    @Transactional
    public List<SkillResponse> discoverSkillsByCategory(
            SkillCategory category
    ) {

        if (category == null) {

            throw new BadRequestException(
                    "Skill category is required"
            );
        }

        skillCategoryService.validateCategory(
                category
        );

        return skillService.getSkillsByCategory(
                category
        );
    }

    // =========================================================
    // DISCOVER BY CATEGORY NAME
    // =========================================================

    @Transactional
    public List<SkillResponse> discoverSkillsByCategoryName(
            String categoryName
    ) {

        SkillCategory category =
                skillCategoryService
                        .validateCategoryName(
                                categoryName
                        );

        return discoverSkillsByCategory(
                category
        );
    }

    // =========================================================
    // POPULAR SKILLS
    // =========================================================

    @Transactional
    public List<SkillSummaryResponse>
    discoverPopularSkills() {

        return skillService.getPopularSkills();
    }

    // =========================================================
    // MOST USED SKILLS
    // =========================================================

    @Transactional
    public List<SkillSummaryResponse>
    discoverMostUsedSkills() {

        return skillService.getMostUsedSkills();
    }

    // =========================================================
    // SKILL DETAILS
    // =========================================================

    @Transactional
    public SkillResponse getSkillDetails(
            Long skillId
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        return skillService.getSkillById(
                skillId
        );
    }

    // =========================================================
    // SKILL DISCOVERY OVERVIEW
    // =========================================================

    @Transactional
    public SkillOverview discoverSkillOverview(
            Long skillId
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        SkillResponse skill =
                skillService.getSkillById(
                        skillId
                );

        List<UserSkillSummaryResponse> teachers =
                discoverTeachers(skillId);

        List<UserSkillSummaryResponse> learners =
                discoverLearners(skillId);

        List<UserSkillSummaryResponse>
                verifiedTeachers =
                discoverVerifiedTeachers(skillId);

        List<UserSkillSummaryResponse>
                experiencedTeachers =
                discoverExperiencedTeachers(skillId);

        List<UserSkillSummaryResponse>
                highestRatedTeachers =
                discoverHighestRatedTeachers(skillId);

        return new SkillOverview(
                skill,
                teachers,
                learners,
                verifiedTeachers,
                experiencedTeachers,
                highestRatedTeachers
        );
    }

    // =========================================================
    // DISCOVERY STATISTICS
    // =========================================================

    @Transactional
    public DiscoveryStatistics
    getDiscoveryStatistics(
            Long skillId
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        Skill skill =
                skillService.getSkillEntity(
                        skillId
                );

        List<UserSkillSummaryResponse> teachers =
                userSkillRepository
                        .findTeachersBySkillId(
                                skillId
                        )
                        .stream()
                        .map(UserSkillSummaryResponse::from)
                        .toList();

        List<UserSkillSummaryResponse> learners =
                userSkillRepository
                        .findLearnersBySkillId(
                                skillId
                        )
                        .stream()
                        .map(UserSkillSummaryResponse::from)
                        .toList();

        long verifiedTeachers =
                teachers
                        .stream()
                        .filter(
                                UserSkillSummaryResponse::verified
                        )
                        .count();

        long experiencedTeachers =
                teachers
                        .stream()
                        .filter(
                                UserSkillSummaryResponse::experienced
                        )
                        .count();

        long ratedTeachers =
                teachers
                        .stream()
                        .filter(
                                teacher ->
                                        teacher.ratingCount() > 0
                        )
                        .count();

        double averageTeacherRating =
                teachers
                        .stream()
                        .filter(
                                teacher ->
                                        teacher.ratingCount() > 0
                        )
                        .mapToDouble(
                                UserSkillSummaryResponse::averageRating
                        )
                        .average()
                        .orElse(0.0);

        return new DiscoveryStatistics(
                skill.getId(),
                skill.getName(),
                skill.getCategory(),
                teachers.size(),
                learners.size(),
                verifiedTeachers,
                experiencedTeachers,
                ratedTeachers,
                roundRating(
                        averageTeacherRating
                ),
                skill.getUserCount(),
                skill.getPopularityScore()
        );
    }

    // =========================================================
    // FILTER TEACHERS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    filterTeachers(
            Long skillId,
            ProficiencyLevel proficiencyLevel,
            boolean verifiedOnly,
            double minimumExperience
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        validateActiveSkill(skillId);

        if (minimumExperience < 0) {

            throw new BadRequestException(
                    "Minimum experience cannot be negative"
            );
        }

        if (minimumExperience > 80) {

            throw new BadRequestException(
                    "Minimum experience cannot exceed 80 years"
            );
        }

        /*
         * Start from the repository's teacher collection.
         *
         * Additional filters are applied here so the discovery
         * layer can evolve without forcing controller changes.
         */
        List<UserSkillSummaryResponse> teachers =
                userSkillRepository
                        .findTeachersBySkillId(
                                skillId
                        )
                        .stream()
                        .map(UserSkillSummaryResponse::from)
                        .toList();

        return teachers
                .stream()
                .filter(
                        teacher ->
                                proficiencyLevel == null ||
                                        teacher.proficiencyLevel()
                                                == proficiencyLevel
                )
                .filter(
                        teacher ->
                                !verifiedOnly ||
                                        teacher.verified()
                )
                .filter(
                        teacher ->
                                teacher.yearsOfExperience()
                                        >= minimumExperience
                )
                .toList();
    }

    // =========================================================
    // FILTER LEARNERS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    filterLearners(
            Long skillId,
            ProficiencyLevel proficiencyLevel
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        validateActiveSkill(skillId);

        return userSkillRepository
                .findLearnersBySkillId(skillId)
                .stream()
                .map(UserSkillSummaryResponse::from)
                .filter(
                        learner ->
                                proficiencyLevel == null ||
                                        learner.proficiencyLevel()
                                                == proficiencyLevel
                )
                .toList();
    }

    // =========================================================
    // TEACHER SORTING
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    getTeachersSortedByExperience(
            Long skillId
    ) {

        return discoverTeachers(skillId)
                .stream()
                .sorted(
                        Comparator
                                .comparingDouble(
                                        UserSkillSummaryResponse::
                                                yearsOfExperience
                                )
                                .reversed()
                )
                .toList();
    }

    // =========================================================
    // TEACHER SORTING BY RATING
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    getTeachersSortedByRating(
            Long skillId
    ) {

        return discoverTeachers(skillId)
                .stream()
                .sorted(
                        Comparator
                                .comparingDouble(
                                        UserSkillSummaryResponse::
                                                averageRating
                                )
                                .reversed()
                                .thenComparing(
                                        Comparator
                                                .comparingLong(
                                                        UserSkillSummaryResponse::
                                                                ratingCount
                                                )
                                                .reversed()
                                )
                )
                .toList();
    }

    // =========================================================
    // TEACHER SORTING BY EXPERIENCE + RATING
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    getTeachersSortedByExperienceAndRating(
            Long skillId
    ) {

        return discoverTeachers(skillId)
                .stream()
                .sorted(
                        Comparator
                                .comparingDouble(
                                        UserSkillSummaryResponse::
                                                yearsOfExperience
                                )
                                .reversed()
                                .thenComparing(
                                        Comparator
                                                .comparingDouble(
                                                        UserSkillSummaryResponse::
                                                                averageRating
                                                )
                                                .reversed()
                                )
                )
                .toList();
    }

    // =========================================================
    // TOP TEACHERS
    // =========================================================
    //
    // This is deliberately NOT an AI ranking.
    //
    // It is a deterministic discovery helper using documented
    // profile attributes only.
    //
    // The actual AI matching/ranking engine comes later.
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    getTopTeacherCandidates(
            Long skillId,
            int limit
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        if (limit <= 0) {
            limit = 10;
        }

        limit = Math.min(
                limit,
                MAX_SIZE
        );

        return discoverTeachers(skillId)
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        UserSkillSummaryResponse::
                                                verified
                                )
                                .reversed()
                                .thenComparing(
                                        Comparator
                                                .comparingDouble(
                                                        UserSkillSummaryResponse::
                                                                averageRating
                                                )
                                                .reversed()
                                )
                                .thenComparing(
                                        Comparator
                                                .comparingLong(
                                                        UserSkillSummaryResponse::
                                                                ratingCount
                                                )
                                                .reversed()
                                )
                                .thenComparing(
                                        Comparator
                                                .comparingDouble(
                                                        UserSkillSummaryResponse::
                                                                yearsOfExperience
                                                )
                                                .reversed()
                                )
                )
                .limit(limit)
                .toList();
    }

    // =========================================================
    // SKILL TYPE INFORMATION
    // =========================================================

    public boolean supportsTeaching(
            SkillType skillType
    ) {

        return skillType == SkillType.TEACH ||
                skillType == SkillType.BOTH;
    }

    public boolean supportsLearning(
            SkillType skillType
    ) {

        return skillType == SkillType.LEARN ||
                skillType == SkillType.BOTH;
    }

    // =========================================================
    // DISCOVERY VALIDATION
    // =========================================================

    private void validateActiveSkill(
            Long skillId
    ) {

        Skill skill =
                skillService.getSkillEntity(
                        skillId
                );

        if (!skill.isActive()) {

            throw new BadRequestException(
                    "Skill is currently inactive"
            );
        }
    }

    private void validateId(
            Long id,
            String fieldName
    ) {

        if (id == null || id <= 0) {

            throw new BadRequestException(
                    fieldName +
                            " must be a valid positive number"
            );
        }
    }

    // =========================================================
    // QUERY NORMALIZATION
    // =========================================================

    private String normalizeQuery(
            String query
    ) {

        if (query == null) {
            return "";
        }

        return query
                .trim()
                .replaceAll("\\s+", " ");
    }

    // =========================================================
    // PAGINATION
    // =========================================================

    private int normalizePage(
            int page
    ) {

        return Math.max(
                DEFAULT_PAGE,
                page
        );
    }

    private int normalizeSize(
            int size
    ) {

        if (size <= 0) {
            return DEFAULT_SIZE;
        }

        return Math.min(
                size,
                MAX_SIZE
        );
    }

    // =========================================================
    // RATING NORMALIZATION
    // =========================================================

    private double roundRating(
            double rating
    ) {

        return Math.round(
                rating * 100.0
        ) / 100.0;
    }

    // =========================================================
    // RESPONSE OBJECTS
    // =========================================================

    public record SkillOverview(

            SkillResponse skill,

            List<UserSkillSummaryResponse> teachers,

            List<UserSkillSummaryResponse> learners,

            List<UserSkillSummaryResponse>
                    verifiedTeachers,

            List<UserSkillSummaryResponse>
                    experiencedTeachers,

            List<UserSkillSummaryResponse>
                    highestRatedTeachers

    ) {
    }

    public record DiscoveryStatistics(

            Long skillId,

            String skillName,

            SkillCategory category,

            long teacherCount,

            long learnerCount,

            long verifiedTeacherCount,

            long experiencedTeacherCount,

            long ratedTeacherCount,

            double averageTeacherRating,

            long totalUserCount,

            long popularityScore

    ) {
    }
}