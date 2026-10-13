package com.skillswap.chat.security;

import com.skillswap.chat.entity.ParticipantStatus;
import com.skillswap.chat.repository.ConversationParticipantRepository;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.identity.security.JwtService;
import com.skillswap.identity.security.UserDetailsServiceImpl;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor
        implements ChannelInterceptor {

    private static final String SESSION_USER =
            "SKILLSWAP_WEBSOCKET_USER";

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final ConversationParticipantRepository participantRepository;

    public WebSocketAuthInterceptor(
            JwtService jwtService,
            UserDetailsServiceImpl userDetailsService,
            UserRepository userRepository,
            ConversationParticipantRepository participantRepository
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(message);

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {

            authenticateConnection(accessor);

        } else if (StompCommand.SUBSCRIBE.equals(command)) {

            restoreAuthentication(accessor);

            validateConversationSubscription(accessor);
        }

        return message;
    }

    private void authenticateConnection(
            StompHeaderAccessor accessor
    ) {

        String authorizationHeader =
                accessor.getFirstNativeHeader(
                        "Authorization"
                );

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            throw new IllegalArgumentException(
                    "WebSocket authentication token is required"
            );
        }

        String token =
                authorizationHeader
                        .substring(7)
                        .trim();

        if (token.isBlank()) {
            throw new IllegalArgumentException(
                    "WebSocket authentication token is empty"
            );
        }

        if (!jwtService.isAccessToken(token)) {
            throw new IllegalArgumentException(
                    "Invalid WebSocket access token"
            );
        }

        String username =
                jwtService.tryExtractUsername(token);

        if (username == null
                || username.isBlank()) {

            throw new IllegalArgumentException(
                    "WebSocket token does not contain a valid username"
            );
        }

        UserDetails userDetails =
                userDetailsService
                        .loadUserByUsername(username);

        if (!userDetails.isEnabled()) {
            throw new IllegalArgumentException(
                    "WebSocket user account is disabled"
            );
        }

        if (!jwtService.isAccessTokenValid(
                token,
                userDetails
        )) {

            throw new IllegalArgumentException(
                    "WebSocket access token validation failed"
            );
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        accessor.setUser(authentication);

        if (accessor.getSessionAttributes() != null) {

            accessor.getSessionAttributes().put(
                    SESSION_USER,
                    authentication
            );
        }
    }

    private void restoreAuthentication(
            StompHeaderAccessor accessor
    ) {

        if (accessor.getUser() != null) {
            return;
        }

        if (accessor.getSessionAttributes() == null) {
            throw new IllegalArgumentException(
                    "WebSocket session is not authenticated"
            );
        }

        Object storedUser =
                accessor.getSessionAttributes()
                        .get(SESSION_USER);

        if (!(storedUser instanceof Authentication authentication)) {
            throw new IllegalArgumentException(
                    "WebSocket user authentication was not found"
            );
        }

        accessor.setUser(authentication);
    }

    private void validateConversationSubscription(
            StompHeaderAccessor accessor
    ) {

        if (accessor.getUser() == null) {
            throw new IllegalArgumentException(
                    "WebSocket user is not authenticated"
            );
        }

        String destination =
                accessor.getDestination();

        if (destination == null
                || destination.isBlank()) {

            throw new IllegalArgumentException(
                    "WebSocket subscription destination is required"
            );
        }

        String prefix =
                "/topic/conversations/";

        if (!destination.startsWith(prefix)) {
            return;
        }

        String conversationIdText =
                destination
                        .substring(prefix.length())
                        .trim();

        if (conversationIdText.isBlank()) {
            throw new IllegalArgumentException(
                    "Conversation ID is required"
            );
        }

        Long conversationId;

        try {

            conversationId =
                    Long.parseLong(conversationIdText);

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "Invalid conversation ID"
            );
        }

        if (conversationId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid conversation ID"
            );
        }

        String email =
                accessor.getUser().getName();

        User user =
                userRepository.findByEmail(
                        email.trim().toLowerCase()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "WebSocket user not found"
                        )
                );

        boolean participant =
                participantRepository
                        .existsByConversationIdAndUserIdAndStatus(
                                conversationId,
                                user.getId(),
                                ParticipantStatus.ACTIVE
                        );

        if (!participant) {

            throw new IllegalArgumentException(
                    "You are not an active participant in this conversation"
            );
        }
    }
}