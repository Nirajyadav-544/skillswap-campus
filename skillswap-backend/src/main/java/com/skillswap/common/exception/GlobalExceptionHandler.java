package com.skillswap.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Centralized exception handler for the SkillSwap Campus backend.
 *
 * All REST controllers can throw normal Java exceptions and this
 * class converts them into a consistent ApiError response.
 *
 * Benefits:
 *
 * 1. Consistent API error format
 * 2. Centralized validation handling
 * 3. Cleaner controllers
 * 4. No stack traces returned to clients
 * 5. Better frontend error handling
 * 6. Easier API debugging
 * 7. Consistent HTTP status codes
 */
@RestControllerAdvice
public class GlobalExceptionHandler
        extends ResponseEntityExceptionHandler {


    // ============================================================
    // BAD REQUEST
    // ============================================================

    /**
     * Handles application-level bad requests.
     *
     * HTTP 400.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(
            BadRequestException exception,
            HttpServletRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        safeMessage(
                                exception.getMessage(),
                                "Bad request"
                        ),
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }


    // ============================================================
    // UNAUTHORIZED
    // ============================================================

    /**
     * Handles application-level unauthorized requests.
     *
     * HTTP 401.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(
            UnauthorizedException exception,
            HttpServletRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        safeMessage(
                                exception.getMessage(),
                                "Authentication is required"
                        ),
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }


    // ============================================================
    // RESOURCE NOT FOUND
    // ============================================================

    /**
     * Handles missing resources.
     *
     * HTTP 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        safeMessage(
                                exception.getMessage(),
                                "Resource not found"
                        ),
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }


    // ============================================================
    // VALIDATION ERROR
    // ============================================================

    /**
     * Handles @Valid request-body validation failures.
     *
     * Example:
     *
     * {
     *     "email": "Please enter a valid email address",
     *     "password": "Password must be between 8 and 100 characters"
     * }
     *
     * HTTP 400.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {

        Map<String, String> validationErrors =
                new LinkedHashMap<>();

        for (FieldError fieldError :
                exception.getBindingResult().getFieldErrors()) {

            String fieldName =
                    fieldError.getField();

            String message =
                    fieldError.getDefaultMessage();

            if (message == null || message.isBlank()) {
                message = "Invalid value";
            }

            /*
             * If the same field somehow produces multiple errors,
             * preserve the first useful error rather than replacing
             * it with an arbitrary later message.
             */
            validationErrors.putIfAbsent(
                    fieldName,
                    message
            );
        }

        String path =
                getRequestPath(request);

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Request validation failed",
                        path,
                        validationErrors,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }


    // ============================================================
    // CONSTRAINT VIOLATION
    // ============================================================

    /**
     * Handles validation failures generated by
     * @Validated / constraint validation.
     *
     * HTTP 400.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {

        Map<String, String> validationErrors =
                new LinkedHashMap<>();

        exception.getConstraintViolations()
                .forEach(violation -> {

                    String propertyPath =
                            violation
                                    .getPropertyPath()
                                    .toString();

                    String message =
                            violation.getMessage();

                    validationErrors.put(
                            propertyPath,
                            message
                    );
                });

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Request validation failed",
                        getRequestPath(request),
                        validationErrors,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }


    // ============================================================
    // MALFORMED JSON
    // ============================================================

    /**
     * Handles malformed JSON request bodies.
     *
     * Example:
     *
     * {
     *     "email": "test@example.com",
     *
     *
     * Missing closing brace.
     *
     * HTTP 400.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Request body is missing or contains invalid JSON",
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }


    // ============================================================
    // METHOD NOT SUPPORTED
    // ============================================================

    /**
     * Handles unsupported HTTP methods.
     *
     * Example:
     *
     * Sending GET when endpoint only supports POST.
     *
     * HTTP 405.
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {

        String message =
                "HTTP method "
                        + exception.getMethod()
                        + " is not supported for this endpoint";

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                        message,
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(error);
    }


    // ============================================================
    // MEDIA TYPE NOT SUPPORTED
    // ============================================================

    /**
     * Handles unsupported Content-Type values.
     *
     * Example:
     *
     * Sending text/plain when application/json is expected.
     *
     * HTTP 415.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(),
                        "Unsupported request content type",
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(error);
    }


    // ============================================================
    // NO HANDLER FOUND
    // ============================================================

    /**
     * Handles requests to an endpoint that does not exist.
     *
     * HTTP 404.
     *
     * This may be used when Spring MVC is configured to throw
     * NoHandlerFoundException for unknown endpoints.
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "The requested endpoint was not found",
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }


    // ============================================================
    // DATA INTEGRITY
    // ============================================================

    /**
     * Handles database constraint violations.
     *
     * Examples:
     *
     * - Duplicate email
     * - Duplicate profile
     * - Unique constraint violation
     * - Foreign key constraint violation
     *
     * HTTP 409.
     *
     * We intentionally do not expose raw database messages because
     * they can reveal internal database details.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        "The requested operation conflicts with existing data",
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }


    // ============================================================
    // BAD CREDENTIALS
    // ============================================================

    /**
     * Handles Spring Security bad credentials.
     *
     * HTTP 401.
     *
     * We intentionally use a generic message rather than exposing
     * whether the email or password was incorrect.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(
            BadCredentialsException exception,
            HttpServletRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "Invalid email or password",
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }


    // ============================================================
    // DISABLED ACCOUNT
    // ============================================================

    /**
     * Handles disabled user accounts.
     *
     * HTTP 403.
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledException(
            DisabledException exception,
            HttpServletRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.FORBIDDEN.value(),
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        "This account is disabled",
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }


    // ============================================================
    // OTHER AUTHENTICATION EXCEPTIONS
    // ============================================================

    /**
     * Handles other Spring Security authentication failures.
     *
     * HTTP 401.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(
            AuthenticationException exception,
            HttpServletRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "Authentication failed",
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }


    // ============================================================
    // ILLEGAL ARGUMENT
    // ============================================================

    /**
     * Handles IllegalArgumentException.
     *
     * This provides backward compatibility with existing services
     * that may currently throw IllegalArgumentException.
     *
     * HTTP 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        safeMessage(
                                exception.getMessage(),
                                "Invalid request"
                        ),
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }


    // ============================================================
    // ILLEGAL STATE
    // ============================================================

    /**
     * Handles IllegalStateException.
     *
     * This is useful for the current profile/authentication services
     * while they transition toward the custom exception hierarchy.
     *
     * HTTP 400.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalStateException(
            IllegalStateException exception,
            HttpServletRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        safeMessage(
                                exception.getMessage(),
                                "The requested operation cannot be completed"
                        ),
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }


    // ============================================================
    // NULL POINTER
    // ============================================================

    /**
     * Handles unexpected NullPointerException.
     *
     * The actual exception details are intentionally not returned
     * to the client.
     *
     * HTTP 500.
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiError> handleNullPointerException(
            NullPointerException exception,
            HttpServletRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "An unexpected server error occurred",
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }


    // ============================================================
    // GENERIC EXCEPTION
    // ============================================================

    /**
     * Final safety net for unexpected exceptions.
     *
     * HTTP 500.
     *
     * Never expose exception class names, stack traces, SQL queries,
     * filesystem paths, tokens, passwords, or other internal
     * information to API clients.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception exception,
            HttpServletRequest request
    ) {

        ApiError error =
                new ApiError(
                        LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "An unexpected server error occurred",
                        getRequestPath(request),
                        null,
                        null
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }


    // ============================================================
    // REQUEST PATH - SERVLET REQUEST
    // ============================================================

    /**
     * Safely retrieves the HTTP request path.
     */
    private String getRequestPath(
            HttpServletRequest request
    ) {

        if (request == null) {
            return null;
        }

        return request.getRequestURI();
    }


    // ============================================================
    // REQUEST PATH - WEB REQUEST
    // ============================================================

    /**
     * Safely retrieves the request path from Spring's WebRequest.
     */
    private String getRequestPath(
            WebRequest request
    ) {

        if (request == null) {
            return null;
        }

        String description =
                request.getDescription(false);

        if (description == null) {
            return null;
        }

        /*
         * Spring usually returns:
         *
         * uri=/api/v1/profile/me
         *
         * Remove the "uri=" prefix for a cleaner API response.
         */
        if (description.startsWith("uri=")) {
            return description.substring(4);
        }

        return description;
    }


    // ============================================================
    // SAFE MESSAGE
    // ============================================================

    /**
     * Returns the supplied message if it contains useful text.
     *
     * Otherwise returns the fallback.
     */
    private String safeMessage(
            String message,
            String fallback
    ) {

        if (message == null || message.isBlank()) {
            return fallback;
        }

        return message;
    }
}