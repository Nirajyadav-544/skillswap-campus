package com.skillswap.identity.service;

import com.skillswap.common.exception.UnauthorizedException;
import com.skillswap.identity.dto.LoginRequest;
import com.skillswap.identity.dto.LoginResponse;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.identity.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoginService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        if (request == null) {
            throw new UnauthorizedException("Login request is required");
        }

        if (request.email() == null || request.email().isBlank()) {
            throw new UnauthorizedException("Email is required");
        }

        if (request.password() == null || request.password().isBlank()) {
            throw new UnauthorizedException("Password is required");
        }

        String email = request.email()
                .trim()
                .toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "Invalid email or password"
                        )
                );

        if (!user.isEnabled()) {
            throw new UnauthorizedException(
                    "Your account is disabled"
            );
        }

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    request.password()
                            )
                    );

            if (authentication == null
                    || !authentication.isAuthenticated()) {
                throw new UnauthorizedException(
                        "Invalid email or password"
                );
            }

        } catch (UnauthorizedException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnauthorizedException(
                    "Invalid email or password"
            );
        }

        String accessToken =
                jwtService.generateAccessToken(email);

        String refreshToken =
                jwtService.generateRefreshToken(email);

        boolean verified = user.isAccountVerified();

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                verified,
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationSeconds(),
                jwtService.getRefreshTokenExpirationSeconds()
        );
    }
}