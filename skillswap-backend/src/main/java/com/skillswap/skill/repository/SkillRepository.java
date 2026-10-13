package com.skillswap.skill.repository;

import com.skillswap.skill.entity.Skill;
import com.skillswap.skill.entity.SkillCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Skill catalog operations.
 *
 * The repository intentionally contains both simple Spring Data
 * queries and optimized search queries so that the service layer
 * does not need to access the database directly.
 */
public interface SkillRepository
        extends JpaRepository<Skill, Long> {

    Optional<Skill> findByNormalizedName(
            String normalizedName
    );

    boolean existsByNormalizedName(
            String normalizedName
    );

    Optional<Skill> findByNameIgnoreCase(
            String name
    );

    boolean existsByNameIgnoreCase(
            String name
    );

    List<Skill> findAllByActiveTrue();

    List<Skill> findAllByActiveTrueOrderByPopularityScoreDesc();

    List<Skill> findAllByActiveTrueOrderByUserCountDesc();

    List<Skill> findAllByCategoryAndActiveTrue(
            SkillCategory category
    );

    List<Skill>
    findAllByCategoryAndActiveTrueOrderByPopularityScoreDesc(
            SkillCategory category
    );

    List<Skill>
    findAllByNameContainingIgnoreCaseAndActiveTrue(
            String name
    );

    @Query("""
            SELECT s
            FROM Skill s
            WHERE s.active = true
              AND (
                    LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%'))
                    OR
                    LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%'))
                  )
            ORDER BY s.popularityScore DESC,
                     s.userCount DESC,
                     s.name ASC
            """)
    List<Skill> searchActiveSkills(
            @Param("query") String query
    );

    @Query("""
            SELECT s
            FROM Skill s
            WHERE s.active = true
              AND (
                    LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%'))
                    OR
                    LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%'))
                  )
            """)
    Page<Skill> searchActiveSkills(
            @Param("query") String query,
            Pageable pageable
    );

    Page<Skill> findAllByCategoryAndActiveTrue(
            SkillCategory category,
            Pageable pageable
    );

    Page<Skill> findAllByActiveTrue(
            Pageable pageable
    );

    /*
     * Service-compatible method.
     */
    Page<Skill> findByActiveTrue(
            Pageable pageable
    );

    /*
     * Service-compatible alphabetical queries.
     */
    List<Skill> findAllByOrderByNameAsc();

    List<Skill> findByActiveTrueOrderByNameAsc();

    List<Skill>
    findByCategoryAndActiveTrueOrderByNameAsc(
            SkillCategory category
    );

    List<Skill>
    findTop20ByActiveTrueOrderByPopularityScoreDesc();

    List<Skill>
    findTop20ByActiveTrueOrderByUserCountDesc();

    List<Skill> findAllBySystemDefinedTrue();

    List<Skill> findAllBySystemDefinedTrueAndActiveTrue();

    List<Skill> findAllBySystemDefinedFalse();

    List<Skill> findAllBySystemDefinedFalseAndActiveTrue();

    /*
     * Service-compatible system-defined queries.
     */
    List<Skill>
    findBySystemDefinedTrueOrderByNameAsc();

    List<Skill>
    findBySystemDefinedFalseOrderByNameAsc();

    long countByCategoryAndActiveTrue(
            SkillCategory category
    );

    long countByActiveTrue();

    long countBySystemDefinedTrue();

    List<Skill> findAllByActiveTrueAndUserCountGreaterThanEqual(
            long userCount
    );

    List<Skill>
    findAllByActiveTrueOrderByNameAsc();

    List<Skill>
    findAllByCategoryAndActiveTrueOrderByUserCountDescNameAsc(
            SkillCategory category
    );
}