package com.skillswap.skill.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.skill.entity.ProficiencyLevel;
import com.skillswap.skill.entity.Skill;
import com.skillswap.skill.entity.SkillType;
import com.skillswap.skill.entity.UserSkill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserSkill relationships.
 *
 * UserSkill represents a user's relationship with a particular skill,
 * including:
 *
 * - Teach / Learn / Both
 * - Proficiency
 * - Experience
 * - Verification
 * - Activity
 * - Ratings
 * - Exchange history
 */
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    // ============================================================
    // BASIC USER + SKILL LOOKUPS
    // ============================================================

    Optional<UserSkill> findByUserAndSkill(
            User user,
            Skill skill
    );

    Optional<UserSkill> findByUserIdAndSkillId(
            Long userId,
            Long skillId
    );

    boolean existsByUserAndSkill(
            User user,
            Skill skill
    );

    boolean existsByUserIdAndSkillId(
            Long userId,
            Long skillId
    );

    /**
     * Required by UserSkillService.
     */
    boolean existsByIdAndUserId(
            Long id,
            Long userId
    );

    /**
     * Required by UserSkillService.
     */
    Optional<UserSkill> findByIdAndUserId(
            Long id,
            Long userId
    );


    // ============================================================
    // USER SKILLS
    // ============================================================

    List<UserSkill> findAllByUser(
            User user
    );

    List<UserSkill> findAllByUserAndActiveTrue(
            User user
    );

    List<UserSkill> findAllByUserOrderByCreatedAtDesc(
            User user
    );

    List<UserSkill> findAllByUserAndActiveTrueOrderByCreatedAtDesc(
            User user
    );

    /**
     * Required by UserSkillService.
     */
    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.user.id = :userId
            ORDER BY us.createdAt DESC
            """)
    List<UserSkill> findByUserIdOrderByCreatedAtDesc(
            @Param("userId") Long userId
    );

    /**
     * Required by UserSkillService.
     */
    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.user.id = :userId
              AND us.active = true
            ORDER BY us.createdAt DESC
            """)
    List<UserSkill> findByUserIdAndActiveTrueOrderByCreatedAtDesc(
            @Param("userId") Long userId
    );


    // ============================================================
    // TEACHING / LEARNING
    // ============================================================

    List<UserSkill>
    findAllByUserAndSkillTypeInAndActiveTrue(
            User user,
            List<SkillType> skillTypes
    );

    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.user.id = :userId
              AND us.active = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.TEACH
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.proficiencyLevel DESC,
                     us.yearsOfExperience DESC
            """)
    List<UserSkill> findTeachingSkills(
            @Param("userId") Long userId
    );

    /**
     * Required by UserSkillService.
     */
    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.user.id = :userId
              AND us.active = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.TEACH
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.proficiencyLevel DESC,
                     us.yearsOfExperience DESC
            """)
    List<UserSkill> findTeachingSkillsByUserId(
            @Param("userId") Long userId
    );

    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.user.id = :userId
              AND us.active = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.LEARN
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.proficiencyLevel ASC,
                     us.yearsOfExperience ASC
            """)
    List<UserSkill> findLearningSkills(
            @Param("userId") Long userId
    );

    /**
     * Required by UserSkillService.
     */
    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.user.id = :userId
              AND us.active = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.LEARN
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.proficiencyLevel ASC,
                     us.yearsOfExperience ASC
            """)
    List<UserSkill> findLearningSkillsByUserId(
            @Param("userId") Long userId
    );


    // ============================================================
    // SKILL LOOKUPS
    // ============================================================

    List<UserSkill> findAllBySkillAndActiveTrue(
            Skill skill
    );

    List<UserSkill> findAllBySkillIdAndActiveTrue(
            Long skillId
    );


    // ============================================================
    // TEACHERS FOR SKILL
    // ============================================================

    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.skill.id = :skillId
              AND us.active = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.TEACH
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.verified DESC,
                     us.proficiencyLevel DESC,
                     us.yearsOfExperience DESC,
                     us.averageRating DESC
            """)
    List<UserSkill> findTeachersForSkill(
            @Param("skillId") Long skillId
    );

    /**
     * Required by UserSkillService / SkillDiscoveryService.
     */
    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.skill.id = :skillId
              AND us.active = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.TEACH
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.verified DESC,
                     us.proficiencyLevel DESC,
                     us.yearsOfExperience DESC,
                     us.averageRating DESC
            """)
    List<UserSkill> findTeachersBySkillId(
            @Param("skillId") Long skillId
    );


    // ============================================================
    // LEARNERS FOR SKILL
    // ============================================================

    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.skill.id = :skillId
              AND us.active = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.LEARN
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.proficiencyLevel ASC,
                     us.yearsOfExperience ASC
            """)
    List<UserSkill> findLearnersForSkill(
            @Param("skillId") Long skillId
    );

    /**
     * Required by UserSkillService / SkillDiscoveryService.
     */
    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.skill.id = :skillId
              AND us.active = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.LEARN
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.proficiencyLevel ASC,
                     us.yearsOfExperience ASC
            """)
    List<UserSkill> findLearnersBySkillId(
            @Param("skillId") Long skillId
    );


    // ============================================================
    // VERIFIED TEACHERS
    // ============================================================

    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.skill.id = :skillId
              AND us.active = true
              AND us.verified = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.TEACH
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.proficiencyLevel DESC,
                     us.yearsOfExperience DESC,
                     us.averageRating DESC
            """)
    List<UserSkill> findVerifiedTeachersForSkill(
            @Param("skillId") Long skillId
    );

    /**
     * Required by UserSkillService / SkillDiscoveryService.
     */
    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.skill.id = :skillId
              AND us.active = true
              AND us.verified = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.TEACH
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.proficiencyLevel DESC,
                     us.yearsOfExperience DESC,
                     us.averageRating DESC
            """)
    List<UserSkill> findVerifiedTeachersBySkillId(
            @Param("skillId") Long skillId
    );


    // ============================================================
    // PROFICIENCY
    // ============================================================

    List<UserSkill>
    findAllBySkillAndProficiencyLevelAndActiveTrue(
            Skill skill,
            ProficiencyLevel proficiencyLevel
    );

    List<UserSkill>
    findAllByVerifiedTrueAndActiveTrue();

    List<UserSkill>
    findAllBySkillAndVerifiedTrueAndActiveTrue(
            Skill skill
    );


    // ============================================================
    // PAGINATION
    // ============================================================

    Page<UserSkill> findAllByUser(
            User user,
            Pageable pageable
    );

    Page<UserSkill> findAllByUserAndActiveTrue(
            User user,
            Pageable pageable
    );

    /**
     * Required by UserSkillService.
     */
    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.user.id = :userId
            """)
    Page<UserSkill> findByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );


    // ============================================================
    // PAGINATED TEACHERS
    // ============================================================

    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.active = true
              AND us.skill.id = :skillId
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.TEACH
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            """)
    Page<UserSkill> findTeachersForSkill(
            @Param("skillId") Long skillId,
            Pageable pageable
    );


    // ============================================================
    // PAGINATED LEARNERS
    // ============================================================

    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.active = true
              AND us.skill.id = :skillId
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.LEARN
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            """)
    Page<UserSkill> findLearnersForSkill(
            @Param("skillId") Long skillId,
            Pageable pageable
    );


    // ============================================================
    // USER + TYPE / PROFICIENCY
    // ============================================================

    List<UserSkill>
    findAllByUserAndSkillTypeAndActiveTrue(
            User user,
            SkillType skillType
    );

    List<UserSkill>
    findAllByUserAndProficiencyLevelAndActiveTrue(
            User user,
            ProficiencyLevel proficiencyLevel
    );


    // ============================================================
    // COUNTS
    // ============================================================

    long countByUserAndActiveTrue(
            User user
    );

    long countBySkillAndActiveTrue(
            Skill skill
    );

    long countBySkillAndVerifiedTrueAndActiveTrue(
            Skill skill
    );

    @Query("""
            SELECT COUNT(us)
            FROM UserSkill us
            WHERE us.skill.id = :skillId
              AND us.active = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.TEACH
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            """)
    long countTeachersForSkill(
            @Param("skillId") Long skillId
    );

    @Query("""
            SELECT COUNT(us)
            FROM UserSkill us
            WHERE us.skill.id = :skillId
              AND us.active = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.LEARN
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            """)
    long countLearnersForSkill(
            @Param("skillId") Long skillId
    );


    // ============================================================
    // EXPERIENCE
    // ============================================================

    List<UserSkill>
    findAllBySkillAndYearsOfExperienceGreaterThanEqualAndActiveTrue(
            Skill skill,
            double yearsOfExperience
    );

    /**
     * Required by UserSkillService / SkillDiscoveryService.
     *
     * Uses a default minimum experience of 0 so the method
     * returns experienced active teachers ordered by experience.
     */
    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.skill.id = :skillId
              AND us.active = true
              AND us.yearsOfExperience >= 0
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.TEACH
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.yearsOfExperience DESC,
                     us.averageRating DESC
            """)
    List<UserSkill> findExperiencedTeachersBySkillId(
            @Param("skillId") Long skillId
    );

    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.skill.id = :skillId
              AND us.active = true
              AND us.yearsOfExperience >= :minimumExperience
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.TEACH
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.yearsOfExperience DESC,
                     us.averageRating DESC
            """)
    List<UserSkill> findExperiencedTeachers(
            @Param("skillId") Long skillId,
            @Param("minimumExperience") double minimumExperience
    );


    // ============================================================
    // HIGHEST RATED TEACHERS
    // ============================================================

    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.skill.id = :skillId
              AND us.active = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.TEACH
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.averageRating DESC,
                     us.ratingCount DESC,
                     us.proficiencyLevel DESC
            """)
    List<UserSkill> findHighestRatedTeachers(
            @Param("skillId") Long skillId
    );

    /**
     * Required by UserSkillService / SkillDiscoveryService.
     */
    @Query("""
            SELECT us
            FROM UserSkill us
            WHERE us.skill.id = :skillId
              AND us.active = true
              AND (
                    us.skillType = com.skillswap.skill.entity.SkillType.TEACH
                    OR
                    us.skillType = com.skillswap.skill.entity.SkillType.BOTH
                  )
            ORDER BY us.averageRating DESC,
                     us.ratingCount DESC,
                     us.proficiencyLevel DESC
            """)
    List<UserSkill> findHighestRatedTeachersBySkillId(
            @Param("skillId") Long skillId
    );


    // ============================================================
    // MULTIPLE SKILLS
    // ============================================================

    List<UserSkill> findAllByUserAndSkillInAndActiveTrue(
            User user,
            List<Skill> skills
    );


    // ============================================================
    // DELETE
    // ============================================================

    void deleteByUserAndSkill(
            User user,
            Skill skill
    );

    void deleteAllByUser(
            User user
    );
}