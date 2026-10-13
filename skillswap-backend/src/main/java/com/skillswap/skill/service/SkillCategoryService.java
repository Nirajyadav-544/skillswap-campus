package com.skillswap.skill.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.skill.entity.SkillCategory;
import com.skillswap.skill.repository.SkillCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class SkillCategoryService {

    private final SkillCategoryRepository skillCategoryRepository;

    public SkillCategoryService(
            SkillCategoryRepository skillCategoryRepository
    ) {
        this.skillCategoryRepository = skillCategoryRepository;
    }

    // =========================================================
    // GET ALL CATEGORIES
    // =========================================================

    public List<SkillCategory> getAllCategories() {

        return skillCategoryRepository.findAll();
    }

    // =========================================================
    // GET CATEGORY BY NAME
    // =========================================================

    public SkillCategory getCategoryByName(String name) {

        if (name == null || name.isBlank()) {
            throw new BadRequestException(
                    "Skill category name is required"
            );
        }

        String normalizedName =
                normalizeCategoryName(name);

        return skillCategoryRepository
                .findByName(normalizedName)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Invalid skill category: " + name
                        )
                );
    }

    // =========================================================
    // GET CATEGORY OR NULL
    // =========================================================

    public SkillCategory findCategoryByName(String name) {

        if (name == null || name.isBlank()) {
            return null;
        }

        return skillCategoryRepository
                .findByName(
                        normalizeCategoryName(name)
                )
                .orElse(null);
    }

    // =========================================================
    // CHECK CATEGORY
    // =========================================================

    public boolean categoryExists(String name) {

        if (name == null || name.isBlank()) {
            return false;
        }

        return skillCategoryRepository.existsByName(
                normalizeCategoryName(name)
        );
    }

    // =========================================================
    // CHECK ENUM CATEGORY
    // =========================================================

    public boolean categoryExists(
            SkillCategory category
    ) {

        return category != null &&
                skillCategoryRepository.exists(category);
    }

    // =========================================================
    // GET CATEGORY COUNT
    // =========================================================

    public long getCategoryCount() {

        return skillCategoryRepository.count();
    }

    // =========================================================
    // GET CATEGORY NAMES
    // =========================================================

    public List<String> getCategoryNames() {

        return skillCategoryRepository.findAllNames();
    }

    // =========================================================
    // VALIDATE CATEGORY
    // =========================================================

    public SkillCategory validateCategory(
            SkillCategory category
    ) {

        if (category == null) {
            throw new BadRequestException(
                    "Skill category is required"
            );
        }

        if (!skillCategoryRepository.exists(category)) {
            throw new BadRequestException(
                    "Invalid skill category: " +
                            category.name()
            );
        }

        return category;
    }

    // =========================================================
    // VALIDATE CATEGORY NAME
    // =========================================================

    public SkillCategory validateCategoryName(
            String categoryName
    ) {

        SkillCategory category =
                findCategoryByName(categoryName);

        if (category == null) {
            throw new BadRequestException(
                    "Invalid skill category: " +
                            categoryName
            );
        }

        return category;
    }

    // =========================================================
    // DEFAULT CATEGORY
    // =========================================================

    public SkillCategory getDefaultCategory() {

        return SkillCategory.OTHER;
    }

    // =========================================================
    // CHECK IF DEFAULT CATEGORY
    // =========================================================

    public boolean isDefaultCategory(
            SkillCategory category
    ) {

        return SkillCategory.OTHER.equals(category);
    }

    // =========================================================
    // GET ENUM VALUES
    // =========================================================

    public List<SkillCategory> getEnumCategories() {

        return Arrays.asList(
                SkillCategory.values()
        );
    }

    // =========================================================
    // PARSE CATEGORY SAFELY
    // =========================================================

    public SkillCategory parseCategory(
            String categoryName
    ) {

        if (categoryName == null ||
                categoryName.isBlank()) {

            return null;
        }

        String normalized =
                categoryName
                        .trim()
                        .toUpperCase(Locale.ROOT)
                        .replace('-', '_')
                        .replace(' ', '_');

        try {

            return SkillCategory.valueOf(
                    normalized
            );

        } catch (IllegalArgumentException exception) {

            throw new BadRequestException(
                    "Invalid skill category: " +
                            categoryName
            );
        }
    }

    // =========================================================
    // NORMALIZE CATEGORY NAME
    // =========================================================

    private String normalizeCategoryName(
            String categoryName
    ) {

        return categoryName
                .trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
    }
}