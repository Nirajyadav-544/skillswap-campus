package com.skillswap.identity.repository;

import com.skillswap.identity.entity.LoginSession;
import com.skillswap.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoginSessionRepository
        extends JpaRepository<LoginSession, Long> {

    Optional<LoginSession>
    findBySessionToken(String sessionToken);

    List<LoginSession>
    findAllByUserOrderByLastActiveAtDesc(User user);

    void deleteAllByUser(User user);
}