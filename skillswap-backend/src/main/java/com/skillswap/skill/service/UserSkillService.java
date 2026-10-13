package com.skillswap.skill.service;

import com.skillswap.activity.entity.ActivityType;
import com.skillswap.activity.service.ActivityService;
import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.skill.dto.AddUserSkillRequest;
import com.skillswap.skill.dto.UpdateUserSkillRequest;
import com.skillswap.skill.dto.UserSkillResponse;
import com.skillswap.skill.dto.UserSkillSummaryResponse;
import com.skillswap.skill.entity.Skill;
import com.skillswap.skill.entity.SkillType;
import com.skillswap.skill.entity.UserSkill;
import com.skillswap.skill.repository.UserSkillRepository;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserSkillService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SkillService skillService;
    private final ActivityService activityService;

    public UserSkillService(
            UserSkillRepository userSkillRepository,
            UserRepository userRepository,
            SkillService skillService,
            ActivityService activityService
    ) {
        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
        this.skillService = skillService;
        this.activityService = activityService;
    }

    // =========================================================
    // ADD USER SKILL
    // =========================================================

    public UserSkillResponse addUserSkill(
            Long userId,
            AddUserSkillRequest request
    ) {

        validateUserId(userId);

        if (request == null) {
            throw new BadRequestException(
                    "User skill request cannot be null"
            );
        }

        User user = getUser(userId);

        Skill skill =
                skillService.getActiveSkillEntity(
                        request.skillId()
                );

        if (userSkillRepository.existsByUserAndSkill(
                user,
                skill
        )) {

            throw new BadRequestException(
                    "You have already added this skill"
            );
        }

        validateSkillType(request.skillType());

        validateExperience(
                request.yearsOfExperience()
        );

        UserSkill userSkill = new UserSkill();

        userSkill.setUser(user);
        userSkill.setSkill(skill);
        userSkill.setSkillType(request.skillType());
        userSkill.setProficiencyLevel(
                request.proficiencyLevel()
        );
        userSkill.setYearsOfExperience(
                request.yearsOfExperience()
        );
        userSkill.setDescription(
                normalizeDescription(
                        request.description()
                )
        );

        userSkill.setActive(true);
        userSkill.setVerified(false);
        userSkill.setExchangeCount(0);
        userSkill.setSuccessfulExchangeCount(0);
        userSkill.setAverageRating(0.0);
        userSkill.setRatingCount(0);

        UserSkill saved =
                userSkillRepository.save(userSkill);

        // Maintain denormalized skill counters.
        skillService.incrementUserCount(
                skill.getId()
        );

        if (saved.canTeach()) {
            skillService.incrementPopularity(
                    skill.getId()
            );
        }

        // Create activity.
        activityService.createActivity(
                user,
                ActivityType.SKILL_ADDED,
                "Added " + skill.getName() + " skill",
                saved.getId(),
                "USER_SKILL"
        );

        return UserSkillResponse.from(saved);
    }

    // =========================================================
    // GET USER SKILL
    // =========================================================

    @Transactional
    public UserSkillResponse getUserSkill(
            Long userId,
            Long userSkillId
    ) {

        UserSkill userSkill =
                getOwnedUserSkill(
                        userId,
                        userSkillId
                );

        return UserSkillResponse.from(userSkill);
    }

    // =========================================================
    // GET USER SKILL BY SKILL ID
    // =========================================================

    @Transactional
    public UserSkillResponse getUserSkillBySkillId(
            Long userId,
            Long skillId
    ) {

        validateUserId(userId);
        validateId(skillId, "Skill ID");

        UserSkill userSkill =
                userSkillRepository
                        .findByUserIdAndSkillId(
                                userId,
                                skillId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "You have not added this skill"
                                )
                        );

        return UserSkillResponse.from(userSkill);
    }

    // =========================================================
    // GET ALL USER SKILLS
    // =========================================================

    @Transactional
    public List<UserSkillResponse> getUserSkills(
            Long userId
    ) {

        validateUserId(userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with ID: " + userId
            );
        }

        return userSkillRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(UserSkillResponse::from)
                .toList();
    }

    // =========================================================
    // GET ACTIVE USER SKILLS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse> getActiveUserSkills(
            Long userId
    ) {

        validateUserId(userId);

        return userSkillRepository
                .findByUserIdAndActiveTrueOrderByCreatedAtDesc(
                        userId
                )
                .stream()
                .map(UserSkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // GET TEACHING SKILLS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse> getTeachingSkills(
            Long userId
    ) {

        validateUserId(userId);

        return userSkillRepository
                .findTeachingSkillsByUserId(userId)
                .stream()
                .map(UserSkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // GET LEARNING SKILLS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse> getLearningSkills(
            Long userId
    ) {

        validateUserId(userId);

        return userSkillRepository
                .findLearningSkillsByUserId(userId)
                .stream()
                .map(UserSkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // UPDATE
    // =========================================================

    public UserSkillResponse updateUserSkill(
            Long userId,
            Long userSkillId,
            UpdateUserSkillRequest request
    ) {

        if (request == null) {
            throw new BadRequestException(
                    "Update request cannot be null"
            );
        }

        UserSkill userSkill =
                getOwnedUserSkill(
                        userId,
                        userSkillId
                );

        SkillType oldType =
                userSkill.getSkillType();

        SkillType newType =
                request.skillType();

        validateSkillType(newType);

        validateExperience(
                request.yearsOfExperience()
        );

        userSkill.setSkillType(newType);
        userSkill.setProficiencyLevel(
                request.proficiencyLevel()
        );
        userSkill.setYearsOfExperience(
                request.yearsOfExperience()
        );
        userSkill.setDescription(
                normalizeDescription(
                        request.description()
                )
        );

        boolean wasActive =
                userSkill.isActive();

        boolean shouldBeActive =
                request.active();

        userSkill.setActive(
                shouldBeActive
        );

        UserSkill saved =
                userSkillRepository.save(userSkill);

        /*
         * If teaching capability changed, update the
         * popularity counter accordingly.
         */
        if (wasActive != shouldBeActive ||
                oldType != newType) {

            Skill skill = userSkill.getSkill();

            if (skill != null) {

                boolean previouslyCounted =
                        wasActive &&
                                canTeach(oldType);

                boolean currentlyCounted =
                        shouldBeActive &&
                                canTeach(newType);

                if (!previouslyCounted &&
                        currentlyCounted) {

                    skillService.incrementPopularity(
                            skill.getId()
                    );

                } else if (previouslyCounted &&
                        !currentlyCounted) {

                    skillService.decrementPopularity(
                            skill.getId()
                    );
                }
            }
        }

        // Create activity.
        Skill skill = userSkill.getSkill();

        if (skill != null) {
            activityService.createActivity(
                    userSkill.getUser(),
                    ActivityType.SKILL_UPDATED,
                    "Updated " + skill.getName() + " skill",
                    saved.getId(),
                    "USER_SKILL"
            );
        }

        return UserSkillResponse.from(saved);
    }

    // =========================================================
    // ACTIVATE
    // =========================================================

    public UserSkillResponse activateUserSkill(
            Long userId,
            Long userSkillId
    ) {

        UserSkill userSkill =
                getOwnedUserSkill(
                        userId,
                        userSkillId
                );

        if (userSkill.isActive()) {
            return UserSkillResponse.from(userSkill);
        }

        userSkill.activate();

        UserSkill saved =
                userSkillRepository.save(userSkill);

        if (saved.canTeach()) {
            skillService.incrementPopularity(
                    saved.getSkill().getId()
            );
        }

        return UserSkillResponse.from(saved);
    }

    // =========================================================
    // DEACTIVATE
    // =========================================================

    public UserSkillResponse deactivateUserSkill(
            Long userId,
            Long userSkillId
    ) {

        UserSkill userSkill =
                getOwnedUserSkill(
                        userId,
                        userSkillId
                );

        if (!userSkill.isActive()) {
            return UserSkillResponse.from(userSkill);
        }

        boolean wasTeaching =
                userSkill.canTeach();

        userSkill.deactivate();

        UserSkill saved =
                userSkillRepository.save(userSkill);

        if (wasTeaching) {
            skillService.decrementPopularity(
                    saved.getSkill().getId()
            );
        }

        return UserSkillResponse.from(saved);
    }

    // =========================================================
    // VERIFY USER SKILL
    // =========================================================

    public UserSkillResponse verifyUserSkill(
            Long userSkillId
    ) {

        validateId(
                userSkillId,
                "User skill ID"
        );

        UserSkill userSkill =
                userSkillRepository
                        .findById(userSkillId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User skill not found with ID: " +
                                                userSkillId
                                )
                        );

        userSkill.markVerified();

        return UserSkillResponse.from(
                userSkillRepository.save(userSkill)
        );
    }

    // =========================================================
    // UNVERIFY USER SKILL
    // =========================================================

    public UserSkillResponse unverifyUserSkill(
            Long userSkillId
    ) {

        validateId(
                userSkillId,
                "User skill ID"
        );

        UserSkill userSkill =
                userSkillRepository
                        .findById(userSkillId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User skill not found with ID: " +
                                                userSkillId
                                )
                        );

        userSkill.unverify();

        return UserSkillResponse.from(
                userSkillRepository.save(userSkill)
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    public void deleteUserSkill(
            Long userId,
            Long userSkillId
    ) {

        UserSkill userSkill =
                getOwnedUserSkill(
                        userId,
                        userSkillId
                );

        Skill skill =
                userSkill.getSkill();

        boolean active =
                userSkill.isActive();

        boolean teaching =
                userSkill.canTeach();

        /*
         * Save activity information before deleting the entity.
         */
        String skillName =
                skill != null
                        ? skill.getName()
                        : "skill";

        User user =
                userSkill.getUser();

        userSkillRepository.delete(userSkill);

        if (skill != null) {

            skillService.decrementUserCount(
                    skill.getId()
            );

            if (active && teaching) {
                skillService.decrementPopularity(
                        skill.getId()
                );
            }
        }

        // Create activity after successful delete.
        activityService.createActivity(
                user,
                ActivityType.SKILL_DELETED,
                "Removed " + skillName + " skill",
                userSkillId,
                "USER_SKILL"
        );
    }

    // =========================================================
    // REMOVE BY SKILL ID
    // =========================================================

    public void deleteUserSkillBySkillId(
            Long userId,
            Long skillId
    ) {

        validateUserId(userId);
        validateId(skillId, "Skill ID");

        UserSkill userSkill =
                userSkillRepository
                        .findByUserIdAndSkillId(
                                userId,
                                skillId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "You have not added this skill"
                                )
                        );

        deleteUserSkill(
                userId,
                userSkill.getId()
        );
    }

    // =========================================================
    // CHECK OWNERSHIP
    // =========================================================

    @Transactional
    public boolean ownsUserSkill(
            Long userId,
            Long userSkillId
    ) {

        validateUserId(userId);
        validateId(
                userSkillId,
                "User skill ID"
        );

        return userSkillRepository
                .existsByIdAndUserId(
                        userSkillId,
                        userId
                );
    }

    // =========================================================
    // GET ENTITY
    // =========================================================

    @Transactional
    public UserSkill getUserSkillEntity(
            Long userSkillId
    ) {

        validateId(
                userSkillId,
                "User skill ID"
        );

        return userSkillRepository
                .findById(userSkillId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User skill not found with ID: " +
                                        userSkillId
                        )
                );
    }

    // =========================================================
    // GET OWNED ENTITY
    // =========================================================

    @Transactional
    public UserSkill getOwnedUserSkill(
            Long userId,
            Long userSkillId
    ) {

        validateUserId(userId);

        validateId(
                userSkillId,
                "User skill ID"
        );

        return userSkillRepository
                .findByIdAndUserId(
                        userSkillId,
                        userId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User skill not found for this user"
                        )
                );
    }

    // =========================================================
    // DISCOVERY - TEACHERS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse> getTeachersForSkill(
            Long skillId
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        return userSkillRepository
                .findTeachersBySkillId(skillId)
                .stream()
                .map(UserSkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // DISCOVERY - LEARNERS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse> getLearnersForSkill(
            Long skillId
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        return userSkillRepository
                .findLearnersBySkillId(skillId)
                .stream()
                .map(UserSkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // DISCOVERY - VERIFIED TEACHERS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    getVerifiedTeachersForSkill(
            Long skillId
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        return userSkillRepository
                .findVerifiedTeachersBySkillId(skillId)
                .stream()
                .map(UserSkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // DISCOVERY - EXPERIENCED TEACHERS
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    getExperiencedTeachersForSkill(
            Long skillId
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        return userSkillRepository
                .findExperiencedTeachersBySkillId(
                        skillId
                )
                .stream()
                .map(UserSkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // DISCOVERY - HIGHEST RATED
    // =========================================================

    @Transactional
    public List<UserSkillSummaryResponse>
    getHighestRatedTeachersForSkill(
            Long skillId
    ) {

        validateId(
                skillId,
                "Skill ID"
        );

        return userSkillRepository
                .findHighestRatedTeachersBySkillId(
                        skillId
                )
                .stream()
                .map(UserSkillSummaryResponse::from)
                .toList();
    }

    // =========================================================
    // PAGINATED USER SKILLS
    // =========================================================

    @Transactional
    public Page<UserSkillSummaryResponse>
    getUserSkills(
            Long userId,
            int page,
            int size
    ) {

        validateUserId(userId);

        Pageable pageable =
                PageRequest.of(
                        normalizePage(page),
                        normalizePageSize(size),
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        return userSkillRepository
                .findByUserId(
                        userId,
                        pageable
                )
                .map(UserSkillSummaryResponse::from);
    }

    // =========================================================
    // RECORD EXCHANGE
    // =========================================================

    public UserSkillResponse recordExchange(
            Long userSkillId
    ) {

        UserSkill userSkill =
                getUserSkillEntity(userSkillId);

        userSkill.recordExchange();

        return UserSkillResponse.from(
                userSkillRepository.save(userSkill)
        );
    }

    // =========================================================
    // RECORD SUCCESSFUL EXCHANGE
    // =========================================================

    public UserSkillResponse recordSuccessfulExchange(
            Long userSkillId
    ) {

        UserSkill userSkill =
                getUserSkillEntity(userSkillId);

        userSkill.recordSuccessfulExchange();

        return UserSkillResponse.from(
                userSkillRepository.save(userSkill)
        );
    }

    // =========================================================
    // ADD RATING
    // =========================================================

    public UserSkillResponse addRating(
            Long userSkillId,
            double rating
    ) {

        UserSkill userSkill =
                getUserSkillEntity(userSkillId);

        validateRating(rating);

        userSkill.addRating(rating);

        return UserSkillResponse.from(
                userSkillRepository.save(userSkill)
        );
    }

    // =========================================================
    // ACTIVITY
    // =========================================================

    public UserSkillResponse markUsed(
            Long userSkillId
    ) {

        UserSkill userSkill =
                getUserSkillEntity(userSkillId);

        userSkill.setLastUsedAt(
                java.time.LocalDateTime.now()
        );

        return UserSkillResponse.from(
                userSkillRepository.save(userSkill)
        );
    }

    // =========================================================
    // PRIVATE USER LOOKUP
    // =========================================================

    private User getUser(Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " +
                                        userId
                        )
                );
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateUserId(Long userId) {

        validateId(userId, "User ID");
    }

    private void validateId(
            Long id,
            String fieldName
    ) {

        if (id == null || id <= 0) {
            throw new BadRequestException(
                    fieldName + " must be a valid positive number"
            );
        }
    }

    private void validateSkillType(
            SkillType skillType
    ) {

        if (skillType == null) {
            throw new BadRequestException(
                    "Skill type is required"
            );
        }
    }

    private void validateExperience(
            double yearsOfExperience
    ) {

        if (Double.isNaN(yearsOfExperience) ||
                Double.isInfinite(yearsOfExperience)) {

            throw new BadRequestException(
                    "Years of experience must be a valid number"
            );
        }

        if (yearsOfExperience < 0) {
            throw new BadRequestException(
                    "Years of experience cannot be negative"
            );
        }

        if (yearsOfExperience > 80) {
            throw new BadRequestException(
                    "Years of experience is invalid"
            );
        }
    }

    private void validateRating(
            double rating
    ) {

        if (Double.isNaN(rating) ||
                Double.isInfinite(rating)) {

            throw new BadRequestException(
                    "Rating must be a valid number"
            );
        }

        if (rating < 1.0 || rating > 5.0) {
            throw new BadRequestException(
                    "Rating must be between 1 and 5"
            );
        }
    }

    // =========================================================
    // SKILL TYPE HELPERS
    // =========================================================

    private boolean canTeach(
            SkillType skillType
    ) {

        return skillType == SkillType.TEACH ||
                skillType == SkillType.BOTH;
    }

    private boolean wantsToLearn(
            SkillType skillType
    ) {

        return skillType == SkillType.LEARN ||
                skillType == SkillType.BOTH;
    }

    // =========================================================
    // DESCRIPTION NORMALIZATION
    // =========================================================

    private String normalizeDescription(
            String description
    ) {

        if (description == null) {
            return null;
        }

        String normalized =
                description.trim();

        return normalized.isBlank()
                ? null
                : normalized;
    }

    // =========================================================
    // PAGINATION
    // =========================================================

    private int normalizePage(int page) {

        return Math.max(
                page,
                0
        );
    }

    private int normalizePageSize(int size) {

        if (size <= 0) {
            return DEFAULT_PAGE_SIZE;
        }

        return Math.min(
                size,
                MAX_PAGE_SIZE
        );
    }
}