package com.skillswap.identity.dto;

public record AuthResponse(

        Long userId,

        String email,

        String firstName,

        String lastName,

        String role,

        boolean verified,

        String accessToken,

        String refreshToken
) {
}