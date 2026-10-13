package com.skillswap.exchange.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.exchange.dto.ExchangeRequestResponse;
import com.skillswap.exchange.dto.ExchangeSearchRequest;
import com.skillswap.exchange.dto.ExchangeSummaryResponse;
import com.skillswap.exchange.entity.ExchangeRequest;
import com.skillswap.exchange.entity.ExchangeStatus;
import com.skillswap.exchange.entity.ExchangeType;
import com.skillswap.exchange.repository.ExchangeRequestRepository;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ExchangeDiscoveryService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final UserRepository userRepository;

    public ExchangeDiscoveryService(
            ExchangeRequestRepository exchangeRequestRepository,
            UserRepository userRepository
    ) {
        this.exchangeRequestRepository =
                exchangeRequestRepository;
        this.userRepository = userRepository;
    }

    public List<ExchangeRequestResponse> getAllByStatus(
            ExchangeStatus status
    ) {
        if (status == null) {
            throw new BadRequestException(
                    "Exchange status is required"
            );
        }

        return exchangeRequestRepository
                .findByStatus(status)
                .stream()
                .map(ExchangeRequestResponse::from)
                .toList();
    }

    public Page<ExchangeSummaryResponse> getAllByStatus(
            ExchangeStatus status,
            Pageable pageable
    ) {
        if (status == null) {
            throw new BadRequestException(
                    "Exchange status is required"
            );
        }

        return exchangeRequestRepository
                .findByStatus(status, pageable)
                .map(ExchangeSummaryResponse::from);
    }

    public ExchangeRequestResponse getByMatchResult(
            Long matchResultId,
            String userEmail
    ) {
        if (matchResultId == null) {
            throw new BadRequestException(
                    "Match result ID is required"
            );
        }

        User user = getUserByEmail(userEmail);

        ExchangeRequest request =
                exchangeRequestRepository
                        .findByMatchResultId(matchResultId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Exchange request not found for match result: "
                                                + matchResultId
                                )
                        );

        boolean requester =
                request.getRequesterUser()
                        .getId()
                        .equals(user.getId());

        boolean recipient =
                request.getRecipientUser()
                        .getId()
                        .equals(user.getId());

        if (!requester && !recipient) {
            throw new BadRequestException(
                    "You are not a participant in this exchange"
            );
        }

        return ExchangeRequestResponse.from(request);
    }

    public boolean existsForMatchResult(
            Long matchResultId
    ) {
        if (matchResultId == null) {
            return false;
        }

        return exchangeRequestRepository
                .existsByMatchResultId(matchResultId);
    }

    public List<ExchangeRequestResponse> search(
            String userEmail,
            ExchangeSearchRequest searchRequest
    ) {
        if (searchRequest == null) {
            searchRequest = new ExchangeSearchRequest();
        }

        searchRequest.normalize();

        User user = getUserByEmail(userEmail);

        List<ExchangeRequest> results =
                collectSearchResults(
                        user,
                        searchRequest
                );

        return results.stream()
                .map(ExchangeRequestResponse::from)
                .toList();
    }

    public Page<ExchangeSummaryResponse> searchPage(
            String userEmail,
            ExchangeSearchRequest searchRequest
    ) {
        if (searchRequest == null) {
            searchRequest = new ExchangeSearchRequest();
        }

        searchRequest.normalize();

        User user = getUserByEmail(userEmail);

        Pageable pageable = buildPageable(searchRequest);

        if (searchRequest.isIncomingOnly()) {
            return searchIncoming(
                    user,
                    searchRequest,
                    pageable
            );
        }

        if (searchRequest.isOutgoingOnly()) {
            return searchOutgoing(
                    user,
                    searchRequest,
                    pageable
            );
        }

        /*
         * When direction is not explicitly selected,
         * combine incoming and outgoing records.
         *
         * Repository-level filtering remains intentionally
         * simple here because the current Phase 5 repository
         * exposes separate requester/recipient queries.
         */
        List<ExchangeRequest> combined =
                new ArrayList<>();

        combined.addAll(
                exchangeRequestRepository
                        .findByRequesterUser(user)
        );

        combined.addAll(
                exchangeRequestRepository
                        .findByRecipientUser(user)
        );

        List<ExchangeRequest> filtered =
                filterResults(
                        combined,
                        searchRequest
                );

        int fromIndex =
                Math.min(
                        searchRequest.getPage()
                                * searchRequest.getSize(),
                        filtered.size()
                );

        int toIndex =
                Math.min(
                        fromIndex
                                + searchRequest.getSize(),
                        filtered.size()
                );

        List<ExchangeSummaryResponse> content =
                filtered.subList(fromIndex, toIndex)
                        .stream()
                        .map(ExchangeSummaryResponse::from)
                        .toList();

        return new org.springframework.data.domain.PageImpl<>(
                content,
                pageable,
                filtered.size()
        );
    }

    public long countByStatus(
            ExchangeStatus status
    ) {
        if (status == null) {
            throw new BadRequestException(
                    "Exchange status is required"
            );
        }

        return exchangeRequestRepository
                .countByStatus(status);
    }

    public long countByType(
            ExchangeType exchangeType
    ) {
        if (exchangeType == null) {
            throw new BadRequestException(
                    "Exchange type is required"
            );
        }

        return exchangeRequestRepository
                .findAll()
                .stream()
                .filter(request ->
                        exchangeType
                                .equals(request.getExchangeType()))
                .count();
    }

    public List<ExchangeSummaryResponse> getPendingRequestsForUser(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return exchangeRequestRepository
                .findByRecipientUserAndStatus(
                        user,
                        ExchangeStatus.PENDING
                )
                .stream()
                .map(ExchangeSummaryResponse::from)
                .toList();
    }

    public List<ExchangeSummaryResponse> getAcceptedExchangesForUser(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        List<ExchangeRequest> results =
                new ArrayList<>();

        results.addAll(
                exchangeRequestRepository
                        .findByRequesterUserAndStatus(
                                user,
                                ExchangeStatus.ACCEPTED
                        )
        );

        results.addAll(
                exchangeRequestRepository
                        .findByRecipientUserAndStatus(
                                user,
                                ExchangeStatus.ACCEPTED
                        )
        );

        return results.stream()
                .map(ExchangeSummaryResponse::from)
                .toList();
    }

    private List<ExchangeRequest> collectSearchResults(
            User user,
            ExchangeSearchRequest searchRequest
    ) {
        List<ExchangeRequest> results;

        if (searchRequest.isIncomingOnly()) {
            results =
                    exchangeRequestRepository
                            .findByRecipientUser(user);
        } else if (searchRequest.isOutgoingOnly()) {
            results =
                    exchangeRequestRepository
                            .findByRequesterUser(user);
        } else {
            results = new ArrayList<>();

            results.addAll(
                    exchangeRequestRepository
                            .findByRequesterUser(user)
            );

            results.addAll(
                    exchangeRequestRepository
                            .findByRecipientUser(user)
            );
        }

        return filterResults(results, searchRequest);
    }

    private List<ExchangeRequest> filterResults(
            List<ExchangeRequest> results,
            ExchangeSearchRequest searchRequest
    ) {
        return results.stream()
                .filter(request -> {
                    if (searchRequest.getStatus() != null
                            && request.getStatus()
                            != searchRequest.getStatus()) {
                        return false;
                    }

                    if (searchRequest.getExchangeType() != null
                            && request.getExchangeType()
                            != searchRequest.getExchangeType()) {
                        return false;
                    }

                    if (searchRequest.hasSkillFilter()) {
                        String skill =
                                searchRequest.getSkillName()
                                        .toLowerCase();

                        String requested =
                                request.getRequestedSkillName();

                        String offered =
                                request.getOfferedSkillName();

                        boolean matchesRequested =
                                requested != null
                                        && requested
                                        .toLowerCase()
                                        .contains(skill);

                        boolean matchesOffered =
                                offered != null
                                        && offered
                                        .toLowerCase()
                                        .contains(skill);

                        if (!matchesRequested
                                && !matchesOffered) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();
    }

    private Page<ExchangeSummaryResponse> searchIncoming(
            User user,
            ExchangeSearchRequest searchRequest,
            Pageable pageable
    ) {
        Page<ExchangeRequest> page;

        if (searchRequest.getStatus() != null) {
            page =
                    exchangeRequestRepository
                            .findByRecipientUserAndStatus(
                                    user,
                                    searchRequest.getStatus(),
                                    pageable
                            );
        } else {
            page =
                    exchangeRequestRepository
                            .findByRecipientUser(
                                    user,
                                    pageable
                            );
        }

        return page
                .map(ExchangeSummaryResponse::from);
    }

    private Page<ExchangeSummaryResponse> searchOutgoing(
            User user,
            ExchangeSearchRequest searchRequest,
            Pageable pageable
    ) {
        Page<ExchangeRequest> page =
                exchangeRequestRepository
                        .findByRequesterUser(
                                user,
                                pageable
                        );

        if (searchRequest.getStatus() == null
                && searchRequest.getExchangeType() == null
                && !searchRequest.hasSkillFilter()) {

            return page.map(
                    ExchangeSummaryResponse::from
            );
        }

        List<ExchangeRequest> filtered =
                filterResults(
                        page.getContent(),
                        searchRequest
                );

        return new org.springframework.data.domain.PageImpl<>(
                filtered.stream()
                        .map(ExchangeSummaryResponse::from)
                        .toList(),
                pageable,
                filtered.size()
        );
    }

    private Pageable buildPageable(
            ExchangeSearchRequest request
    ) {
        Sort.Direction direction =
                "ASC".equalsIgnoreCase(
                        request.getSortDirection()
                )
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        String sortProperty =
                normalizeSortProperty(
                        request.getSortBy()
                );

        return PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(direction, sortProperty)
        );
    }

    private String normalizeSortProperty(
            String sortBy
    ) {
        if (sortBy == null || sortBy.isBlank()) {
            return "createdAt";
        }

        return switch (sortBy.trim()) {
            case "id" -> "id";
            case "createdAt" -> "createdAt";
            case "updatedAt" -> "updatedAt";
            case "expiresAt" -> "expiresAt";
            case "status" -> "status";
            case "exchangeType" -> "exchangeType";
            default -> "createdAt";
        };
    }

    private User getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException(
                    "Authenticated user email is required"
            );
        }

        return userRepository
                .findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }
}