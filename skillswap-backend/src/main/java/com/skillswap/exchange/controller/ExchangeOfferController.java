package com.skillswap.exchange.controller;

import com.skillswap.exchange.dto.ExchangeOfferRequest;
import com.skillswap.exchange.dto.ExchangeOfferResponse;
import com.skillswap.exchange.entity.ExchangeResponseStatus;
import com.skillswap.exchange.service.ExchangeOfferService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchange/offers")
public class ExchangeOfferController {

    private final ExchangeOfferService exchangeOfferService;

    public ExchangeOfferController(
            ExchangeOfferService exchangeOfferService
    ) {
        this.exchangeOfferService =
                exchangeOfferService;
    }

    @PostMapping("/request/{exchangeRequestId:\\d+}")
    public ResponseEntity<ExchangeOfferResponse> create(
            Authentication authentication,
            @PathVariable Long exchangeRequestId,
            @Valid @RequestBody ExchangeOfferRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        exchangeOfferService.create(
                                exchangeRequestId,
                                authentication.getName(),
                                request
                        )
                );
    }

    @GetMapping("/{offerId:\\d+}")
    public ResponseEntity<ExchangeOfferResponse> getById(
            Authentication authentication,
            @PathVariable Long offerId
    ) {
        return ResponseEntity.ok(
                exchangeOfferService.getById(
                        offerId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/request/{exchangeRequestId:\\d+}")
    public ResponseEntity<List<ExchangeOfferResponse>> getForExchange(
            Authentication authentication,
            @PathVariable Long exchangeRequestId
    ) {
        return ResponseEntity.ok(
                exchangeOfferService.getForExchange(
                        exchangeRequestId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/request/{exchangeRequestId:\\d+}/page")
    public ResponseEntity<Page<ExchangeOfferResponse>> getForExchangePage(
            Authentication authentication,
            @PathVariable Long exchangeRequestId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                exchangeOfferService.getForExchange(
                        exchangeRequestId,
                        authentication.getName(),
                        pageable
                )
        );
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ExchangeOfferResponse>> getMyOffers(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                exchangeOfferService.getMyOffers(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/mine/page")
    public ResponseEntity<Page<ExchangeOfferResponse>> getMyOffersPage(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                exchangeOfferService.getMyOffers(
                        authentication.getName(),
                        pageable
                )
        );
    }

    @GetMapping("/mine/status/{status}")
    public ResponseEntity<List<ExchangeOfferResponse>> getMyOffersByStatus(
            Authentication authentication,
            @PathVariable ExchangeResponseStatus status
    ) {
        return ResponseEntity.ok(
                exchangeOfferService.getMyOffersByStatus(
                        authentication.getName(),
                        status
                )
        );
    }

    @PatchMapping("/{offerId:\\d+}/accept")
    public ResponseEntity<ExchangeOfferResponse> accept(
            Authentication authentication,
            @PathVariable Long offerId
    ) {
        return ResponseEntity.ok(
                exchangeOfferService.accept(
                        offerId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{offerId:\\d+}/reject")
    public ResponseEntity<ExchangeOfferResponse> reject(
            Authentication authentication,
            @PathVariable Long offerId
    ) {
        return ResponseEntity.ok(
                exchangeOfferService.reject(
                        offerId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{offerId:\\d+}/cancel")
    public ResponseEntity<ExchangeOfferResponse> cancel(
            Authentication authentication,
            @PathVariable Long offerId
    ) {
        return ResponseEntity.ok(
                exchangeOfferService.cancel(
                        offerId,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{offerId:\\d+}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable Long offerId
    ) {
        exchangeOfferService.delete(
                offerId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/request/{exchangeRequestId:\\d+}/count")
    public ResponseEntity<Long> countForExchange(
            Authentication authentication,
            @PathVariable Long exchangeRequestId
    ) {
        return ResponseEntity.ok(
                exchangeOfferService.countForExchange(
                        exchangeRequestId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/mine/count")
    public ResponseEntity<Long> countMyOffers(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                exchangeOfferService.countMyOffers(
                        authentication.getName()
                )
        );
    }
}