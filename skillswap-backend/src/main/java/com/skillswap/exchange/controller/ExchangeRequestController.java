package com.skillswap.exchange.controller;

import com.skillswap.exchange.dto.CreateExchangeRequest;
import com.skillswap.exchange.dto.ExchangeRequestResponse;
import com.skillswap.exchange.dto.ExchangeSummaryResponse;
import com.skillswap.exchange.dto.UpdateExchangeRequest;
import com.skillswap.exchange.entity.ExchangeStatus;
import com.skillswap.exchange.service.ExchangeRequestService;

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
@RequestMapping("/api/v1/exchange/requests")
public class ExchangeRequestController {

    private final ExchangeRequestService exchangeRequestService;

    public ExchangeRequestController(
            ExchangeRequestService exchangeRequestService
    ) {
        this.exchangeRequestService = exchangeRequestService;
    }

    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping
    public ResponseEntity<ExchangeRequestResponse> create(
            Authentication authentication,
            @Valid @RequestBody CreateExchangeRequest request
    ) {
        ExchangeRequestResponse response =
                exchangeRequestService.create(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{requestId:\\d+}")
    public ResponseEntity<ExchangeRequestResponse> getById(
            Authentication authentication,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.getById(
                        requestId,
                        authentication.getName()
                )
        );
    }

    // =========================================================
    // SENT
    // =========================================================

    @GetMapping("/sent")
    public ResponseEntity<List<ExchangeRequestResponse>> getSent(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.getMyRequests(
                        authentication.getName()
                )
        );
    }

    // =========================================================
    // RECEIVED
    // =========================================================

    @GetMapping("/received")
    public ResponseEntity<List<ExchangeRequestResponse>> getReceived(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.getIncomingRequests(
                        authentication.getName()
                )
        );
    }

    // =========================================================
    // PENDING SENT
    // =========================================================

    @GetMapping("/pending")
    public ResponseEntity<List<ExchangeRequestResponse>> getPending(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.getMyRequestsByStatus(
                        authentication.getName(),
                        ExchangeStatus.PENDING
                )
        );
    }

    // =========================================================
    // SENT PAGINATION
    // =========================================================

    @GetMapping("/sent/page")
    public ResponseEntity<Page<ExchangeSummaryResponse>> getSentPage(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                exchangeRequestService.getMyRequests(
                        authentication.getName(),
                        pageable
                )
        );
    }

    // =========================================================
    // RECEIVED PAGINATION
    // =========================================================

    @GetMapping("/received/page")
    public ResponseEntity<Page<ExchangeSummaryResponse>> getReceivedPage(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                exchangeRequestService.getIncomingRequests(
                        authentication.getName(),
                        pageable
                )
        );
    }

    // =========================================================
    // SENT BY STATUS
    // =========================================================

    @GetMapping("/sent/status/{status}")
    public ResponseEntity<List<ExchangeRequestResponse>> getSentByStatus(
            Authentication authentication,
            @PathVariable ExchangeStatus status
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.getMyRequestsByStatus(
                        authentication.getName(),
                        status
                )
        );
    }

    // =========================================================
    // RECEIVED BY STATUS
    // =========================================================

    @GetMapping("/received/status/{status}")
    public ResponseEntity<List<ExchangeRequestResponse>> getReceivedByStatus(
            Authentication authentication,
            @PathVariable ExchangeStatus status
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.getIncomingRequestsByStatus(
                        authentication.getName(),
                        status
                )
        );
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @PatchMapping("/{requestId:\\d+}")
    public ResponseEntity<ExchangeRequestResponse> update(
            Authentication authentication,
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateExchangeRequest request
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.update(
                        requestId,
                        request,
                        authentication.getName()
                )
        );
    }

    // =========================================================
    // ACCEPT
    // =========================================================

    @PatchMapping("/{requestId:\\d+}/accept")
    public ResponseEntity<ExchangeRequestResponse> accept(
            Authentication authentication,
            @PathVariable Long requestId,
            @RequestParam(required = false) String responseMessage
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.accept(
                        requestId,
                        authentication.getName(),
                        responseMessage
                )
        );
    }

    // =========================================================
    // REJECT
    // =========================================================

    @PatchMapping("/{requestId:\\d+}/reject")
    public ResponseEntity<ExchangeRequestResponse> reject(
            Authentication authentication,
            @PathVariable Long requestId,
            @RequestParam(required = false) String responseMessage
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.reject(
                        requestId,
                        authentication.getName(),
                        responseMessage
                )
        );
    }

    // =========================================================
    // CANCEL
    // =========================================================

    @PatchMapping("/{requestId:\\d+}/cancel")
    public ResponseEntity<ExchangeRequestResponse> cancel(
            Authentication authentication,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.cancel(
                        requestId,
                        authentication.getName()
                )
        );
    }

    // =========================================================
    // COMPLETE
    // =========================================================

    @PatchMapping("/{requestId:\\d+}/complete")
    public ResponseEntity<ExchangeRequestResponse> complete(
            Authentication authentication,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.complete(
                        requestId,
                        authentication.getName()
                )
        );
    }

    // =========================================================
    // ARCHIVE
    // =========================================================

    @PatchMapping("/{requestId:\\d+}/archive")
    public ResponseEntity<ExchangeRequestResponse> archive(
            Authentication authentication,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.archive(
                        requestId,
                        authentication.getName()
                )
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{requestId:\\d+}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable Long requestId
    ) {
        exchangeRequestService.delete(
                requestId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // COUNTS
    // =========================================================

    @GetMapping("/count/sent")
    public ResponseEntity<Long> countSent(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.countOutgoing(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/count/received")
    public ResponseEntity<Long> countReceived(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.countIncoming(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/count/sent/{status}")
    public ResponseEntity<Long> countSentByStatus(
            Authentication authentication,
            @PathVariable ExchangeStatus status
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.countOutgoingByStatus(
                        authentication.getName(),
                        status
                )
        );
    }

    @GetMapping("/count/received/{status}")
    public ResponseEntity<Long> countReceivedByStatus(
            Authentication authentication,
            @PathVariable ExchangeStatus status
    ) {
        return ResponseEntity.ok(
                exchangeRequestService.countIncomingByStatus(
                        authentication.getName(),
                        status
                )
        );
    }
}