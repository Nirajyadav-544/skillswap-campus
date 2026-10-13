package com.skillswap.identity.service;

import com.skillswap.identity.entity.RefreshToken;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.RefreshTokenRepository;
import com.skillswap.identity.security.JwtService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService
    ) {
        this.refreshTokenRepository =
                refreshTokenRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public RefreshToken create(
            User user,
            String token
    ) {

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setToken(token);

        refreshToken.setCreatedAt(
                LocalDateTime.now()
        );

        refreshToken.setExpiresAt(
                LocalDateTime.now().plusSeconds(
                        jwtService
                                .getRefreshTokenExpirationSeconds()
                )
        );

        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(
                refreshToken
        );
    }

    @Transactional(readOnly = true)
    public RefreshToken findByToken(
            String token
    ) {

        return refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid refresh token"
                        )
                );
    }

    @Transactional
    public void validate(
            RefreshToken refreshToken
    ) {

        if (!refreshToken.isUsable()) {

            throw new IllegalArgumentException(
                    "Refresh token is expired or revoked"
            );
        }
    }

    @Transactional
    public void revoke(
            RefreshToken refreshToken
    ) {

        refreshToken.setRevoked(true);

        refreshToken.setRevokedAt(
                LocalDateTime.now()
        );

        refreshTokenRepository.save(
                refreshToken
        );
    }

    @Transactional
    public void revokeAll(User user) {

        List<RefreshToken> tokens =
                refreshTokenRepository
                        .findAllByUser(user);

        LocalDateTime now =
                LocalDateTime.now();

        for (RefreshToken token : tokens) {

            token.setRevoked(true);
            token.setRevokedAt(now);
        }

        refreshTokenRepository.saveAll(tokens);
    }
}