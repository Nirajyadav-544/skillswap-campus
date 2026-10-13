package com.skillswap.identity.service;

import com.skillswap.identity.dto.*;
import com.skillswap.identity.entity.RefreshToken;
import com.skillswap.identity.entity.Role;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.entity.VerificationMethod;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.identity.security.JwtService;
import com.skillswap.notification.dto.CreateNotificationRequest;
import com.skillswap.notification.entity.NotificationChannel;
import com.skillswap.notification.entity.NotificationType;
import com.skillswap.notification.service.NotificationService;
import com.skillswap.profile.entity.StudentProfile;
import com.skillswap.profile.repository.StudentProfileRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final LoginSessionService loginSessionService;
    private final PasswordResetService passwordResetService;
    private final AuthenticationManager authenticationManager;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final StudentProfileRepository studentProfileRepository;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            OtpService otpService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            LoginSessionService loginSessionService,
            PasswordResetService passwordResetService,
            AuthenticationManager authenticationManager,
            NotificationService notificationService,
            EmailService emailService,
            StudentProfileRepository studentProfileRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.loginSessionService = loginSessionService;
        this.passwordResetService = passwordResetService;
        this.authenticationManager = authenticationManager;
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.studentProfileRepository = studentProfileRepository;
    }

    @Transactional
    public String register(RegisterRequest request) {

        String email =
                request.email()
                        .trim()
                        .toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists"
            );
        }

        User user = new User();

        user.setEmail(email);
        user.setPassword(
                passwordEncoder.encode(request.password())
        );
        user.setFirstName(
                request.firstName().trim()
        );
        user.setLastName(
                request.lastName().trim()
        );

        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setAccountVerified(false);
        user.setRole(Role.STUDENT);
        user.setVerificationMethod(
                VerificationMethod.EMAIL
        );

        User savedUser =
                userRepository.save(user);

        /*
         * Create the basic student profile immediately.
         *
         * This guarantees that every newly registered student
         * has a StudentProfile row, even before OTP verification.
         */
        ensureStudentProfileExists(savedUser);

        otpService.sendOtp(
                savedUser,
                VerificationMethod.EMAIL
        );

        return "Registration successful. Verification OTP has been sent to your email.";
    }

    @Transactional
    public AuthResponse verifyOtp(
            String email,
            VerifyOtpRequest request
    ) {

        User user =
                getUserByEmail(email);

        otpService.verifyOtp(
                user,
                VerificationMethod.EMAIL,
                request.otp()
        );

        userRepository.save(user);

        /*
         * Safety net for older accounts or any account whose
         * profile was missing before this flow was introduced.
         */
        ensureStudentProfileExists(user);

        return buildAuthResponse(
                user,
                null,
                null
        );
    }

    @Transactional
    public String resendOtp(
            String email,
            ResendOtpRequest request
    ) {

        User user =
                getUserByEmail(email);

        if (user.isAccountVerified()) {
            throw new IllegalStateException(
                    "Account is already verified"
            );
        }

        otpService.sendOtp(
                user,
                VerificationMethod.EMAIL
        );

        return "A new verification OTP has been sent to your email.";
    }

    @Transactional
    public LoginResponse login(
            LoginRequest request,
            String ipAddress,
            String userAgent
    ) {

        String email =
                request.email()
                        .trim()
                        .toLowerCase();

        User user =
                getUserByEmail(email);

        if (!user.isEnabled()) {
            throw new IllegalStateException(
                    "Account is disabled"
            );
        }

        if (!user.isAccountVerified()) {
            throw new IllegalStateException(
                    "Please verify your account before logging in"
            );
        }

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.password()
                        )
                );

        if (!authentication.isAuthenticated()) {
            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        /*
         * Repair/safety mechanism for older accounts.
         *
         * This runs only AFTER successful authentication.
         */
        ensureStudentProfileExists(user);

        String accessToken =
                jwtService.generateAccessToken(email);

        String refreshToken =
                jwtService.generateRefreshToken(email);

        refreshTokenService.create(
                user,
                refreshToken
        );

        loginSessionService.create(
                user,
                ipAddress,
                userAgent
        );

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.isAccountVerified(),
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationSeconds(),
                jwtService.getRefreshTokenExpirationSeconds()
        );
    }

    @Transactional
    public LoginResponse refreshToken(
            RefreshTokenRequest request
    ) {

        String token =
                request.refreshToken().trim();

        if (!jwtService.isRefreshToken(token)) {
            throw new IllegalArgumentException(
                    "Invalid refresh token"
            );
        }

        RefreshToken storedToken =
                refreshTokenService.findByToken(token);

        refreshTokenService.validate(
                storedToken
        );

        User user =
                storedToken.getUser();

        if (!user.isEnabled()) {
            throw new IllegalStateException(
                    "Account is disabled"
            );
        }

        String accessToken =
                jwtService.generateAccessToken(
                        user.getEmail()
                );

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.isAccountVerified(),
                accessToken,
                token,
                jwtService.getAccessTokenExpirationSeconds(),
                jwtService.getRefreshTokenExpirationSeconds()
        );
    }

    @Transactional
    public void logout(
            RefreshTokenRequest request
    ) {

        String token =
                request.refreshToken().trim();

        RefreshToken refreshToken =
                refreshTokenService.findByToken(token);

        refreshTokenService.revoke(
                refreshToken
        );
    }

    @Transactional
    public void logoutAll(
            String email
    ) {

        User user =
                getUserByEmail(email);

        refreshTokenService.revokeAll(user);
        loginSessionService.revokeAll(user);
    }

    /**
     * Sends a password-reset OTP to the user's email.
     *
     * This uses the same Gmail OTP system as account verification,
     * but the OTP is verified separately so password reset does
     * not accidentally mark the account as verified.
     */
    @Transactional
    public void forgotPassword(
            ForgotPasswordRequest request
    ) {

        String email =
                request.email()
                        .trim()
                        .toLowerCase();

        User user =
                getUserByEmail(email);

        if (!user.isEnabled()) {
            throw new IllegalStateException(
                    "Account is disabled"
            );
        }

        otpService.sendOtp(
                user,
                VerificationMethod.EMAIL
        );
    }

    /**
     * Verifies the password-reset OTP and creates a temporary
     * password-reset token.
     */
    @Transactional
    public String verifyPasswordResetOtp(
            String email,
            String otp
    ) {

        User user =
                getUserByEmail(email);

        if (!user.isEnabled()) {
            throw new IllegalStateException(
                    "Account is disabled"
            );
        }

        otpService.verifyPasswordResetOtp(
                user,
                VerificationMethod.EMAIL,
                otp
        );

        return passwordResetService.createResetToken(
                user
        );
    }

    /**
     * Changes the password using the temporary reset token.
     *
     * Existing refresh tokens and login sessions are revoked
     * after a successful password reset.
     */
    @Transactional
    public void resetPassword(
            ResetPasswordRequest request
    ) {

        User user =
                passwordResetService.getUserByResetToken(
                        request.token()
                );

        passwordResetService.resetPassword(
                request.token(),
                request.newPassword()
        );

        refreshTokenService.revokeAll(user);
        loginSessionService.revokeAll(user);

        userRepository.save(user);
    }

    @Transactional
    public void changePassword(
            String email,
            ChangePasswordRequest request
    ) {

        User user =
                getUserByEmail(email);

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Current password is incorrect"
            );
        }

        if (request.currentPassword()
                .equals(request.newPassword())) {

            throw new IllegalArgumentException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        userRepository.save(user);

        refreshTokenService.revokeAll(user);
        loginSessionService.revokeAll(user);

        CreateNotificationRequest notificationRequest =
                new CreateNotificationRequest();

        notificationRequest.setUserId(
                user.getId()
        );

        notificationRequest.setType(
                NotificationType.SYSTEM
        );

        notificationRequest.setChannel(
                NotificationChannel.IN_APP
        );

        notificationRequest.setTitle(
                "Password changed successfully"
        );

        notificationRequest.setMessage(
                "Your SkillSwap Campus password was changed successfully. "
                        + "For your security, your existing login sessions "
                        + "have been signed out."
        );

        notificationService.create(
                notificationRequest
        );

        emailService.sendPasswordChangedEmail(
                user.getEmail()
        );
    }

    /**
     * Ensures that a user has exactly one basic StudentProfile.
     *
     * Existing profiles are never modified.
     * If the profile is missing, a new incomplete profile is created.
     */
    private void ensureStudentProfileExists(User user) {

        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException(
                    "User is required to create student profile"
            );
        }

        if (studentProfileRepository.existsByUserId(
                user.getId()
        )) {
            return;
        }

        StudentProfile profile =
                new StudentProfile();

        profile.setUser(user);

        studentProfileRepository.save(profile);
    }

    private User getUserByEmail(
            String email
    ) {

        if (email == null ||
                email.isBlank()) {

            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        return userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    private AuthResponse buildAuthResponse(
            User user,
            String accessToken,
            String refreshToken
    ) {

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.isAccountVerified(),
                accessToken,
                refreshToken
        );
    }
}