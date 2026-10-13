package com.skillswap.exchange.repository;

import com.skillswap.exchange.entity.ExchangeOffer;
import com.skillswap.exchange.entity.ExchangeResponseStatus;
import com.skillswap.exchange.entity.ExchangeRequest;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExchangeOfferRepository
        extends JpaRepository<ExchangeOffer, Long> {

    List<ExchangeOffer> findByExchangeRequest(
            ExchangeRequest exchangeRequest
    );

    List<ExchangeOffer> findByExchangeRequestId(
            Long exchangeRequestId
    );

    Page<ExchangeOffer> findByExchangeRequestId(
            Long exchangeRequestId,
            Pageable pageable
    );

    List<ExchangeOffer> findByUser(
            User user
    );

    Page<ExchangeOffer> findByUser(
            User user,
            Pageable pageable
    );

    List<ExchangeOffer> findByUserAndResponseStatus(
            User user,
            ExchangeResponseStatus responseStatus
    );

    Optional<ExchangeOffer> findByExchangeRequestIdAndUserId(
            Long exchangeRequestId,
            Long userId
    );

    boolean existsByExchangeRequestIdAndUserId(
            Long exchangeRequestId,
            Long userId
    );

    boolean existsByExchangeRequestIdAndSkillName(
            Long exchangeRequestId,
            String skillName
    );

    long countByExchangeRequest(
            ExchangeRequest exchangeRequest
    );

    long countByUser(
            User user
    );

    long countByResponseStatus(
            ExchangeResponseStatus responseStatus
    );

    void deleteByExchangeRequest(
            ExchangeRequest exchangeRequest
    );

    void deleteByExchangeRequestId(
            Long exchangeRequestId
    );
}