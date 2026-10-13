package com.skillswap.common.constant;

public final class AppConstants {

    private AppConstants() {
    }

    // Application
    public static final String APPLICATION_NAME =
            "SkillSwap Campus";

    public static final String API_VERSION =
            "v1";

    public static final String API_PREFIX =
            "/api/" + API_VERSION;

    // Pagination
    public static final int DEFAULT_PAGE = 0;

    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final int MAX_PAGE_SIZE = 100;

    // Authentication
    public static final String AUTHORIZATION_HEADER =
            "Authorization";

    public static final String BEARER_PREFIX =
            "Bearer ";

    // Common messages
    public static final String SUCCESS_MESSAGE =
            "Request successful";

    public static final String CREATED_MESSAGE =
            "Resource created successfully";

    public static final String UPDATED_MESSAGE =
            "Resource updated successfully";

    public static final String DELETED_MESSAGE =
            "Resource deleted successfully";

    public static final String NOT_FOUND_MESSAGE =
            "Resource not found";

    public static final String UNAUTHORIZED_MESSAGE =
            "Unauthorized access";

    public static final String FORBIDDEN_MESSAGE =
            "Access denied";

    public static final String INVALID_REQUEST_MESSAGE =
            "Invalid request";

    // Date/time
    public static final String DEFAULT_DATE_TIME_FORMAT =
            "yyyy-MM-dd HH:mm:ss";

    // File
    public static final String FILE_UPLOAD_ENDPOINT =
            API_PREFIX + "/files/upload";

    // Exchange
    public static final String EXCHANGE_REQUEST_ENDPOINT =
            API_PREFIX + "/exchange/requests";

    public static final String EXCHANGE_OFFER_ENDPOINT =
            API_PREFIX + "/exchange/offers";

    // Security
    public static final long DEFAULT_JWT_EXPIRATION =
            24 * 60 * 60 * 1000L;

    public static final int PASSWORD_MIN_LENGTH = 8;

    public static final int PASSWORD_MAX_LENGTH = 100;
}