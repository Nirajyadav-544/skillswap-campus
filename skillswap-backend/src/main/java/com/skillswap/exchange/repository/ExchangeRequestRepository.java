package com.skillswap.exchange.repository;

import com.skillswap.exchange.entity.ExchangeRequest;
import com.skillswap.exchange.entity.ExchangeStatus;
import com.skillswap.identity.entity.User;
import com.skillswap.matching.entity.MatchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExchangeRequestRepository
        extends JpaRepository<ExchangeRequest, Long> {

    List<ExchangeRequest> findByRequesterUser(User requesterUser);

    Page<ExchangeRequest> findByRequesterUser(
            User requesterUser,
            Pageable pageable
    );

    List<ExchangeRequest> findByRecipientUser(User recipientUser);

    Page<ExchangeRequest> findByRecipientUser(
            User recipientUser,
            Pageable pageable
    );

    List<ExchangeRequest> findByRequesterUserAndStatus(
            User requesterUser,
            ExchangeStatus status
    );

    List<ExchangeRequest> findByRecipientUserAndStatus(
            User recipientUser,
            ExchangeStatus status
    );

    Page<ExchangeRequest> findByRecipientUserAndStatus(
            User recipientUser,
            ExchangeStatus status,
            Pageable pageable
    );

    List<ExchangeRequest> findByStatus(
            ExchangeStatus status
    );

    Page<ExchangeRequest> findByStatus(
            ExchangeStatus status,
            Pageable pageable
    );

    Optional<ExchangeRequest> findByMatchResult(
            MatchResult matchResult
    );

    Optional<ExchangeRequest> findByMatchResultId(
            Long matchResultId
    );

    boolean existsByMatchResultId(
            Long matchResultId
    );

    boolean existsByRequesterUserAndRecipientUserAndStatus(
            User requesterUser,
            User recipientUser,
            ExchangeStatus status
    );

    long countByRequesterUser(
            User requesterUser
    );

    long countByRecipientUser(
            User recipientUser
    );

    long countByStatus(
            ExchangeStatus status
    );

    long countByRequesterUserAndStatus(
            User requesterUser,
            ExchangeStatus status
    );

    long countByRecipientUserAndStatus(
            User recipientUser,
            ExchangeStatus status
    );

    void deleteByRequesterUser(
            User requesterUser
    );

    void deleteByRecipientUser(
            User recipientUser
    );
}