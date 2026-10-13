package com.skillswap.profile.dto;

import java.time.LocalDateTime;

public record ProfileResponse(

        /*
         * ============================================================
         * PROFILE IDENTIFICATION
         * ============================================================
         */

        Long id,

        Long userId,


        /*
         * ============================================================
         * USER INFORMATION
         * ============================================================
         *
         * These values come from the Identity User entity.
         * Passwords, tokens and security information are NEVER
         * included in this response.
         */

        String email,

        String firstName,

        String lastName,

        String role,

        boolean emailVerified,


        /*
         * ============================================================
         * PROFILE INFORMATION
         * ============================================================
         */

        String headline,

        String bio,


        /*
         * ============================================================
         * EDUCATION
         * ============================================================
         */

        String college,

        String department,

        String course,

        Integer yearOfStudy,

        Integer graduationYear,


        /*
         * ============================================================
         * LOCATION
         * ============================================================
         */

        String location,


        /*
         * ============================================================
         * PROFILE IMAGE
         * ============================================================
         */

        String profileImageUrl,


        /*
         * ============================================================
         * PROFESSIONAL LINKS
         * ============================================================
         */

        String githubUrl,

        String linkedinUrl,

        String portfolioUrl,


        /*
         * ============================================================
         * AVAILABILITY
         * ============================================================
         */

        String availability,


        /*
         * ============================================================
         * PROFILE STATUS
         * ============================================================
         */

        boolean profileCompleted,


        /*
         * ============================================================
         * AUDIT INFORMATION
         * ============================================================
         */

        LocalDateTime createdAt,

        LocalDateTime updatedAt
        
) {
}