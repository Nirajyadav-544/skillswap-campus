package com.skillswap.exchange.controller;

import com.skillswap.exchange.dto.ExchangeRequestResponse;
import com.skillswap.exchange.dto.ExchangeSearchRequest;
import com.skillswap.exchange.dto.ExchangeSummaryResponse;
import com.skillswap.exchange.entity.ExchangeStatus;
import com.skillswap.exchange.service.ExchangeDiscoveryService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchange/discovery")
public class ExchangeDiscoveryController {

    private final ExchangeDiscoveryService exchangeDiscoveryService;

    public ExchangeDiscoveryController(
            ExchangeDiscoveryService exchangeDiscoveryService
    ) {
        this.exchangeDiscoveryService =
                exchangeDiscoveryService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ExchangeSummaryResponse>> getPending(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                exchangeDiscoveryService
                        .getPendingRequestsForUser(
                                authentication.getName()
                        )
        );
    }

    @GetMapping("/accepted")
    public ResponseEntity<List<ExchangeSummaryResponse>> getAccepted(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                exchangeDiscoveryService
                        .getAcceptedExchangesForUser(
                                authentication.getName()
                        )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ExchangeRequestResponse>> getByStatus(
            @PathVariable ExchangeStatus status
    ) {
        return ResponseEntity.ok(
                exchangeDiscoveryService
                        .getAllByStatus(status)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<ExchangeRequestResponse>> search(
            Authentication authentication,
            @Valid ExchangeSearchRequest request
    ) {
        return ResponseEntity.ok(
                exchangeDiscoveryService.search(
                        authentication.getName(),
                        request
                )
        );
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ExchangeSummaryResponse>> searchPage(
            Authentication authentication,
            @Valid ExchangeSearchRequest request
    ) {
        return ResponseEntity.ok(
                exchangeDiscoveryService.searchPage(
                        authentication.getName(),
                        request
                )
        );
    }

    @GetMapping("/match/{matchResultId}")
    public ResponseEntity<ExchangeRequestResponse> getByMatchResult(
            Authentication authentication,
            @PathVariable Long matchResultId
    ) {
        return ResponseEntity.ok(
                exchangeDiscoveryService.getByMatchResult(
                        matchResultId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/match/{matchResultId}/exists")
    public ResponseEntity<Boolean> existsForMatchResult(
            @PathVariable Long matchResultId
    ) {
        return ResponseEntity.ok(
                exchangeDiscoveryService
                        .existsForMatchResult(matchResultId)
        );
    }
}