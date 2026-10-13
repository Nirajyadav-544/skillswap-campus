package com.skillswap.exchange.service;

import com.skillswap.chat.dto.CreateConversationRequest;
import com.skillswap.chat.entity.ConversationType;
import com.skillswap.chat.service.ConversationService;
import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.common.exception.UnauthorizedException;
import com.skillswap.exchange.dto.CreateExchangeRequest;
import com.skillswap.exchange.dto.ExchangeRequestResponse;
import com.skillswap.exchange.dto.ExchangeSummaryResponse;
import com.skillswap.exchange.dto.UpdateExchangeRequest;
import com.skillswap.exchange.entity.ExchangeRequest;
import com.skillswap.exchange.entity.ExchangeStatus;
import com.skillswap.exchange.repository.ExchangeRequestRepository;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.matching.entity.MatchResult;
import com.skillswap.matching.repository.MatchResultRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ExchangeRequestService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final UserRepository userRepository;
    private final MatchResultRepository matchResultRepository;
    private final ConversationService conversationService;

    public ExchangeRequestService(
            ExchangeRequestRepository exchangeRequestRepository,
            UserRepository userRepository,
            MatchResultRepository matchResultRepository,
            ConversationService conversationService
    ) {
        this.exchangeRequestRepository = exchangeRequestRepository;
        this.userRepository = userRepository;
        this.matchResultRepository = matchResultRepository;
        this.conversationService = conversationService;
    }

    // =========================================================
    // CREATE
    // =========================================================

    public ExchangeRequestResponse create(
            CreateExchangeRequest request,
            String requesterEmail
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Exchange request is required"
            );
        }

        request.normalize();

        User requester = getUserByEmail(requesterEmail);

        User recipient = userRepository
                .findById(request.getRecipientUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Recipient user not found"
                        )
                );

        if (requester.getId().equals(recipient.getId())) {
            throw new BadRequestException(
                    "You cannot send an exchange request to yourself"
            );
        }

        boolean alreadyPending =
                exchangeRequestRepository
                        .existsByRequesterUserAndRecipientUserAndStatus(
                                requester,
                                recipient,
                                ExchangeStatus.PENDING
                        );

        if (alreadyPending) {
            throw new BadRequestException(
                    "A pending exchange request already exists for this user"
            );
        }

        MatchResult matchResult = null;

        if (request.getMatchResultId() != null) {
            matchResult = matchResultRepository
                    .findById(request.getMatchResultId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Match result not found"
                            )
                    );

            if (exchangeRequestRepository
                    .existsByMatchResultId(
                            request.getMatchResultId()
                    )) {

                throw new BadRequestException(
                        "An exchange request already exists for this match result"
                );
            }
        }

        ExchangeRequest exchangeRequest =
                new ExchangeRequest(
                        requester,
                        recipient,
                        request.getExchangeType()
                );

        exchangeRequest.setMatchResult(matchResult);

        exchangeRequest.setRequestMessage(
                request.getRequestMessage()
        );

        exchangeRequest.setRequestedSkillName(
                request.getRequestedSkillName()
        );

        exchangeRequest.setOfferedSkillName(
                request.getOfferedSkillName()
        );

        ExchangeRequest saved =
                exchangeRequestRepository.save(exchangeRequest);

        return ExchangeRequestResponse.from(saved);
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public ExchangeRequestResponse getById(
            Long requestId,
            String userEmail
    ) {
        ExchangeRequest exchangeRequest =
                getRequest(requestId);

        User user =
                getUserByEmail(userEmail);

        validateParticipant(
                exchangeRequest,
                user
        );

        return ExchangeRequestResponse.from(
                exchangeRequest
        );
    }

    // =========================================================
    // SENT REQUESTS
    // =========================================================

    @Transactional(readOnly = true)
    public List<ExchangeRequestResponse> getMyRequests(
            String userEmail
    ) {
        User user =
                getUserByEmail(userEmail);

        return exchangeRequestRepository
                .findByRequesterUser(user)
                .stream()
                .map(ExchangeRequestResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ExchangeSummaryResponse> getMyRequests(
            String userEmail,
            Pageable pageable
    ) {
        User user =
                getUserByEmail(userEmail);

        return exchangeRequestRepository
                .findByRequesterUser(
                        user,
                        pageable
                )
                .map(ExchangeSummaryResponse::from);
    }

    // =========================================================
    // RECEIVED REQUESTS
    // =========================================================

    @Transactional(readOnly = true)
    public List<ExchangeRequestResponse> getIncomingRequests(
            String userEmail
    ) {
        User user =
                getUserByEmail(userEmail);

        return exchangeRequestRepository
                .findByRecipientUser(user)
                .stream()
                .map(ExchangeRequestResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ExchangeSummaryResponse> getIncomingRequests(
            String userEmail,
            Pageable pageable
    ) {
        User user =
                getUserByEmail(userEmail);

        return exchangeRequestRepository
                .findByRecipientUser(
                        user,
                        pageable
                )
                .map(ExchangeSummaryResponse::from);
    }

    // =========================================================
    // SENT BY STATUS
    // =========================================================

    @Transactional(readOnly = true)
    public List<ExchangeRequestResponse> getMyRequestsByStatus(
            String userEmail,
            ExchangeStatus status
    ) {
        User user =
                getUserByEmail(userEmail);

        return exchangeRequestRepository
                .findByRequesterUserAndStatus(
                        user,
                        status
                )
                .stream()
                .map(ExchangeRequestResponse::from)
                .toList();
    }

    // =========================================================
    // RECEIVED BY STATUS
    // =========================================================

    @Transactional(readOnly = true)
    public List<ExchangeRequestResponse> getIncomingRequestsByStatus(
            String userEmail,
            ExchangeStatus status
    ) {
        User user =
                getUserByEmail(userEmail);

        return exchangeRequestRepository
                .findByRecipientUserAndStatus(
                        user,
                        status
                )
                .stream()
                .map(ExchangeRequestResponse::from)
                .toList();
    }

    // =========================================================
    // UPDATE
    // =========================================================

    public ExchangeRequestResponse update(
            Long requestId,
            UpdateExchangeRequest request,
            String userEmail
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Update request is required"
            );
        }

        request.normalize();

        ExchangeRequest exchangeRequest =
                getRequest(requestId);

        User requester =
                getUserByEmail(userEmail);

        if (exchangeRequest.getRequesterUser() == null
                || !exchangeRequest
                        .getRequesterUser()
                        .getId()
                        .equals(requester.getId())) {

            throw new UnauthorizedException(
                    "Only the requester can update this exchange request"
            );
        }

        if (!exchangeRequest.isPending()) {
            throw new BadRequestException(
                    "Only pending exchange requests can be updated"
            );
        }

        if (request.isEmpty()) {
            throw new BadRequestException(
                    "At least one field is required for update"
            );
        }

        if (request.getExchangeType() != null) {
            exchangeRequest.setExchangeType(
                    request.getExchangeType()
            );
        }

        if (request.getRequestMessage() != null) {
            exchangeRequest.setRequestMessage(
                    request.getRequestMessage()
            );
        }

        if (request.getRequestedSkillName() != null) {
            exchangeRequest.setRequestedSkillName(
                    request.getRequestedSkillName()
            );
        }

        if (request.getOfferedSkillName() != null) {
            exchangeRequest.setOfferedSkillName(
                    request.getOfferedSkillName()
            );
        }

        ExchangeRequest saved =
                exchangeRequestRepository.save(
                        exchangeRequest
                );

        return ExchangeRequestResponse.from(saved);
    }

    // =========================================================
    // ACCEPT
    // =========================================================

    public ExchangeRequestResponse accept(
            Long requestId,
            String userEmail,
            String responseMessage
    ) {
        ExchangeRequest exchangeRequest =
                getRequest(requestId);

        User recipient =
                getUserByEmail(userEmail);

        validateRecipient(
                exchangeRequest,
                recipient
        );

        ensurePending(exchangeRequest);

        // -----------------------------------------------------
        // 1. Mark exchange request as ACCEPTED
        // -----------------------------------------------------

        exchangeRequest.markAccepted();

        if (responseMessage != null
                && !responseMessage.isBlank()) {

            exchangeRequest.setResponseMessage(
                    responseMessage.trim()
            );
        }

        ExchangeRequest saved =
                exchangeRequestRepository.save(
                        exchangeRequest
                );

        // -----------------------------------------------------
        // 2. Create exchange conversation
        // -----------------------------------------------------

        createExchangeConversation(
                saved,
                recipient
        );

        // -----------------------------------------------------
        // 3. Return latest exchange state
        // -----------------------------------------------------

        return ExchangeRequestResponse.from(saved);
    }

    // =========================================================
    // CREATE EXCHANGE CONVERSATION
    // =========================================================

    private void createExchangeConversation(
            ExchangeRequest exchangeRequest,
            User recipient
    ) {
        if (exchangeRequest == null
                || exchangeRequest.getId() == null) {

            throw new BadRequestException(
                    "Accepted exchange request is invalid"
            );
        }

        if (exchangeRequest.getRequesterUser() == null
                || exchangeRequest.getRecipientUser() == null) {

            throw new BadRequestException(
                    "Exchange participants are missing"
            );
        }

        List<Long> participantUserIds =
                new ArrayList<>();

        participantUserIds.add(
                exchangeRequest
                        .getRequesterUser()
                        .getId()
        );

        participantUserIds.add(
                exchangeRequest
                        .getRecipientUser()
                        .getId()
        );

        CreateConversationRequest conversationRequest =
                new CreateConversationRequest();

        conversationRequest.setType(
                ConversationType.EXCHANGE
        );

        conversationRequest.setTitle(
                "Skill Exchange"
        );

        conversationRequest.setDescription(
                "Learning conversation for exchange request #"
                        + exchangeRequest.getId()
        );

        conversationRequest.setReferenceType(
                "EXCHANGE_REQUEST"
        );

        conversationRequest.setReferenceId(
                exchangeRequest.getId()
        );

        conversationRequest.setParticipantUserIds(
                participantUserIds
        );

        /*
         * ConversationService.create() automatically adds
         * the authenticated/current user as a participant.
         *
         * The accepting student is the authenticated user,
         * so recipient is passed here.
         */
        conversationService.create(
                conversationRequest,
                recipient.getEmail()
        );
    }

    // =========================================================
    // REJECT
    // =========================================================

    public ExchangeRequestResponse reject(
            Long requestId,
            String userEmail,
            String responseMessage
    ) {
        ExchangeRequest exchangeRequest =
                getRequest(requestId);

        User recipient =
                getUserByEmail(userEmail);

        validateRecipient(
                exchangeRequest,
                recipient
        );

        ensurePending(exchangeRequest);

        exchangeRequest.markRejected(
                responseMessage
        );

        ExchangeRequest saved =
                exchangeRequestRepository.save(
                        exchangeRequest
                );

        return ExchangeRequestResponse.from(saved);
    }

    // =========================================================
    // CANCEL
    // =========================================================

    public ExchangeRequestResponse cancel(
            Long requestId,
            String userEmail
    ) {
        ExchangeRequest exchangeRequest =
                getRequest(requestId);

        User requester =
                getUserByEmail(userEmail);

        validateRequester(
                exchangeRequest,
                requester
        );

        if (exchangeRequest.isClosed()) {
            throw new BadRequestException(
                    "This exchange request is already closed"
            );
        }

        exchangeRequest.markCancelled();

        ExchangeRequest saved =
                exchangeRequestRepository.save(
                        exchangeRequest
                );

        return ExchangeRequestResponse.from(saved);
    }

    // =========================================================
    // COMPLETE
    // =========================================================

    public ExchangeRequestResponse complete(
            Long requestId,
            String userEmail
    ) {
        ExchangeRequest exchangeRequest =
                getRequest(requestId);

        User user =
                getUserByEmail(userEmail);

        validateParticipant(
                exchangeRequest,
                user
        );

        if (!exchangeRequest.isAccepted()) {
            throw new BadRequestException(
                    "Only accepted exchange requests can be completed"
            );
        }

        exchangeRequest.markCompleted();

        ExchangeRequest saved =
                exchangeRequestRepository.save(
                        exchangeRequest
                );

        return ExchangeRequestResponse.from(saved);
    }

    // =========================================================
    // ARCHIVE
    // =========================================================

    public ExchangeRequestResponse archive(
            Long requestId,
            String userEmail
    ) {
        ExchangeRequest exchangeRequest =
                getRequest(requestId);

        User user =
                getUserByEmail(userEmail);

        validateParticipant(
                exchangeRequest,
                user
        );

        if (!exchangeRequest.isClosed()) {
            throw new BadRequestException(
                    "Only closed exchange requests can be archived"
            );
        }

        exchangeRequest.archive();

        ExchangeRequest saved =
                exchangeRequestRepository.save(
                        exchangeRequest
                );

        return ExchangeRequestResponse.from(saved);
    }

    // =========================================================
    // DELETE
    // =========================================================

    public void delete(
            Long requestId,
            String userEmail
    ) {
        ExchangeRequest exchangeRequest =
                getRequest(requestId);

        User requester =
                getUserByEmail(userEmail);

        validateRequester(
                exchangeRequest,
                requester
        );

        if (!exchangeRequest.isClosed()) {
            throw new BadRequestException(
                    "Only closed exchange requests can be deleted"
            );
        }

        exchangeRequestRepository.delete(
                exchangeRequest
        );
    }
// =========================================================
// COUNTS
// =========================================================

@Transactional(readOnly = true)
public long countOutgoing(String userEmail) {
    User user = getUserByEmail(userEmail);

    return exchangeRequestRepository
            .countByRequesterUser(user);
}

@Transactional(readOnly = true)
public long countIncoming(String userEmail) {
    User user = getUserByEmail(userEmail);

    return exchangeRequestRepository
            .countByRecipientUser(user);
}

@Transactional(readOnly = true)
public long countOutgoingByStatus(
        String userEmail,
        ExchangeStatus status
) {
    User user = getUserByEmail(userEmail);

    if (status == null) {
        throw new BadRequestException(
                "Exchange status is required"
        );
    }

    return exchangeRequestRepository
            .countByRequesterUserAndStatus(
                    user,
                    status
            );
}

@Transactional(readOnly = true)
public long countIncomingByStatus(
        String userEmail,
        ExchangeStatus status
) {
    User user = getUserByEmail(userEmail);

    if (status == null) {
        throw new BadRequestException(
                "Exchange status is required"
        );
    }

    return exchangeRequestRepository
            .countByRecipientUserAndStatus(
                    user,
                    status
            );
}

    // =========================================================
    // HELPERS
    // =========================================================

    private ExchangeRequest getRequest(
            Long requestId
    ) {
        if (requestId == null || requestId <= 0) {
            throw new BadRequestException(
                    "Exchange request ID must be valid"
            );
        }

        return exchangeRequestRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exchange request not found: "
                                        + requestId
                        )
                );
    }

    private User getUserByEmail(
            String email
    ) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException(
                    "User email is required"
            );
        }

        return userRepository
                .findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + email
                        )
                );
    }

    private void validateParticipant(
            ExchangeRequest request,
            User user
    ) {
        if (request.getRequesterUser() == null
                || request.getRecipientUser() == null) {

            throw new BadRequestException(
                    "Exchange participants are missing"
            );
        }

        boolean requester =
                request
                        .getRequesterUser()
                        .getId()
                        .equals(user.getId());

        boolean recipient =
                request
                        .getRecipientUser()
                        .getId()
                        .equals(user.getId());

        if (!requester && !recipient) {
            throw new BadRequestException(
                    "You are not a participant in this exchange"
            );
        }
    }

    private void validateRecipient(
            ExchangeRequest request,
            User user
    ) {
        if (request.getRecipientUser() == null
                || request.getRecipientUser().getId() == null) {

            throw new BadRequestException(
                    "Exchange recipient is missing"
            );
        }

        if (!request
                .getRecipientUser()
                .getId()
                .equals(user.getId())) {

            throw new UnauthorizedException(
                    "Only the recipient can respond to this exchange request"
            );
        }
    }

    private void validateRequester(
            ExchangeRequest request,
            User user
    ) {
        if (request.getRequesterUser() == null
                || request.getRequesterUser().getId() == null) {

            throw new BadRequestException(
                    "Exchange requester is missing"
            );
        }

        if (!request
                .getRequesterUser()
                .getId()
                .equals(user.getId())) {

            throw new UnauthorizedException(
                    "Only the requester can perform this action"
            );
        }
    }

    private void ensurePending(
            ExchangeRequest request
    ) {
        if (!request.isPending()) {
            throw new BadRequestException(
                    "Only pending exchange requests can be accepted or rejected"
            );
        }

        if (request.isExpired()) {
            throw new BadRequestException(
                    "This exchange request has expired"
            );
        }
    }
}