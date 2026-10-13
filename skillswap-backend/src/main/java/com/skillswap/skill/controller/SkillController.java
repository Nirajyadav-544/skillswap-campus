package com.skillswap.skill.controller;

import com.skillswap.skill.dto.CreateSkillRequest;
import com.skillswap.skill.dto.SkillResponse;
import com.skillswap.skill.dto.SkillSummaryResponse;
import com.skillswap.skill.dto.UpdateSkillRequest;
import com.skillswap.skill.entity.SkillCategory;
import com.skillswap.skill.service.SkillService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@Validated
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    // =========================================================
    // CREATE SKILL
    // =========================================================

    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(
            @Valid @RequestBody CreateSkillRequest request
    ) {

        SkillResponse response =
                skillService.createSkill(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // CREATE SYSTEM SKILL
    // =========================================================
    //
    // This endpoint is intentionally separate from normal
    // skill creation. Authorization for administrative/system
    // operations can be tightened in SecurityConfig later
    // without changing the service architecture.
    // =========================================================

    @PostMapping("/system")
    public ResponseEntity<SkillResponse> createSystemSkill(
            @Valid @RequestBody CreateSkillRequest request
    ) {

        SkillResponse response =
                skillService.createSystemSkill(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // GET ALL SKILLS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills() {

        return ResponseEntity.ok(
                skillService.getAllSkills()
        );
    }

    // =========================================================
    // GET ACTIVE SKILLS
    // =========================================================

    @GetMapping("/active")
    public ResponseEntity<List<SkillResponse>> getActiveSkills() {

        return ResponseEntity.ok(
                skillService.getActiveSkills()
        );
    }

    // =========================================================
    // GET POPULAR SKILLS
    // =========================================================

    @GetMapping("/popular")
    public ResponseEntity<List<SkillSummaryResponse>>
    getPopularSkills() {

        return ResponseEntity.ok(
                skillService.getPopularSkills()
        );
    }

    // =========================================================
    // GET MOST USED SKILLS
    // =========================================================

    @GetMapping("/most-used")
    public ResponseEntity<List<SkillSummaryResponse>>
    getMostUsedSkills() {

        return ResponseEntity.ok(
                skillService.getMostUsedSkills()
        );
    }

    // =========================================================
    // GET SYSTEM-DEFINED SKILLS
    // =========================================================

    @GetMapping("/system")
    public ResponseEntity<List<SkillResponse>>
    getSystemDefinedSkills() {

        return ResponseEntity.ok(
                skillService.getSystemDefinedSkills()
        );
    }

    // =========================================================
    // GET CUSTOM SKILLS
    // =========================================================

    @GetMapping("/custom")
    public ResponseEntity<List<SkillResponse>>
    getCustomSkills() {

        return ResponseEntity.ok(
                skillService.getCustomSkills()
        );
    }

    // =========================================================
    // SEARCH SKILLS
    // =========================================================

    @GetMapping("/search")
    public ResponseEntity<List<SkillSummaryResponse>>
    searchSkills(

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
    // PAGINATED SEARCH
    // =========================================================

    @GetMapping("/search/page")
    public ResponseEntity<Page<SkillSummaryResponse>>
    searchSkillsPaginated(

            @RequestParam(
                    name = "q",
                    required = false
            )
            @Size(
                    max = 150,
                    message = "Search query must not exceed 150 characters"
            )
            String query,

            @RequestParam(
                    name = "page",
                    defaultValue = "0"
            )
            @Min(
                    value = 0,
                    message = "Page cannot be negative"
            )
            int page,

            @RequestParam(
                    name = "size",
                    defaultValue = "20"
            )
            @Min(
                    value = 1,
                    message = "Page size must be at least 1"
            )
            @Max(
                    value = 100,
                    message = "Page size cannot exceed 100"
            )
            int size
    ) {

        return ResponseEntity.ok(
                skillService.searchSkills(
                        query,
                        page,
                        size
                )
        );
    }

    // =========================================================
    // GET BY CATEGORY
    // =========================================================

    @GetMapping("/category/{category}")
    public ResponseEntity<List<SkillResponse>>
    getSkillsByCategory(
            @PathVariable SkillCategory category
    ) {

        return ResponseEntity.ok(
                skillService.getSkillsByCategory(
                        category
                )
        );
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{skillId}")
    public ResponseEntity<SkillResponse> getSkillById(
            @PathVariable Long skillId
    ) {

        return ResponseEntity.ok(
                skillService.getSkillById(skillId)
        );
    }

    // =========================================================
    // GET BY NAME
    // =========================================================

    @GetMapping("/name/{name}")
    public ResponseEntity<SkillResponse> getSkillByName(
            @PathVariable String name
    ) {

        return ResponseEntity.ok(
                skillService.getSkillByName(name)
        );
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @PutMapping("/{skillId}")
    public ResponseEntity<SkillResponse> updateSkill(
            @PathVariable Long skillId,
            @Valid @RequestBody UpdateSkillRequest request
    ) {

        return ResponseEntity.ok(
                skillService.updateSkill(
                        skillId,
                        request
                )
        );
    }

    // =========================================================
    // ACTIVATE
    // =========================================================

    @PatchMapping("/{skillId}/activate")
    public ResponseEntity<SkillResponse> activateSkill(
            @PathVariable Long skillId
    ) {

        return ResponseEntity.ok(
                skillService.activateSkill(skillId)
        );
    }

    // =========================================================
    // DEACTIVATE
    // =========================================================

    @PatchMapping("/{skillId}/deactivate")
    public ResponseEntity<SkillResponse> deactivateSkill(
            @PathVariable Long skillId
    ) {

        return ResponseEntity.ok(
                skillService.deactivateSkill(skillId)
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteSkill(
            @PathVariable Long skillId
    ) {

        skillService.deleteSkill(skillId);

        return ResponseEntity.noContent().build();
    }
}