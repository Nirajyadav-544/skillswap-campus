package com.skillswap.profile.dto;

public record ProfileSummaryResponse(

        /**
         * Student profile ID.
         */
        Long id,

        /**
         * User ID associated with this profile.
         */
        Long userId,

        /**
         * Basic identity information.
         */
        String firstName,

        String lastName,

        /**
         * Short professional/student headline.
         */
        String headline,

        /**
         * Academic information.
         */
        String college,

        String department,

        String course,

        Integer yearOfStudy,

        Integer graduationYear,

        /**
         * Current location.
         */
        String location,

        /**
         * Profile picture.
         */
        String profileImageUrl,

        /**
         * Indicates whether the profile has
         * completed the required profile information.
         */
        boolean profileCompleted
) {
}