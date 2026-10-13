package com.skillswap.exchange.repository;

import com.skillswap.exchange.entity.ExchangeParticipant;
import com.skillswap.exchange.entity.ExchangeRequest;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExchangeParticipantRepository
        extends JpaRepository<ExchangeParticipant, Long> {

    List<ExchangeParticipant> findByExchangeRequest(
            ExchangeRequest exchangeRequest
    );

    List<ExchangeParticipant> findByExchangeRequestId(
            Long exchangeRequestId
    );

    Page<ExchangeParticipant> findByExchangeRequestId(
            Long exchangeRequestId,
            Pageable pageable
    );

    List<ExchangeParticipant> findByUser(
            User user
    );

    Page<ExchangeParticipant> findByUser(
            User user,
            Pageable pageable
    );

    List<ExchangeParticipant> findByUserAndActiveTrue(
            User user
    );

    Optional<ExchangeParticipant> findByExchangeRequestIdAndUserId(
            Long exchangeRequestId,
            Long userId
    );

    boolean existsByExchangeRequestIdAndUserId(
            Long exchangeRequestId,
            Long userId
    );

    boolean existsByExchangeRequestIdAndUserIdAndActiveTrue(
            Long exchangeRequestId,
            Long userId
    );

    long countByExchangeRequest(
            ExchangeRequest exchangeRequest
    );

    long countByUser(
            User user
    );

    long countByUserAndActiveTrue(
            User user
    );

    long countByExchangeRequestId(
            Long exchangeRequestId
    );

    void deleteByExchangeRequest(
            ExchangeRequest exchangeRequest
    );

    void deleteByExchangeRequestId(
            Long exchangeRequestId
    );
}