package com.skillswap.identity.dto;

public record LoginResponse(

        Long userId,

        String email,

        String firstName,

        String lastName,

        String role,

        boolean verified,

        String accessToken,

        String refreshToken,

        long accessTokenExpiresInSeconds,

        long refreshTokenExpiresInSeconds

) {
}
