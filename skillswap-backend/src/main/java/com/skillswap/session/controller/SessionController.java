package com.skillswap.session.controller;

import com.skillswap.session.dto.CreateSessionRequest;
import com.skillswap.session.dto.SessionPageResponse;
import com.skillswap.session.dto.SessionResponse;
import com.skillswap.session.dto.SessionSearchRequest;
import com.skillswap.session.dto.UpdateSessionRequest;
import com.skillswap.session.entity.SessionStatus;
import com.skillswap.session.service.SessionDiscoveryService;
import com.skillswap.session.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final SessionDiscoveryService sessionDiscoveryService;

    public SessionController(
            SessionService sessionService,
            SessionDiscoveryService sessionDiscoveryService
    ) {
        this.sessionService = sessionService;
        this.sessionDiscoveryService =
                sessionDiscoveryService;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> create(
            @Valid @RequestBody CreateSessionRequest request,
            Authentication authentication
    ) {

        SessionResponse response =
                sessionService.create(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{sessionId:\\d+}")
    public ResponseEntity<SessionResponse> getById(
            @PathVariable Long sessionId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.getById(
                        sessionId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<SessionResponse>> getMySessions(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.getMySessions(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/my/page")
    public ResponseEntity<SessionPageResponse> getMySessionsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.getMySessions(
                        authentication.getName(),
                        page,
                        size
                )
        );
    }

    @GetMapping("/my/upcoming")
    public ResponseEntity<List<SessionResponse>> getUpcoming(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.getUpcomingSessions(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/my/status/{status}")
    public ResponseEntity<List<SessionResponse>> getMyByStatus(
            @PathVariable SessionStatus status,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.getMySessionsByStatus(
                        authentication.getName(),
                        status
                )
        );
    }

    @GetMapping("/my/status/{status}/page")
    public ResponseEntity<SessionPageResponse> getMyByStatusPage(
            @PathVariable SessionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.getMySessionsByStatus(
                        authentication.getName(),
                        status,
                        page,
                        size
                )
        );
    }

    @GetMapping("/exchange/{exchangeRequestId}")
    public ResponseEntity<List<SessionResponse>> getByExchange(
            @PathVariable Long exchangeRequestId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.getSessionsByExchange(
                        exchangeRequestId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{sessionId:\\d+}")
    public ResponseEntity<SessionResponse> update(
            @PathVariable Long sessionId,
            @Valid @RequestBody UpdateSessionRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.update(
                        sessionId,
                        request,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{sessionId:\\d+}/confirm")
    public ResponseEntity<SessionResponse> confirm(
            @PathVariable Long sessionId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.confirm(
                        sessionId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{sessionId:\\d+}/start")
    public ResponseEntity<SessionResponse> start(
            @PathVariable Long sessionId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.start(
                        sessionId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{sessionId:\\d+}/complete")
    public ResponseEntity<SessionResponse> complete(
            @PathVariable Long sessionId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.complete(
                        sessionId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{sessionId:\\d+}/cancel")
    public ResponseEntity<SessionResponse> cancel(
            @PathVariable Long sessionId,
            @RequestParam(required = false) String reason,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.cancel(
                        sessionId,
                        authentication.getName(),
                        reason
                )
        );
    }

    @PatchMapping("/{sessionId:\\d+}/reject")
    public ResponseEntity<SessionResponse> reject(
            @PathVariable Long sessionId,
            @RequestParam(required = false) String reason,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.reject(
                        sessionId,
                        authentication.getName(),
                        reason
                )
        );
    }

    @PatchMapping("/{sessionId:\\d+}/no-show")
    public ResponseEntity<SessionResponse> noShow(
            @PathVariable Long sessionId,
            @RequestParam(required = false) String reason,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.markNoShow(
                        sessionId,
                        authentication.getName(),
                        reason
                )
        );
    }

    @PatchMapping("/{sessionId:\\d+}/join")
    public ResponseEntity<SessionResponse> join(
            @PathVariable Long sessionId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.markJoined(
                        sessionId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{sessionId:\\d+}/archive")
    public ResponseEntity<SessionResponse> archive(
            @PathVariable Long sessionId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.archive(
                        sessionId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<SessionPageResponse> search(
            SessionSearchRequest request
    ) {

        return ResponseEntity.ok(
                sessionDiscoveryService.search(request)
        );
    }

    @GetMapping("/search/my")
    public ResponseEntity<SessionPageResponse> searchMySessions(
            SessionSearchRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionDiscoveryService.searchMySessions(
                        request,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/search/skill")
    public ResponseEntity<List<SessionResponse>> searchBySkill(
            @RequestParam String skillName
    ) {

        return ResponseEntity.ok(
                sessionDiscoveryService
                        .searchBySkill(skillName)
                        .stream()
                        .map(SessionResponse::from)
                        .toList()
        );
    }

    @GetMapping("/search/status/{status}")
    public ResponseEntity<List<SessionResponse>> searchByStatus(
            @PathVariable SessionStatus status
    ) {

        return ResponseEntity.ok(
                sessionDiscoveryService
                        .getByStatus(status)
                        .stream()
                        .map(SessionResponse::from)
                        .toList()
        );
    }

    @GetMapping("/search/upcoming")
    public ResponseEntity<List<SessionResponse>> upcomingSessions() {

        return ResponseEntity.ok(
                sessionDiscoveryService
                        .getUpcomingPublicSessions()
                        .stream()
                        .map(SessionResponse::from)
                        .toList()
        );
    }

    @GetMapping("/search/running")
    public ResponseEntity<List<SessionResponse>> runningSessions() {

        return ResponseEntity.ok(
                sessionDiscoveryService
                        .getCurrentlyRunningSessions()
                        .stream()
                        .map(SessionResponse::from)
                        .toList()
        );
    }

    @PostMapping("/expire")
    public ResponseEntity<Void> expirePastSessions() {

        sessionService.expirePastSessions();

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my/count")
    public ResponseEntity<Long> countMySessions(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.countMySessions(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/my/count/{status}")
    public ResponseEntity<Long> countMySessionsByStatus(
            @PathVariable SessionStatus status,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                sessionService.countMySessionsByStatus(
                        authentication.getName(),
                        status
                )
        );
    }
}