package com.skillswap.profile.controller;

import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.profile.dto.CreateProfileRequest;
import com.skillswap.profile.dto.ProfileResponse;
import com.skillswap.profile.dto.ProfileSummaryResponse;
import com.skillswap.profile.dto.UpdateProfileRequest;
import com.skillswap.profile.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserRepository userRepository;

    public ProfileController(
            ProfileService profileService,
            UserRepository userRepository
    ) {
        this.profileService = profileService;
        this.userRepository = userRepository;
    }


    // ============================================================
    // CREATE PROFILE
    // ============================================================

    /**
     * POST /api/v1/profile
     *
     * Creates a profile for the authenticated user.
     */
    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(
            Authentication authentication,
            @Valid @RequestBody CreateProfileRequest request
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        ProfileResponse response =
                profileService.createProfile(
                        userId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ============================================================
    // GET MY PROFILE
    // ============================================================

    /**
     * GET /api/v1/profile/me
     */
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(
            Authentication authentication
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        ProfileResponse response =
                profileService.getMyProfile(userId);

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // UPDATE MY PROFILE
    // ============================================================

    /**
     * PUT /api/v1/profile/me
     */
    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        ProfileResponse response =
                profileService.updateProfile(
                        userId,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // DELETE MY PROFILE
    // ============================================================

    /**
     * DELETE /api/v1/profile/me
     *
     * Deletes the profile only.
     * The User account remains.
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(
            Authentication authentication
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        profileService.deleteMyProfile(userId);

        return ResponseEntity.noContent().build();
    }


    // ============================================================
    // CHECK PROFILE EXISTS
    // ============================================================

    /**
     * GET /api/v1/profile/me/exists
     */
    @GetMapping("/me/exists")
    public ResponseEntity<ProfileExistsResponse> profileExists(
            Authentication authentication
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        boolean exists =
                profileService.hasProfile(userId);

        return ResponseEntity.ok(
                new ProfileExistsResponse(exists)
        );
    }


    // ============================================================
    // CHECK PROFILE COMPLETION
    // ============================================================

    /**
     * GET /api/v1/profile/me/completion
     */
    @GetMapping("/me/completion")
    public ResponseEntity<ProfileCompletionResponse>
    profileCompletion(
            Authentication authentication
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        boolean completed =
                profileService.isProfileCompleted(userId);

        return ResponseEntity.ok(
                new ProfileCompletionResponse(completed)
        );
    }


    // ============================================================
    // GET PROFILE BY USER ID
    // ============================================================

    /**
     * GET /api/v1/profile/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ProfileResponse> getProfileByUserId(
            @PathVariable Long userId
    ) {

        ProfileResponse response =
                profileService.getProfileByUserId(userId);

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // GET PROFILE BY PROFILE ID
    // ============================================================

    /**
     * GET /api/v1/profile/{profileId}
     */
    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileResponse> getProfileById(
            @PathVariable Long profileId
    ) {

        ProfileResponse response =
                profileService.getProfileById(profileId);

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // DISCOVERY
    // ============================================================

    /**
     * GET /api/v1/profile/discover
     *
     * Returns completed profiles.
     */
    @GetMapping("/discover")
    public ResponseEntity<List<ProfileSummaryResponse>>
    getCompletedProfiles() {

        List<ProfileSummaryResponse> profiles =
                profileService.getCompletedProfiles();

        return ResponseEntity.ok(profiles);
    }


    // ============================================================
    // DISCOVERY BY COLLEGE
    // ============================================================

    /**
     * GET /api/v1/profile/discover/college?college=ABC
     */
    @GetMapping("/discover/college")
    public ResponseEntity<List<ProfileSummaryResponse>>
    getProfilesByCollege(
            @RequestParam String college
    ) {

        List<ProfileSummaryResponse> profiles =
                profileService.getProfilesByCollege(
                        college
                );

        return ResponseEntity.ok(profiles);
    }


    // ============================================================
    // DISCOVERY BY DEPARTMENT
    // ============================================================

    /**
     * GET /api/v1/profile/discover/department?department=CSE
     */
    @GetMapping("/discover/department")
    public ResponseEntity<List<ProfileSummaryResponse>>
    getProfilesByDepartment(
            @RequestParam String department
    ) {

        List<ProfileSummaryResponse> profiles =
                profileService.getProfilesByDepartment(
                        department
                );

        return ResponseEntity.ok(profiles);
    }


    // ============================================================
    // DISCOVERY BY COLLEGE + DEPARTMENT
    // ============================================================

    /**
     * GET /api/v1/profile/discover/filter
     *
     * Example:
     *
     * /api/v1/profile/discover/filter
     * ?college=ABC College
     * &department=Computer Science
     */
    @GetMapping("/discover/filter")
    public ResponseEntity<List<ProfileSummaryResponse>>
    getProfilesByCollegeAndDepartment(
            @RequestParam String college,
            @RequestParam String department
    ) {

        List<ProfileSummaryResponse> profiles =
                profileService.getProfilesByCollegeAndDepartment(
                        college,
                        department
                );

        return ResponseEntity.ok(profiles);
    }


    // ============================================================
    // DISCOVERY BY COURSE
    // ============================================================

    /**
     * GET /api/v1/profile/discover/course?course=B.Tech
     */
    @GetMapping("/discover/course")
    public ResponseEntity<List<ProfileSummaryResponse>>
    getProfilesByCourse(
            @RequestParam String course
    ) {

        List<ProfileSummaryResponse> profiles =
                profileService.getProfilesByCourse(
                        course
                );

        return ResponseEntity.ok(profiles);
    }


    // ============================================================
    // DISCOVERY BY GRADUATION YEAR
    // ============================================================

    /**
     * GET /api/v1/profile/discover/graduation-year?year=2027
     */
    @GetMapping("/discover/graduation-year")
    public ResponseEntity<List<ProfileSummaryResponse>>
    getProfilesByGraduationYear(
            @RequestParam Integer year
    ) {

        List<ProfileSummaryResponse> profiles =
                profileService.getProfilesByGraduationYear(
                        year
                );

        return ResponseEntity.ok(profiles);
    }


    // ============================================================
    // AUTHENTICATED USER ID RESOLUTION
    // ============================================================

    /**
     * Resolves the authenticated user's database ID.
     *
     * Current security architecture:
     *
     * JWT
     *   ↓
     * email
     *   ↓
     * UserDetails
     *   ↓
     * UserRepository
     *   ↓
     * userId
     *
     * This keeps the profile module compatible with the
     * existing UserDetailsServiceImpl.
     */
    private Long getAuthenticatedUserId(
            Authentication authentication
    ) {

        if (authentication == null) {
            throw new IllegalStateException(
                    "Authentication is required"
            );
        }

        if (!authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "User is not authenticated"
            );
        }

        Object principal =
                authentication.getPrincipal();

        String email = null;

        if (principal instanceof UserDetails userDetails) {

            email = userDetails.getUsername();

        } else if (principal instanceof String principalString) {

            email = principalString;
        }

        if (email == null || email.isBlank()) {
            throw new IllegalStateException(
                    "Authenticated user email could not be determined"
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        User user =
                userRepository.findByEmail(normalizedEmail)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Authenticated user was not found"
                                )
                        );

        return user.getId();
    }


    // ============================================================
    // RESPONSE TYPES
    // ============================================================

    /**
     * Response used by:
     *
     * GET /me/exists
     */
    public record ProfileExistsResponse(
            boolean exists
    ) {
    }


    /**
     * Response used by:
     *
     * GET /me/completion
     */
    public record ProfileCompletionResponse(
            boolean completed
    ) {
    }
}