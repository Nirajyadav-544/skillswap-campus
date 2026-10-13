package com.skillswap.profile.service;

import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.profile.dto.CreateProfileRequest;
import com.skillswap.profile.dto.ProfileResponse;
import com.skillswap.profile.dto.ProfileSummaryResponse;
import com.skillswap.profile.dto.UpdateProfileRequest;
import com.skillswap.profile.entity.StudentProfile;
import com.skillswap.profile.repository.StudentProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    public ProfileService(
            StudentProfileRepository studentProfileRepository,
            UserRepository userRepository
    ) {
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
    }

    // ============================================================
    // CREATE PROFILE
    // ============================================================

    /**
     * Creates a student profile for the authenticated user.
     *
     * A user can have only one student profile.
     */
    @Transactional
    public ProfileResponse createProfile(
            Long userId,
            CreateProfileRequest request
    ) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "Authenticated user ID is required"
            );
        }

        if (request == null) {
            throw new IllegalArgumentException(
                    "Profile request is required"
            );
        }

        User user = getUserById(userId);

        if (studentProfileRepository.existsByUserId(userId)) {
            throw new IllegalStateException(
                    "Student profile already exists"
            );
        }

        StudentProfile profile = new StudentProfile();

        profile.setUser(user);

        applyCreateRequest(profile, request);

        updateProfileCompletionStatus(profile);

        StudentProfile savedProfile =
                studentProfileRepository.save(profile);

        return toProfileResponse(savedProfile);
    }


    // ============================================================
    // GET MY PROFILE
    // ============================================================

    /**
     * Returns the complete profile of the authenticated user.
     */
    @Transactional
    public ProfileResponse getMyProfile(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "Authenticated user ID is required"
            );
        }

        StudentProfile profile =
                studentProfileRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Student profile not found"
                                )
                        );

        return toProfileResponse(profile);
    }


    // ============================================================
    // GET PROFILE BY USER ID
    // ============================================================

    /**
     * Returns another student's complete profile.
     *
     * This method will be useful later for:
     * - skill discovery
     * - matching
     * - collaboration
     * - learning requests
     * - project discovery
     */
    @Transactional
    public ProfileResponse getProfileByUserId(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required"
            );
        }

        StudentProfile profile =
                studentProfileRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Student profile not found"
                                )
                        );

        return toProfileResponse(profile);
    }


    // ============================================================
    // GET PROFILE BY PROFILE ID
    // ============================================================

    /**
     * Returns a profile using the profile's own ID.
     */
    @Transactional
    public ProfileResponse getProfileById(Long profileId) {

        if (profileId == null) {
            throw new IllegalArgumentException(
                    "Profile ID is required"
            );
        }

        StudentProfile profile =
                studentProfileRepository.findById(profileId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Student profile not found"
                                )
                        );

        return toProfileResponse(profile);
    }


    // ============================================================
    // UPDATE PROFILE
    // ============================================================

    /**
     * Updates the authenticated user's profile.
     *
     * The profile remains owned by the authenticated user.
     * The user relationship itself cannot be changed through
     * this endpoint.
     */
    @Transactional
    public ProfileResponse updateProfile(
            Long userId,
            UpdateProfileRequest request
    ) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "Authenticated user ID is required"
            );
        }

        if (request == null) {
            throw new IllegalArgumentException(
                    "Profile update request is required"
            );
        }

        StudentProfile profile =
                studentProfileRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Student profile not found"
                                )
                        );

        applyUpdateRequest(profile, request);

        updateProfileCompletionStatus(profile);

        StudentProfile savedProfile =
                studentProfileRepository.save(profile);

        return toProfileResponse(savedProfile);
    }


    // ============================================================
    // DELETE PROFILE
    // ============================================================

    /**
     * Deletes the authenticated user's student profile.
     *
     * The User account itself is NOT deleted.
     *
     * This distinction is important because identity/account
     * management belongs to the identity module.
     */
    @Transactional
    public void deleteMyProfile(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "Authenticated user ID is required"
            );
        }

        StudentProfile profile =
                studentProfileRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Student profile not found"
                                )
                        );

        studentProfileRepository.delete(profile);
    }


    // ============================================================
    // PROFILE EXISTENCE
    // ============================================================

    /**
     * Checks whether the authenticated user has a profile.
     */
    @Transactional
    public boolean hasProfile(Long userId) {

        if (userId == null) {
            return false;
        }

        return studentProfileRepository.existsByUserId(userId);
    }


    // ============================================================
    // PROFILE COMPLETION
    // ============================================================

    /**
     * Returns whether the user's profile is complete.
     */
    @Transactional
    public boolean isProfileCompleted(Long userId) {

        StudentProfile profile =
                studentProfileRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Student profile not found"
                                )
                        );

        return profile.isProfileCompleted();
    }


    // ============================================================
    // DISCOVERY - ALL COMPLETED PROFILES
    // ============================================================

    /**
     * Returns completed student profiles.
     *
     * This is intentionally returning summary objects rather than
     * full profiles. Full profiles may contain information that
     * should not be loaded for large discovery lists.
     */
    @Transactional
    public List<ProfileSummaryResponse> getCompletedProfiles() {

        return studentProfileRepository
                .findAllByProfileCompletedTrue()
                .stream()
                .map(this::toProfileSummaryResponse)
                .toList();
    }


    // ============================================================
    // DISCOVERY - COLLEGE
    // ============================================================

    /**
     * Returns completed profiles from a particular college.
     */
    @Transactional
    public List<ProfileSummaryResponse> getProfilesByCollege(
            String college
    ) {

        String normalizedCollege =
                normalizeSearchValue(college);

        if (normalizedCollege == null) {
            throw new IllegalArgumentException(
                    "College is required"
            );
        }

        return studentProfileRepository
                .findAllByCollegeIgnoreCaseAndProfileCompletedTrue(
                        normalizedCollege
                )
                .stream()
                .map(this::toProfileSummaryResponse)
                .toList();
    }


    // ============================================================
    // DISCOVERY - DEPARTMENT
    // ============================================================

    /**
     * Returns completed profiles from a particular department.
     */
    @Transactional
    public List<ProfileSummaryResponse> getProfilesByDepartment(
            String department
    ) {

        String normalizedDepartment =
                normalizeSearchValue(department);

        if (normalizedDepartment == null) {
            throw new IllegalArgumentException(
                    "Department is required"
            );
        }

        return studentProfileRepository
                .findAllByDepartmentIgnoreCaseAndProfileCompletedTrue(
                        normalizedDepartment
                )
                .stream()
                .map(this::toProfileSummaryResponse)
                .toList();
    }


    // ============================================================
    // DISCOVERY - COLLEGE + DEPARTMENT
    // ============================================================

    /**
     * Returns completed profiles matching both college
     * and department.
     */
    @Transactional
    public List<ProfileSummaryResponse> getProfilesByCollegeAndDepartment(
            String college,
            String department
    ) {

        String normalizedCollege =
                normalizeSearchValue(college);

        String normalizedDepartment =
                normalizeSearchValue(department);

        if (normalizedCollege == null) {
            throw new IllegalArgumentException(
                    "College is required"
            );
        }

        if (normalizedDepartment == null) {
            throw new IllegalArgumentException(
                    "Department is required"
            );
        }

        return studentProfileRepository
                .findAllByCollegeIgnoreCaseAndDepartmentIgnoreCase(
                        normalizedCollege,
                        normalizedDepartment
                )
                .stream()
                .filter(StudentProfile::isProfileCompleted)
                .map(this::toProfileSummaryResponse)
                .toList();
    }


    // ============================================================
    // DISCOVERY - COURSE
    // ============================================================

    /**
     * Returns profiles for a particular course.
     */
    @Transactional
    public List<ProfileSummaryResponse> getProfilesByCourse(
            String course
    ) {

        String normalizedCourse =
                normalizeSearchValue(course);

        if (normalizedCourse == null) {
            throw new IllegalArgumentException(
                    "Course is required"
            );
        }

        return studentProfileRepository
                .findAllByCourseIgnoreCase(normalizedCourse)
                .stream()
                .filter(StudentProfile::isProfileCompleted)
                .map(this::toProfileSummaryResponse)
                .toList();
    }


    // ============================================================
    // DISCOVERY - GRADUATION YEAR
    // ============================================================

    /**
     * Returns profiles graduating in a particular year.
     */
    @Transactional
    public List<ProfileSummaryResponse> getProfilesByGraduationYear(
            Integer graduationYear
    ) {

        if (graduationYear == null) {
            throw new IllegalArgumentException(
                    "Graduation year is required"
            );
        }

        return studentProfileRepository
                .findAllByGraduationYear(graduationYear)
                .stream()
                .filter(StudentProfile::isProfileCompleted)
                .map(this::toProfileSummaryResponse)
                .toList();
    }


    // ============================================================
    // REQUEST -> ENTITY
    // ============================================================

    /**
     * Applies create request values to a new profile.
     */
    private void applyCreateRequest(
            StudentProfile profile,
            CreateProfileRequest request
    ) {

        profile.setHeadline(
                normalizeNullableText(request.headline())
        );

        profile.setBio(
                normalizeNullableText(request.bio())
        );

        profile.setCollege(
                normalizeNullableText(request.college())
        );

        profile.setDepartment(
                normalizeNullableText(request.department())
        );

        profile.setCourse(
                normalizeNullableText(request.course())
        );

        profile.setYearOfStudy(
                request.yearOfStudy()
        );

        profile.setGraduationYear(
                request.graduationYear()
        );

        profile.setLocation(
                normalizeNullableText(request.location())
        );

        profile.setProfileImageUrl(
                normalizeNullableText(request.profileImageUrl())
        );

        profile.setGithubUrl(
                normalizeNullableText(request.githubUrl())
        );

        profile.setLinkedinUrl(
                normalizeNullableText(request.linkedinUrl())
        );

        profile.setPortfolioUrl(
                normalizeNullableText(request.portfolioUrl())
        );

        profile.setAvailability(
                normalizeNullableText(request.availability())
        );
    }


    /**
     * Applies update request values to an existing profile.
     *
     * This is currently a full update operation because the DTO
     * represents the complete editable profile state.
     */
    private void applyUpdateRequest(
            StudentProfile profile,
            UpdateProfileRequest request
    ) {

        profile.setHeadline(
                normalizeNullableText(request.headline())
        );

        profile.setBio(
                normalizeNullableText(request.bio())
        );

        profile.setCollege(
                normalizeNullableText(request.college())
        );

        profile.setDepartment(
                normalizeNullableText(request.department())
        );

        profile.setCourse(
                normalizeNullableText(request.course())
        );

        profile.setYearOfStudy(
                request.yearOfStudy()
        );

        profile.setGraduationYear(
                request.graduationYear()
        );

        profile.setLocation(
                normalizeNullableText(request.location())
        );

        profile.setProfileImageUrl(
                normalizeNullableText(request.profileImageUrl())
        );

        profile.setGithubUrl(
                normalizeNullableText(request.githubUrl())
        );

        profile.setLinkedinUrl(
                normalizeNullableText(request.linkedinUrl())
        );

        profile.setPortfolioUrl(
                normalizeNullableText(request.portfolioUrl())
        );

        profile.setAvailability(
                normalizeNullableText(request.availability())
        );
    }


    // ============================================================
    // PROFILE COMPLETION LOGIC
    // ============================================================

    /**
     * Determines whether the profile contains enough information
     * to be considered completed.
     *
     * Identity fields such as email/name are intentionally excluded
     * because they already belong to User.
     *
     * Current completion requirements:
     *
     * - Headline
     * - Bio
     * - College
     * - Department
     * - Course
     * - Year of study
     * - Graduation year
     * - Location
     *
     * Optional:
     * - Profile image
     * - GitHub
     * - LinkedIn
     * - Portfolio
     * - Availability
     */
    private void updateProfileCompletionStatus(
            StudentProfile profile
    ) {

        boolean completed =
                hasText(profile.getHeadline())
                        && hasText(profile.getBio())
                        && hasText(profile.getCollege())
                        && hasText(profile.getDepartment())
                        && hasText(profile.getCourse())
                        && profile.getYearOfStudy() != null
                        && profile.getGraduationYear() != null
                        && hasText(profile.getLocation());

        profile.setProfileCompleted(completed);
    }


    // ============================================================
    // ENTITY -> FULL RESPONSE
    // ============================================================

    /**
     * Converts StudentProfile into a complete API response.
     */
    private ProfileResponse toProfileResponse(
            StudentProfile profile
    ) {

        User user = profile.getUser();

        if (user == null) {
            throw new IllegalStateException(
                    "Profile is not associated with a user"
            );
        }

        return new ProfileResponse(

                profile.getId(),

                user.getId(),

                user.getEmail(),

                user.getFirstName(),

                user.getLastName(),

                user.getRole() != null
                        ? user.getRole().name()
                        : null,

                user.isEmailVerified(),

                profile.getHeadline(),

                profile.getBio(),

                profile.getCollege(),

                profile.getDepartment(),

                profile.getCourse(),

                profile.getYearOfStudy(),

                profile.getGraduationYear(),

                profile.getLocation(),

                profile.getProfileImageUrl(),

                profile.getGithubUrl(),

                profile.getLinkedinUrl(),

                profile.getPortfolioUrl(),

                profile.getAvailability(),

                profile.isProfileCompleted(),

                profile.getCreatedAt(),

                profile.getUpdatedAt()
        );
    }


    // ============================================================
    // ENTITY -> SUMMARY RESPONSE
    // ============================================================

    /**
     * Converts StudentProfile into a lightweight summary response.
     *
     * This response is intended for lists/search/discovery.
     */
    private ProfileSummaryResponse toProfileSummaryResponse(
            StudentProfile profile
    ) {

        User user = profile.getUser();

        if (user == null) {
            throw new IllegalStateException(
                    "Profile is not associated with a user"
            );
        }

        return new ProfileSummaryResponse(

                profile.getId(),

                user.getId(),

                user.getFirstName(),

                user.getLastName(),

                profile.getHeadline(),

                profile.getCollege(),

                profile.getDepartment(),

                profile.getCourse(),

                profile.getYearOfStudy(),

                profile.getGraduationYear(),

                profile.getLocation(),

                profile.getProfileImageUrl(),

                profile.isProfileCompleted()
        );
    }


    // ============================================================
    // USER LOOKUP
    // ============================================================

    /**
     * Loads a User by ID.
     */
    private User getUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User not found"
                        )
                );
    }


    // ============================================================
    // TEXT NORMALIZATION
    // ============================================================

    /**
     * Converts blank strings into null and trims surrounding
     * whitespace.
     */
    private String normalizeNullableText(String value) {

        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }


    /**
     * Normalizes a search parameter.
     */
    private String normalizeSearchValue(String value) {

        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }


    /**
     * Checks whether a String contains actual text.
     */
    private boolean hasText(String value) {

        return value != null
                && !value.trim().isEmpty();
    }
}