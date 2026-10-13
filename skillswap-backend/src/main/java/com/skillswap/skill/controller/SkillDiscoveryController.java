package com.skillswap.skill.controller;

import com.skillswap.skill.dto.UserSkillSummaryResponse;
import com.skillswap.skill.entity.ProficiencyLevel;
import com.skillswap.skill.entity.SkillCategory;
import com.skillswap.skill.service.SkillCategoryService;
import com.skillswap.skill.service.SkillService;
import com.skillswap.skill.service.UserSkillService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skill-discovery")
public class SkillDiscoveryController {

    private final SkillService skillService;
    private final SkillCategoryService skillCategoryService;
    private final UserSkillService userSkillService;

    public SkillDiscoveryController(
            SkillService skillService,
            SkillCategoryService skillCategoryService,
            UserSkillService userSkillService
    ) {
        this.skillService = skillService;
        this.skillCategoryService = skillCategoryService;
        this.userSkillService = userSkillService;
    }

    // =========================================================
    // SEARCH SKILLS
    // =========================================================

    @GetMapping("/skills/search")
    public ResponseEntity<?> searchSkills(
            @RequestParam(
                    name = "q",
                    required = false
            )
            @Size(
                    max = 150,
                    message = "Search query must not exceed 150 characters"
            )
            String query
    ) {

        return ResponseEntity.ok(
                skillService.searchSkills(query)
        );
    }

    // =========================================================
    // GET ALL CATEGORIES
    // =========================================================

    @GetMapping("/categories")
    public ResponseEntity<List<SkillCategory>>
    getCategories() {

        return ResponseEntity.ok(
                skillCategoryService.getAllCategories()
        );
    }

    // =========================================================
    // GET CATEGORY NAMES
    // =========================================================

    @GetMapping("/categories/names")
    public ResponseEntity<List<String>>
    getCategoryNames() {

        return ResponseEntity.ok(
                skillCategoryService.getCategoryNames()
        );
    }

    // =========================================================
    // GET SKILLS BY CATEGORY
    // =========================================================

    @GetMapping("/skills/category/{category}")
    public ResponseEntity<?> getSkillsByCategory(
            @PathVariable SkillCategory category
    ) {

        return ResponseEntity.ok(
                skillService.getSkillsByCategory(
                        category
                )
        );
    }

    // =========================================================
    // GET TEACHERS FOR A SKILL
    // =========================================================

    @GetMapping("/skills/{skillId}/teachers")
    public ResponseEntity<List<UserSkillSummaryResponse>>
    getTeachersForSkill(
            @PathVariable Long skillId
    ) {

        return ResponseEntity.ok(
                userSkillService.getTeachersForSkill(
                        skillId
                )
        );
    }

    // =========================================================
    // GET LEARNERS FOR A SKILL
    // =========================================================

    @GetMapping("/skills/{skillId}/learners")
    public ResponseEntity<List<UserSkillSummaryResponse>>
    getLearnersForSkill(
            @PathVariable Long skillId
    ) {

        return ResponseEntity.ok(
                userSkillService.getLearnersForSkill(
                        skillId
                )
        );
    }

    // =========================================================
    // GET VERIFIED TEACHERS
    // =========================================================

    @GetMapping("/skills/{skillId}/verified-teachers")
    public ResponseEntity<List<UserSkillSummaryResponse>>
    getVerifiedTeachers(
            @PathVariable Long skillId
    ) {

        return ResponseEntity.ok(
                userSkillService
                        .getVerifiedTeachersForSkill(
                                skillId
                        )
        );
    }

    // =========================================================
    // GET EXPERIENCED TEACHERS
    // =========================================================

    @GetMapping("/skills/{skillId}/experienced-teachers")
    public ResponseEntity<List<UserSkillSummaryResponse>>
    getExperiencedTeachers(
            @PathVariable Long skillId
    ) {

        return ResponseEntity.ok(
                userSkillService
                        .getExperiencedTeachersForSkill(
                                skillId
                        )
        );
    }

    // =========================================================
    // GET HIGHEST-RATED TEACHERS
    // =========================================================

    @GetMapping("/skills/{skillId}/highest-rated-teachers")
    public ResponseEntity<List<UserSkillSummaryResponse>>
    getHighestRatedTeachers(
            @PathVariable Long skillId
    ) {

        return ResponseEntity.ok(
                userSkillService
                        .getHighestRatedTeachersForSkill(
                                skillId
                        )
        );
    }

    // =========================================================
    // GET DISCOVERY DATA FOR A SKILL
    // =========================================================
    //
    // Returns the primary discovery groups together so the
    // frontend can build a skill-discovery page without making
    // several unrelated API calls.
    //
    // =========================================================

    @GetMapping("/skills/{skillId}/overview")
    public ResponseEntity<SkillDiscoveryOverview>
    getSkillOverview(
            @PathVariable Long skillId
    ) {

        return ResponseEntity.ok(
                new SkillDiscoveryOverview(
                        skillService.getSkillById(skillId),
                        userSkillService.getTeachersForSkill(
                                skillId
                        ),
                        userSkillService.getLearnersForSkill(
                                skillId
                        ),
                        userSkillService
                                .getVerifiedTeachersForSkill(
                                        skillId
                                ),
                        userSkillService
                                .getExperiencedTeachersForSkill(
                                        skillId
                                ),
                        userSkillService
                                .getHighestRatedTeachersForSkill(
                                        skillId
                                )
                )
        );
    }

    // =========================================================
    // DISCOVERY FILTER INFORMATION
    // =========================================================

    @GetMapping("/filters")
    public ResponseEntity<DiscoveryFilterResponse>
    getDiscoveryFilters() {

        return ResponseEntity.ok(
                new DiscoveryFilterResponse(
                        skillCategoryService.getAllCategories(),
                        List.of(
                                ProficiencyLevel.values()
                        )
                )
        );
    }

    // =========================================================
    // SIMPLE DISCOVERY QUERY
    // =========================================================
    //
    // skillType:
    // TEACH / LEARN / BOTH
    //
    // proficiency:
    // BEGINNER / INTERMEDIATE / ADVANCED / EXPERT
    //
    // The repository/service layer already contains the
    // individual query capabilities. This endpoint is kept
    // intentionally simple until the full matching engine is
    // introduced in the next phase.
    // =========================================================

    @GetMapping("/skills/{skillId}/teachers/filter")
    public ResponseEntity<List<UserSkillSummaryResponse>>
    filterTeachers(
            @PathVariable Long skillId,

            @RequestParam(
                    name = "verified",
                    defaultValue = "false"
            )
            boolean verified,

            @RequestParam(
                    name = "experienced",
                    defaultValue = "false"
            )
            boolean experienced,

            @RequestParam(
                    name = "minExperience",
                    defaultValue = "0"
            )
            @Min(
                    value = 0,
                    message = "Minimum experience cannot be negative"
            )
            @Max(
                    value = 80,
                    message = "Minimum experience cannot exceed 80 years"
            )
            double minExperience
    ) {

        List<UserSkillSummaryResponse> results;

        if (verified) {

            results =
                    userSkillService
                            .getVerifiedTeachersForSkill(
                                    skillId
                            );

        } else if (experienced) {

            results =
                    userSkillService
                            .getExperiencedTeachersForSkill(
                                    skillId
                            );

        } else {

            results =
                    userSkillService
                            .getTeachersForSkill(
                                    skillId
                            );
        }

        if (minExperience <= 0) {
            return ResponseEntity.ok(results);
        }

        List<UserSkillSummaryResponse> filtered =
                results.stream()
                        .filter(skill ->
                                skill.yearsOfExperience()
                                        >= minExperience
                        )
                        .toList();

        return ResponseEntity.ok(filtered);
    }

    // =========================================================
    // RESPONSE RECORDS
    // =========================================================

    public record SkillDiscoveryOverview(
            Object skill,
            List<UserSkillSummaryResponse> teachers,
            List<UserSkillSummaryResponse> learners,
            List<UserSkillSummaryResponse> verifiedTeachers,
            List<UserSkillSummaryResponse> experiencedTeachers,
            List<UserSkillSummaryResponse> highestRatedTeachers
    ) {
    }

    public record DiscoveryFilterResponse(
            List<SkillCategory> categories,
            List<ProficiencyLevel> proficiencyLevels
    ) {
    }
}