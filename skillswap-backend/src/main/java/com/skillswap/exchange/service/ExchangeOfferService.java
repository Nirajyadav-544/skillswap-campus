package com.skillswap.exchange.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.exchange.dto.ExchangeOfferRequest;
import com.skillswap.exchange.dto.ExchangeOfferResponse;
import com.skillswap.exchange.entity.ExchangeOffer;
import com.skillswap.exchange.entity.ExchangeRequest;
import com.skillswap.exchange.entity.ExchangeResponseStatus;
import com.skillswap.exchange.repository.ExchangeOfferRepository;
import com.skillswap.exchange.repository.ExchangeRequestRepository;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExchangeOfferService {

    private final ExchangeOfferRepository exchangeOfferRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final UserRepository userRepository;

    public ExchangeOfferService(
            ExchangeOfferRepository exchangeOfferRepository,
            ExchangeRequestRepository exchangeRequestRepository,
            UserRepository userRepository
    ) {
        this.exchangeOfferRepository = exchangeOfferRepository;
        this.exchangeRequestRepository =
                exchangeRequestRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ExchangeOfferResponse getById(
            Long offerId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        ExchangeOffer offer = getOffer(offerId);

        validateParticipant(
                offer.getExchangeRequest(),
                user
        );

        return ExchangeOfferResponse.from(offer);
    }

    @Transactional(readOnly = true)
    public List<ExchangeOfferResponse> getForExchange(
            Long exchangeRequestId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        ExchangeRequest request =
                getExchangeRequest(exchangeRequestId);

        validateParticipant(request, user);

        return exchangeOfferRepository
                .findByExchangeRequestId(exchangeRequestId)
                .stream()
                .map(ExchangeOfferResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ExchangeOfferResponse> getForExchange(
            Long exchangeRequestId,
            String userEmail,
            Pageable pageable
    ) {
        User user = getUserByEmail(userEmail);

        ExchangeRequest request =
                getExchangeRequest(exchangeRequestId);

        validateParticipant(request, user);

        return exchangeOfferRepository
                .findByExchangeRequestId(
                        exchangeRequestId,
                        pageable
                )
                .map(ExchangeOfferResponse::from);
    }

    @Transactional(readOnly = true)
    public List<ExchangeOfferResponse> getMyOffers(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return exchangeOfferRepository
                .findByUser(user)
                .stream()
                .map(ExchangeOfferResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ExchangeOfferResponse> getMyOffers(
            String userEmail,
            Pageable pageable
    ) {
        User user = getUserByEmail(userEmail);

        return exchangeOfferRepository
                .findByUser(user, pageable)
                .map(ExchangeOfferResponse::from);
    }

    @Transactional(readOnly = true)
    public List<ExchangeOfferResponse> getMyOffersByStatus(
            String userEmail,
            ExchangeResponseStatus status
    ) {
        if (status == null) {
            throw new BadRequestException(
                    "Offer response status is required"
            );
        }

        User user = getUserByEmail(userEmail);

        return exchangeOfferRepository
                .findByUserAndResponseStatus(
                        user,
                        status
                )
                .stream()
                .map(ExchangeOfferResponse::from)
                .toList();
    }

    public ExchangeOfferResponse create(
            Long exchangeRequestId,
            String userEmail,
            ExchangeOfferRequest request
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Exchange offer body is required"
            );
        }

        request.normalize();

        if (request.getSkillName() == null
                || request.getSkillName().isBlank()) {

            throw new BadRequestException(
                    "Skill name is required"
            );
        }

        User user = getUserByEmail(userEmail);

        ExchangeRequest exchangeRequest =
                getExchangeRequest(exchangeRequestId);

        validateParticipant(exchangeRequest, user);

        if (exchangeRequest.isClosed()
                || exchangeRequest.isRejected()
                || exchangeRequest.isCancelled()
                || exchangeRequest.isExpired()) {

            throw new BadRequestException(
                    "Offers cannot be added to this exchange request"
            );
        }

        if (exchangeOfferRepository
                .existsByExchangeRequestIdAndUserId(
                        exchangeRequestId,
                        user.getId()
                )) {

            throw new BadRequestException(
                    "You already have an offer for this exchange"
            );
        }

        ExchangeOffer offer =
                new ExchangeOffer(
                        exchangeRequest,
                        user,
                        request.getSkillName()
                );

        offer.setSkillId(request.getSkillId());

        offer.setOfferDescription(
                request.getOfferDescription()
        );

        offer.setResponseStatus(
                ExchangeResponseStatus.PENDING
        );

        ExchangeOffer saved =
                exchangeOfferRepository.save(offer);

        return ExchangeOfferResponse.from(saved);
    }

    public ExchangeOfferResponse accept(
            Long offerId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        ExchangeOffer offer = getOffer(offerId);

        ExchangeRequest request =
                offer.getExchangeRequest();

        validateParticipant(request, user);

        validateCanRespond(request, user, offer);

        if (!offer.isPending()) {
            throw new BadRequestException(
                    "Only pending offers can be accepted"
            );
        }

        offer.markAccepted();

        ExchangeOffer saved =
                exchangeOfferRepository.save(offer);

        return ExchangeOfferResponse.from(saved);
    }

    public ExchangeOfferResponse reject(
            Long offerId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        ExchangeOffer offer = getOffer(offerId);

        ExchangeRequest request =
                offer.getExchangeRequest();

        validateParticipant(request, user);

        validateCanRespond(request, user, offer);

        if (!offer.isPending()) {
            throw new BadRequestException(
                    "Only pending offers can be rejected"
            );
        }

        offer.markRejected();

        ExchangeOffer saved =
                exchangeOfferRepository.save(offer);

        return ExchangeOfferResponse.from(saved);
    }

    public ExchangeOfferResponse cancel(
            Long offerId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        ExchangeOffer offer = getOffer(offerId);

        if (!offer.getUser()
                .getId()
                .equals(user.getId())) {

            throw new BadRequestException(
                    "Only the offer creator can cancel this offer"
            );
        }

        if (!offer.isPending()) {
            throw new BadRequestException(
                    "Only pending offers can be cancelled"
            );
        }

        offer.markCancelled();

        ExchangeOffer saved =
                exchangeOfferRepository.save(offer);

        return ExchangeOfferResponse.from(saved);
    }

    public void delete(
            Long offerId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        ExchangeOffer offer = getOffer(offerId);

        if (!offer.getUser()
                .getId()
                .equals(user.getId())) {

            throw new BadRequestException(
                    "Only the offer creator can delete this offer"
            );
        }

        if (offer.isAccepted()) {
            throw new BadRequestException(
                    "Accepted offers cannot be deleted"
            );
        }

        exchangeOfferRepository.delete(offer);
    }

    public long countForExchange(
            Long exchangeRequestId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        ExchangeRequest request =
                getExchangeRequest(exchangeRequestId);

        validateParticipant(request, user);

        return exchangeOfferRepository
                .countByExchangeRequest(request);
    }

    public long countMyOffers(
            String userEmail
    ) {
        return exchangeOfferRepository.countByUser(
                getUserByEmail(userEmail)
        );
    }

    private ExchangeOffer getOffer(Long offerId) {
        if (offerId == null) {
            throw new BadRequestException(
                    "Offer ID is required"
            );
        }

        return exchangeOfferRepository
                .findById(offerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exchange offer not found: "
                                        + offerId
                        )
                );
    }

    private ExchangeRequest getExchangeRequest(
            Long exchangeRequestId
    ) {
        if (exchangeRequestId == null) {
            throw new BadRequestException(
                    "Exchange request ID is required"
            );
        }

        return exchangeRequestRepository
                .findById(exchangeRequestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exchange request not found: "
                                        + exchangeRequestId
                        )
                );
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

    private void validateParticipant(
            ExchangeRequest request,
            User user
    ) {
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
    }

    private void validateCanRespond(
            ExchangeRequest request,
            User user,
            ExchangeOffer offer
    ) {
        /*
         * The creator of an offer should not approve/reject
         * their own offer. The other participant responds.
         */
        if (offer.getUser()
                .getId()
                .equals(user.getId())) {

            throw new BadRequestException(
                    "You cannot respond to your own offer"
            );
        }

        if (request.isClosed()
                || request.isRejected()
                || request.isCancelled()
                || request.isExpired()) {

            throw new BadRequestException(
                    "This exchange request is no longer active"
            );
        }
    }
}