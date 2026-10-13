package com.skillswap.skill.controller;

import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.skill.dto.AddUserSkillRequest;
import com.skillswap.skill.dto.UpdateUserSkillRequest;
import com.skillswap.skill.dto.UserSkillResponse;
import com.skillswap.skill.dto.UserSkillSummaryResponse;
import com.skillswap.skill.service.UserSkillService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/skills")
public class UserSkillController {

    private final UserSkillService userSkillService;
    private final UserRepository userRepository;

    public UserSkillController(
            UserSkillService userSkillService,
            UserRepository userRepository
    ) {
        this.userSkillService = userSkillService;
        this.userRepository = userRepository;
    }

    // =========================================================
    // ADD MY SKILL
    // =========================================================

    @PostMapping
    public ResponseEntity<UserSkillResponse> addSkill(
            Authentication authentication,
            @Valid @RequestBody AddUserSkillRequest request
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        UserSkillResponse response =
                userSkillService.addUserSkill(
                        userId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // GET ALL MY SKILLS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<UserSkillResponse>>
    getMySkills(
            Authentication authentication
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                userSkillService.getUserSkills(userId)
        );
    }

    // =========================================================
    // GET PAGINATED MY SKILLS
    // =========================================================

    @GetMapping("/page")
    public ResponseEntity<Page<UserSkillSummaryResponse>>
    getMySkillsPaginated(

            Authentication authentication,

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

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                userSkillService.getUserSkills(
                        userId,
                        page,
                        size
                )
        );
    }

    // =========================================================
    // GET ACTIVE MY SKILLS
    // =========================================================

    @GetMapping("/active")
    public ResponseEntity<List<UserSkillSummaryResponse>>
    getMyActiveSkills(
            Authentication authentication
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                userSkillService.getActiveUserSkills(
                        userId
                )
        );
    }

    // =========================================================
    // GET MY TEACHING SKILLS
    // =========================================================

    @GetMapping("/teaching")
    public ResponseEntity<List<UserSkillSummaryResponse>>
    getMyTeachingSkills(
            Authentication authentication
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                userSkillService.getTeachingSkills(
                        userId
                )
        );
    }

    // =========================================================
    // GET MY LEARNING SKILLS
    // =========================================================

    @GetMapping("/learning")
    public ResponseEntity<List<UserSkillSummaryResponse>>
    getMyLearningSkills(
            Authentication authentication
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                userSkillService.getLearningSkills(
                        userId
                )
        );
    }

    // =========================================================
    // GET MY SKILL BY USER-SKILL ID
    // =========================================================

    @GetMapping("/{userSkillId}")
    public ResponseEntity<UserSkillResponse>
    getMySkill(
            Authentication authentication,
            @PathVariable Long userSkillId
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                userSkillService.getUserSkill(
                        userId,
                        userSkillId
                )
        );
    }

    // =========================================================
    // GET MY SKILL BY SKILL ID
    // =========================================================

    @GetMapping("/skill/{skillId}")
    public ResponseEntity<UserSkillResponse>
    getMySkillBySkillId(
            Authentication authentication,
            @PathVariable Long skillId
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                userSkillService.getUserSkillBySkillId(
                        userId,
                        skillId
                )
        );
    }

    // =========================================================
    // UPDATE MY SKILL
    // =========================================================

    @PutMapping("/{userSkillId}")
    public ResponseEntity<UserSkillResponse>
    updateMySkill(
            Authentication authentication,
            @PathVariable Long userSkillId,
            @Valid @RequestBody UpdateUserSkillRequest request
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                userSkillService.updateUserSkill(
                        userId,
                        userSkillId,
                        request
                )
        );
    }

    // =========================================================
    // ACTIVATE MY SKILL
    // =========================================================

    @PatchMapping("/{userSkillId}/activate")
    public ResponseEntity<UserSkillResponse>
    activateMySkill(
            Authentication authentication,
            @PathVariable Long userSkillId
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                userSkillService.activateUserSkill(
                        userId,
                        userSkillId
                )
        );
    }

    // =========================================================
    // DEACTIVATE MY SKILL
    // =========================================================

    @PatchMapping("/{userSkillId}/deactivate")
    public ResponseEntity<UserSkillResponse>
    deactivateMySkill(
            Authentication authentication,
            @PathVariable Long userSkillId
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                userSkillService.deactivateUserSkill(
                        userId,
                        userSkillId
                )
        );
    }

    // =========================================================
    // DELETE MY SKILL
    // =========================================================

    @DeleteMapping("/{userSkillId}")
    public ResponseEntity<Void> deleteMySkill(
            Authentication authentication,
            @PathVariable Long userSkillId
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        userSkillService.deleteUserSkill(
                userId,
                userSkillId
        );

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // DELETE MY SKILL BY SKILL ID
    // =========================================================

    @DeleteMapping("/skill/{skillId}")
    public ResponseEntity<Void> deleteMySkillBySkillId(
            Authentication authentication,
            @PathVariable Long skillId
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        userSkillService.deleteUserSkillBySkillId(
                userId,
                skillId
        );

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // AUTHENTICATED USER RESOLUTION
    // =========================================================

    private Long getAuthenticatedUserId(
            Authentication authentication
    ) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Authenticated user is required"
            );
        }

        String email =
                authentication.getName();

        if (email == null || email.isBlank()) {
            throw new IllegalStateException(
                    "Authenticated user email is missing"
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        User user =
                userRepository
                        .findByEmail(normalizedEmail)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Authenticated user was not found"
                                )
                        );

        return user.getId();
    }
}