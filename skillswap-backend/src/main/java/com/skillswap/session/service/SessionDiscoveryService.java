package com.skillswap.session.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.session.dto.SessionPageResponse;
import com.skillswap.session.dto.SessionSearchRequest;
import com.skillswap.session.entity.LearningSession;
import com.skillswap.session.entity.SessionStatus;
import com.skillswap.session.repository.LearningSessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SessionDiscoveryService {

    private final LearningSessionRepository sessionRepository;
    private final UserRepository userRepository;

    public SessionDiscoveryService(
            LearningSessionRepository sessionRepository,
            UserRepository userRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    public SessionPageResponse search(
            SessionSearchRequest request
    ) {

        if (request == null) {
            request = new SessionSearchRequest();
        }

        request.normalize();

        Pageable pageable =
                buildPageable(request);

        Page<LearningSession> result;

        if (request.hasKeywordFilter()) {

            result =
                    sessionRepository.searchByKeyword(
                            request.getKeyword(),
                            pageable
                    );

        } else if (request.hasSkillFilter()) {

            result =
                    sessionRepository
                            .findBySkillNameIgnoreCase(
                                    request.getSkillName(),
                                    pageable
                            );

        } else if (request.hasStatusFilter()) {

            result =
                    sessionRepository.findByStatus(
                            request.getStatus(),
                            pageable
                    );

        } else if (request.hasSessionTypeFilter()) {

            result =
                    sessionRepository.findBySessionType(
                            request.getSessionType(),
                            pageable
                    );

        } else if (request.hasExchangeRequestFilter()) {

            result =
                    sessionRepository.findByExchangeRequestId(
                            request.getExchangeRequestId(),
                            pageable
                    );

        } else if (request.hasTeacherFilter()) {

            result =
                    sessionRepository.findByTeacherUserId(
                            request.getTeacherUserId(),
                            pageable
                    );

        } else if (request.hasLearnerFilter()) {

            result =
                    sessionRepository.findByLearnerUserId(
                            request.getLearnerUserId(),
                            pageable
                    );

        } else if (request.hasStartDateFilter()) {

            result =
                    findByStartRange(
                            request,
                            pageable
                    );

        } else {

            result =
                    sessionRepository.findAll(
                            pageable
                    );
        }

        return SessionPageResponse.from(result);
    }

    public SessionPageResponse searchMySessions(
            SessionSearchRequest request,
            String userEmail
    ) {

        if (request == null) {
            request = new SessionSearchRequest();
        }

        request.normalize();

        User user =
                getUser(userEmail);

        Pageable pageable =
                buildPageable(request);

        Page<LearningSession> result;

        if (request.hasKeywordFilter()) {

            result =
                    sessionRepository.searchMySessionsByKeyword(
                            user.getId(),
                            request.getKeyword(),
                            pageable
                    );

        } else if (request.hasStatusFilter()) {

            result =
                    sessionRepository.findMySessionsByStatus(
                            user.getId(),
                            request.getStatus(),
                            pageable
                    );

        } else if (request.hasStartDateFilter()) {

            LocalDateTime from =
                    request.getStartFrom() != null
                            ? request.getStartFrom()
                            : LocalDateTime.now().minusYears(10);

            LocalDateTime to =
                    request.getStartTo() != null
                            ? request.getStartTo()
                            : LocalDateTime.now().plusYears(10);

            result =
                    sessionRepository.findMySessionsBetween(
                            user.getId(),
                            from,
                            to,
                            pageable
                    );

        } else {

            result =
                    sessionRepository.findMySessions(
                            user.getId(),
                            pageable
                    );
        }

        return SessionPageResponse.from(result);
    }

    public List<LearningSession> getUpcomingPublicSessions() {

        return sessionRepository.findUpcomingSessions(
                List.of(
                        SessionStatus.SCHEDULED,
                        SessionStatus.CONFIRMED
                ),
                LocalDateTime.now()
        );
    }

    public List<LearningSession> getCurrentlyRunningSessions() {

        return sessionRepository.findSessionsCurrentlyRunning(
                List.of(
                        SessionStatus.STARTED
                ),
                LocalDateTime.now()
        );
    }

    public List<LearningSession> searchBySkill(
            String skillName
    ) {

        if (skillName == null
                || skillName.isBlank()) {

            throw new BadRequestException(
                    "Skill name is required"
            );
        }

        return sessionRepository
                .findBySkillNameIgnoreCase(
                        skillName.trim()
                );
    }

    public List<LearningSession> getByStatus(
            SessionStatus status
    ) {

        if (status == null) {
            throw new BadRequestException(
                    "Session status is required"
            );
        }

        return sessionRepository
                .findByStatus(status);
    }

    private Page<LearningSession> findByStartRange(
            SessionSearchRequest request,
            Pageable pageable
    ) {

        LocalDateTime from =
                request.getStartFrom();

        LocalDateTime to =
                request.getStartTo();

        if (from == null) {
            from =
                    LocalDateTime.now()
                            .minusYears(10);
        }

        if (to == null) {
            to =
                    LocalDateTime.now()
                            .plusYears(10);
        }

        return sessionRepository
                .findByScheduledStartBetween(
                        from,
                        to,
                        pageable
                );
    }

    private Pageable buildPageable(
            SessionSearchRequest request
    ) {

        String sortProperty =
                normalizeSortProperty(
                        request.getSortBy()
                );

        Sort.Direction direction =
                request.isDescending()
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        return PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(
                        direction,
                        sortProperty
                )
        );
    }

    private String normalizeSortProperty(
            String sortBy
    ) {

        if (sortBy == null
                || sortBy.isBlank()) {

            return "scheduledStart";
        }

        return switch (sortBy) {

            case "id" ->
                    "id";

            case "title" ->
                    "title";

            case "skillName" ->
                    "skillName";

            case "createdAt" ->
                    "createdAt";

            case "updatedAt" ->
                    "updatedAt";

            case "scheduledStart" ->
                    "scheduledStart";

            case "scheduledEnd" ->
                    "scheduledEnd";

            case "status" ->
                    "status";

            default ->
                    "scheduledStart";
        };
    }

    private User getUser(
            String email
    ) {

        if (email == null
                || email.isBlank()) {

            throw new BadRequestException(
                    "User email is required"
            );
        }

        return userRepository
                .findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found"
                        )
                );
    }
}