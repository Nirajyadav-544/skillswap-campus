package com.skillswap.common.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ApiError(

        /**
         * Time at which the error occurred.
         */
        LocalDateTime timestamp,

        /**
         * HTTP status code.
         *
         * Example:
         * 400
         * 401
         * 403
         * 404
         * 409
         * 500
         */
        int status,

        /**
         * HTTP status name.
         *
         * Example:
         * BAD_REQUEST
         * UNAUTHORIZED
         * NOT_FOUND
         */
        String error,

        /**
         * Human-readable error message.
         */
        String message,

        /**
         * Requested API path.
         */
        String path,

        /**
         * Optional validation errors.
         *
         * Example:
         *
         * {
         *     "email": "Please enter a valid email address",
         *     "password": "Password is required"
         * }
         */
        Map<String, String> validationErrors,

        /**
         * Optional list of additional errors.
         */
        List<String> errors
) {

    /**
     * Constructor for a normal API error without validation errors.
     */
    public ApiError(
            int status,
            String error,
            String message,
            String path
    ) {

        this(
                LocalDateTime.now(),
                status,
                error,
                message,
                path,
                null,
                null
        );
    }


    /**
     * Constructor for a validation error.
     */
    public ApiError(
            int status,
            String error,
            String message,
            String path,
            Map<String, String> validationErrors
    ) {

        this(
                LocalDateTime.now(),
                status,
                error,
                message,
                path,
                validationErrors,
                null
        );
    }


    /**
     * Constructor for multiple errors.
     */
    public ApiError(
            int status,
            String error,
            String message,
            String path,
            Map<String, String> validationErrors,
            List<String> errors
    ) {

        this(
                LocalDateTime.now(),
                status,
                error,
                message,
                path,
                validationErrors,
                errors
        );
    }
}