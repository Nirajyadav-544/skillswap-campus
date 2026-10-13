package com.skillswap.identity.dto;

public record OtpResponse(

        String message,

        String verificationMethod,

        long expiresInSeconds
) {
}