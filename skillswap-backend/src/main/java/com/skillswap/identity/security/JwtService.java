package com.skillswap.identity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Service
 *
 * Responsible for:
 * - Creating access tokens
 * - Creating refresh tokens
 * - Extracting username/email from tokens
 * - Extracting claims
 * - Validating tokens
 * - Checking expiration
 * - Checking token type
 *
 * Architecture:
 *
 * Access Token
 *   - Short lived
 *   - Used for API authorization
 *
 * Refresh Token
 *   - Long lived
 *   - Used to obtain a new access token
 *
 * This service does NOT:
 * - Authenticate users directly
 * - Access the database
 * - Store refresh tokens
 * - Manage login sessions
 *
 * Those responsibilities belong to other services.
 */
@Service
public class JwtService {

    // ============================================================
    // CONFIGURATION
    // ============================================================

    private final String jwtSecret;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    private static final String ACCESS_TOKEN = "ACCESS";

    private static final String REFRESH_TOKEN = "REFRESH";

    /**
     * Constructor.
     *
     * Values come from application.properties:
     *
     * app.jwt.secret
     * app.jwt.access-token-expiration
     * app.jwt.refresh-token-expiration
     */
    public JwtService(
        
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${app.jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {

        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalArgumentException(
                    "JWT secret must not be empty"
            );
        }

        if (accessTokenExpiration <= 0) {
            throw new IllegalArgumentException(
                    "JWT access token expiration must be greater than zero"
            );
        }

        if (refreshTokenExpiration <= 0) {
            throw new IllegalArgumentException(
                    "JWT refresh token expiration must be greater than zero"
            );
        }

        this.jwtSecret = jwtSecret;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }


    // ============================================================
    // SECRET KEY
    // ============================================================

    /**
     * Creates the signing key used by JWT.
     *
     * HMAC SHA-256 is used by default.
     */
    private SecretKey getSigningKey() {

        byte[] keyBytes =
                jwtSecret.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }


    // ============================================================
    // ACCESS TOKEN
    // ============================================================

    /**
     * Generate a standard access token.
     *
     * The username is normally the user's email address.
     */
    public String generateAccessToken(
            UserDetails userDetails
    ) {

        return generateAccessToken(
                userDetails.getUsername(),
                new HashMap<>()
        );
    }


    /**
     * Generate an access token with additional claims.
     */
    public String generateAccessToken(
            String username,
            Map<String, Object> additionalClaims
    ) {

        Map<String, Object> claims =
                new HashMap<>();

        if (additionalClaims != null) {
            claims.putAll(additionalClaims);
        }

        claims.put(
                TOKEN_TYPE_CLAIM,
                ACCESS_TOKEN
        );

        return buildToken(
                claims,
                username,
                accessTokenExpiration
        );
    }


    /**
     * Generate an access token directly from username.
     */
    public String generateAccessToken(
            String username
    ) {

        return generateAccessToken(
                username,
                new HashMap<>()
        );
    }


    // ============================================================
    // REFRESH TOKEN
    // ============================================================

    /**
     * Generate a refresh token.
     */
    public String generateRefreshToken(
            UserDetails userDetails
    ) {

        return generateRefreshToken(
                userDetails.getUsername(),
                new HashMap<>()
        );
    }


    /**
     * Generate refresh token with additional claims.
     */
    public String generateRefreshToken(
            String username,
            Map<String, Object> additionalClaims
    ) {

        Map<String, Object> claims =
                new HashMap<>();

        if (additionalClaims != null) {
            claims.putAll(additionalClaims);
        }

        claims.put(
                TOKEN_TYPE_CLAIM,
                REFRESH_TOKEN
        );

        return buildToken(
                claims,
                username,
                refreshTokenExpiration
        );
    }


    /**
     * Generate refresh token directly from username.
     */
    public String generateRefreshToken(
            String username
    ) {

        return generateRefreshToken(
                username,
                new HashMap<>()
        );
    }


    // ============================================================
    // TOKEN BUILDING
    // ============================================================

    /**
     * Creates the actual signed JWT.
     */
    private String buildToken(
            Map<String, Object> claims,
            String username,
            long expiration
    ) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "JWT username must not be empty"
            );
        }

        long now = System.currentTimeMillis();

        Date issuedAt =
                new Date(now);

        Date expirationDate =
                new Date(now + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }


    // ============================================================
    // CLAIM EXTRACTION
    // ============================================================

    /**
     * Extract username/email from JWT.
     */
    public String extractUsername(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }


    /**
     * Extract token expiration.
     */
    public Date extractExpiration(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }


    /**
     * Extract issued-at date.
     */
    public Date extractIssuedAt(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getIssuedAt
        );
    }


    /**
     * Extract token type.
     */
    public String extractTokenType(
            String token
    ) {

        return extractClaim(
                token,
                claims -> claims.get(
                        TOKEN_TYPE_CLAIM,
                        String.class
                )
        );
    }


    /**
     * Extract arbitrary claim.
     */
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        Claims claims =
                extractAllClaims(token);

        return claimsResolver.apply(claims);
    }


    /**
     * Extract all claims from token.
     *
     * This validates:
     * - Signature
     * - Token structure
     * - Parsing
     */
    public Claims extractAllClaims(
            String token
    ) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "JWT token must not be empty"
            );
        }

        Jws<Claims> parsedToken =
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token);

        return parsedToken.getPayload();
    }


    // ============================================================
    // EXPIRATION
    // ============================================================

    /**
     * Check whether token has expired.
     */
    public boolean isTokenExpired(
            String token
    ) {

        try {

            Date expiration =
                    extractExpiration(token);

            return expiration.before(
                    new Date()
            );

        } catch (Exception exception) {

            return true;
        }
    }


    /**
     * Check whether token is still valid.
     */
    public boolean isTokenNotExpired(
            String token
    ) {

        return !isTokenExpired(token);
    }


    // ============================================================
    // TOKEN TYPE
    // ============================================================

    /**
     * Check whether token is an access token.
     */
    public boolean isAccessToken(
            String token
    ) {

        try {

            String tokenType =
                    extractTokenType(token);

            return ACCESS_TOKEN.equals(tokenType);

        } catch (Exception exception) {

            return false;
        }
    }


    /**
     * Check whether token is a refresh token.
     */
    public boolean isRefreshToken(
            String token
    ) {

        try {

            String tokenType =
                    extractTokenType(token);

            return REFRESH_TOKEN.equals(tokenType);

        } catch (Exception exception) {

            return false;
        }
    }


    // ============================================================
    // USER VALIDATION
    // ============================================================

    /**
     * Validate an access token against a UserDetails object.
     *
     * Validation checks:
     *
     * 1. Username matches
     * 2. Token is not expired
     * 3. Token is actually an access token
     */
    public boolean isAccessTokenValid(
            String token,
            UserDetails userDetails
    ) {

        try {

            String username =
                    extractUsername(token);

            return username != null
                    && username.equals(
                    userDetails.getUsername()
            )
                    && isTokenNotExpired(token)
                    && isAccessToken(token);

        } catch (Exception exception) {

            return false;
        }
    }


    /**
     * Validate a refresh token against UserDetails.
     */
    public boolean isRefreshTokenValid(
            String token,
            UserDetails userDetails
    ) {

        try {

            String username =
                    extractUsername(token);

            return username != null
                    && username.equals(
                    userDetails.getUsername()
            )
                    && isTokenNotExpired(token)
                    && isRefreshToken(token);

        } catch (Exception exception) {

            return false;
        }
    }


    /**
     * Generic JWT validation.
     *
     * This checks:
     * - username
     * - expiration
     */
    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        try {

            String username =
                    extractUsername(token);

            return username != null
                    && username.equals(
                    userDetails.getUsername()
            )
                    && isTokenNotExpired(token);

        } catch (Exception exception) {

            return false;
        }
    }


    // ============================================================
    // RAW TOKEN VALIDATION
    // ============================================================

    /**
     * Determine whether a JWT is structurally valid.
     */
    public boolean isValidToken(
            String token
    ) {

        try {

            extractAllClaims(token);

            return !isTokenExpired(token);

        } catch (
                JwtException
                | IllegalArgumentException exception
        ) {

            return false;
        }
    }


    /**
     * Determine whether a JWT can be parsed.
     */
    public boolean canParseToken(
            String token
    ) {

        try {

            extractAllClaims(token);

            return true;

        } catch (
                JwtException
                | IllegalArgumentException exception
        ) {

            return false;
        }
    }


    // ============================================================
    // EXPIRATION INFORMATION
    // ============================================================

    /**
     * Returns access-token lifetime in milliseconds.
     */
    public long getAccessTokenExpiration() {

        return accessTokenExpiration;
    }


    /**
     * Returns refresh-token lifetime in milliseconds.
     */
    public long getRefreshTokenExpiration() {

        return refreshTokenExpiration;
    }


    /**
     * Returns access-token lifetime in seconds.
     */
    public long getAccessTokenExpirationSeconds() {

        return accessTokenExpiration / 1000;
    }


    /**
     * Returns refresh-token lifetime in seconds.
     */
    public long getRefreshTokenExpirationSeconds() {

        return refreshTokenExpiration / 1000;
    }


    /**
     * Returns remaining lifetime of token.
     *
     * Returns 0 if token is expired or invalid.
     */
    public long getRemainingLifetime(
            String token
    ) {

        try {

            Date expiration =
                    extractExpiration(token);

            long remaining =
                    expiration.getTime()
                            - System.currentTimeMillis();

            return Math.max(
                    remaining,
                    0
            );

        } catch (Exception exception) {

            return 0;
        }
    }


    /**
     * Returns remaining lifetime in seconds.
     */
    public long getRemainingLifetimeSeconds(
            String token
    ) {

        return getRemainingLifetime(token) / 1000;
    }


    // ============================================================
    // TOKEN INFORMATION
    // ============================================================

    /**
     * Returns true if token contains a subject.
     */
    public boolean hasUsername(
            String token
    ) {

        try {

            String username =
                    extractUsername(token);

            return username != null
                    && !username.isBlank();

        } catch (Exception exception) {

            return false;
        }
    }


    /**
     * Returns true if token has an expiration date.
     */
    public boolean hasExpiration(
            String token
    ) {

        try {

            return extractExpiration(token) != null;

        } catch (Exception exception) {

            return false;
        }
    }


    /**
     * Returns true if token is currently usable.
     */
    public boolean isUsable(
            String token
    ) {

        return isValidToken(token)
                && hasUsername(token)
                && hasExpiration(token);
    }


    // ============================================================
    // SECURITY HELPERS
    // ============================================================

    /**
     * Safely extract username.
     *
     * Instead of throwing an exception,
     * this returns null for invalid tokens.
     */
    public String tryExtractUsername(
            String token
    ) {

        try {

            return extractUsername(token);

        } catch (Exception exception) {

            return null;
        }
    }


    /**
     * Safely extract token type.
     */
    public String tryExtractTokenType(
            String token
    ) {

        try {

            return extractTokenType(token);

        } catch (Exception exception) {

            return null;
        }
    }


    /**
     * Safely extract expiration.
     */
    public Date tryExtractExpiration(
            String token
    ) {

        try {

            return extractExpiration(token);

        } catch (Exception exception) {

            return null;
        }
    }
}