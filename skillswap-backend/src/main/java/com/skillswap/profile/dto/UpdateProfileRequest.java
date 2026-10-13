package com.skillswap.profile.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        /*
         * ============================================================
         * BASIC PROFILE INFORMATION
         * ============================================================
         */

        @Size(
                max = 200,
                message = "Headline must not exceed 200 characters"
        )
        String headline,


        @Size(
                max = 2000,
                message = "Bio must not exceed 2000 characters"
        )
        String bio,


        /*
         * ============================================================
         * EDUCATION
         * ============================================================
         */

        @Size(
                max = 200,
                message = "College must not exceed 200 characters"
        )
        String college,


        @Size(
                max = 150,
                message = "Department must not exceed 150 characters"
        )
        String department,


        @Size(
                max = 150,
                message = "Course must not exceed 150 characters"
        )
        String course,


        @Min(
                value = 1,
                message = "Year of study must be at least 1"
        )
        @Max(
                value = 10,
                message = "Year of study must not exceed 10"
        )
        Integer yearOfStudy,


        @Min(
                value = 2000,
                message = "Graduation year is invalid"
        )
        @Max(
                value = 2100,
                message = "Graduation year is invalid"
        )
        Integer graduationYear,


        /*
         * ============================================================
         * LOCATION
         * ============================================================
         */

        @Size(
                max = 200,
                message = "Location must not exceed 200 characters"
        )
        String location,


        /*
         * ============================================================
         * PROFILE IMAGE
         * ============================================================
         */

        @Size(
                max = 1000,
                message = "Profile image URL is too long"
        )
        @Pattern(
                regexp = "^$|^(https?://|/).*$",
                message = "Profile image URL must be a valid HTTP/HTTPS URL or application path"
        )
        String profileImageUrl,


        /*
         * ============================================================
         * PROFESSIONAL LINKS
         * ============================================================
         */

        @Size(
                max = 500,
                message = "GitHub URL is too long"
        )
        @Pattern(
                regexp = "^$|^https?://.*$",
                message = "GitHub URL must start with http:// or https://"
        )
        String githubUrl,


        @Size(
                max = 500,
                message = "LinkedIn URL is too long"
        )
        @Pattern(
                regexp = "^$|^https?://.*$",
                message = "LinkedIn URL must start with http:// or https://"
        )
        String linkedinUrl,


        @Size(
                max = 500,
                message = "Portfolio URL is too long"
        )
        @Pattern(
                regexp = "^$|^https?://.*$",
                message = "Portfolio URL must start with http:// or https://"
        )
        String portfolioUrl,


        /*
         * ============================================================
         * AVAILABILITY
         * ============================================================
         */

        @Size(
                max = 500,
                message = "Availability must not exceed 500 characters"
        )
        String availability
) {
}