package com.skillswap.identity.service;

import com.skillswap.identity.entity.LoginSession;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.LoginSessionRepository;
import com.skillswap.identity.security.JwtService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LoginSessionService {

    private final LoginSessionRepository repository;
    private final JwtService jwtService;

    public LoginSessionService(
            LoginSessionRepository repository,
            JwtService jwtService
    ) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginSession create(
            User user,
            String ipAddress,
            String userAgent
    ) {

        LoginSession session =
                new LoginSession();

        session.setUser(user);

        session.setSessionToken(
                UUID.randomUUID().toString()
        );

        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);

        session.setCreatedAt(
                LocalDateTime.now()
        );

        session.setLastActiveAt(
                LocalDateTime.now()
        );

        session.setExpiresAt(
                LocalDateTime.now().plusSeconds(
                        jwtService
                                .getRefreshTokenExpirationSeconds()
                )
        );

        session.setRevoked(false);

        return repository.save(session);
    }

    @Transactional
    public void touch(
            LoginSession session
    ) {

        session.setLastActiveAt(
                LocalDateTime.now()
        );

        repository.save(session);
    }

    @Transactional
    public void revoke(
            LoginSession session
    ) {

        session.setRevoked(true);

        repository.save(session);
    }

    @Transactional
    public void revokeAll(
            User user
    ) {

        List<LoginSession> sessions =
                repository
                        .findAllByUserOrderByLastActiveAtDesc(
                                user
                        );

        for (LoginSession session : sessions) {
            session.setRevoked(true);
        }

        repository.saveAll(sessions);
    }
}