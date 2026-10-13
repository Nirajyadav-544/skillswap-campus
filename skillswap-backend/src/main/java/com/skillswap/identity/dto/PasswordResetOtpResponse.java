
package com.skillswap.identity.dto;

public record PasswordResetOtpResponse(

        String message,

        String resetToken,

        long expiresInSeconds

) {
}

