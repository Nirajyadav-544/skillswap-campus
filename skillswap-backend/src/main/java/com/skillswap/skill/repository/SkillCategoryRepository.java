package com.skillswap.skill.repository;

import com.skillswap.skill.entity.SkillCategory;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Repository-style abstraction for the SkillCategory enum.
 *
 * Skill categories are represented as an enum rather than a
 * separate database table because the initial category set is
 * controlled by the application.
 *
 * If SkillSwap later requires administrator-created categories,
 * this abstraction can be replaced with a real SkillCategory
 * entity without changing the rest of the service architecture.
 */
@Repository
public class SkillCategoryRepository {

    /**
     * Returns every available skill category.
     */
    public List<SkillCategory> findAll() {

        return Arrays.asList(
                SkillCategory.values()
        );
    }

    /**
     * Finds a category using a case-insensitive name.
     *
     * Example:
     *
     * "programming"
     * "PROGRAMMING"
     * "Programming"
     *
     * all resolve to SkillCategory.PROGRAMMING.
     */
    public Optional<SkillCategory> findByName(
            String name
    ) {

        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        String normalized =
                name.trim()
                        .toUpperCase();

        try {

            return Optional.of(
                    SkillCategory.valueOf(
                            normalized
                    )
            );

        } catch (IllegalArgumentException exception) {

            return Optional.empty();
        }
    }

    /**
     * Checks whether a category exists.
     */
    public boolean existsByName(
            String name
    ) {

        return findByName(name).isPresent();
    }

    /**
     * Returns whether the supplied enum value is valid.
     *
     * This method is useful for service-level validation.
     */
    public boolean exists(
            SkillCategory category
    ) {

        return category != null;
    }

    /**
     * Returns the number of available categories.
     */
    public int count() {

        return SkillCategory.values().length;
    }

    /**
     * Returns the enum names as strings.
     */
    public List<String> findAllNames() {

        return Arrays.stream(
                        SkillCategory.values()
                )
                .map(Enum::name)
                .toList();
    }

    /**
     * Returns a category or throws an exception when it
     * cannot be resolved.
     *
     * The service layer can use this when a category is required.
     */
    public SkillCategory findRequiredByName(
            String name
    ) {

        return findByName(name)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Invalid skill category: " + name
                        )
                );
    }
}