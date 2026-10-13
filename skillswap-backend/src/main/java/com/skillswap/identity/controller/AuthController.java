
package com.skillswap.identity.controller;

import com.skillswap.identity.dto.*;
import com.skillswap.identity.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<OtpResponse> register(
            @Valid
            @RequestBody
            RegisterRequest request
    ) {

        String message =
                authService.register(request);

        return ResponseEntity.ok(
                new OtpResponse(
                        message,
                        "EMAIL",
                        600
                )
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(
            @RequestParam String email,
            @Valid
            @RequestBody
            VerifyOtpRequest request
    ) {

        AuthResponse response =
                authService.verifyOtp(
                        email,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<OtpResponse> resendOtp(
            @RequestParam String email,
            @Valid
            @RequestBody
            ResendOtpRequest request
    ) {

        String message =
                authService.resendOtp(
                        email,
                        request
                );

        return ResponseEntity.ok(
                new OtpResponse(
                        message,
                        "EMAIL",
                        600
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid
            @RequestBody
            LoginRequest request,
            jakarta.servlet.http.HttpServletRequest httpRequest
    ) {

        String ipAddress =
                httpRequest.getRemoteAddr();

        String userAgent =
                httpRequest.getHeader("User-Agent");

        LoginResponse response =
                authService.login(
                        request,
                        ipAddress,
                        userAgent
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Valid
            @RequestBody
            RefreshTokenRequest request
    ) {

        return ResponseEntity.ok(
                authService.refreshToken(request)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid
            @RequestBody
            RefreshTokenRequest request
    ) {

        authService.logout(request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(
            Authentication authentication
    ) {

        authService.logoutAll(
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    /*
     * ============================================================
     * PASSWORD RESET - STEP 1
     * ============================================================
     *
     * User submits their email.
     *
     * Backend sends a 6-digit OTP to their Gmail.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<OtpResponse> forgotPassword(
            @Valid
            @RequestBody
            ForgotPasswordRequest request
    ) {

        authService.forgotPassword(request);

        return ResponseEntity.ok(
                new OtpResponse(
                        "Password reset OTP has been sent to your email.",
                        "EMAIL",
                        600
                )
        );
    }

    /*
     * ============================================================
     * PASSWORD RESET - STEP 2
     * ============================================================
     *
     * User submits the 6-digit OTP.
     *
     * Backend verifies the OTP and returns an internal
     * password-reset token.
     *
     * This token is NOT an email reset link.
     */
    @PostMapping("/verify-reset-otp")
    public ResponseEntity<?> verifyResetOtp(
            @RequestParam String email,
            @Valid
            @RequestBody
            VerifyOtpRequest request
    ) {

        String resetToken =
                authService.verifyPasswordResetOtp(
                        email,
                        request.otp()
                );

        return ResponseEntity.ok(
                new PasswordResetOtpResponse(
                        "OTP verified successfully. You can now create a new password.",
                        resetToken,
                        600
                )
        );
    }

    /*
     * ============================================================
     * PASSWORD RESET - STEP 3
     * ============================================================
     *
     * Frontend sends the internal reset token together with
     * the new password.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid
            @RequestBody
            ResetPasswordRequest request
    ) {

        authService.resetPassword(request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid
            @RequestBody
            ChangePasswordRequest request
    ) {

        authService.changePassword(
                authentication.getName(),
                request
        );

        return ResponseEntity.noContent().build();
    }
}

