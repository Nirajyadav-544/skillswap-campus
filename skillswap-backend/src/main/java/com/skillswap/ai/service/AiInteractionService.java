package com.skillswap.ai.service;

import com.skillswap.ai.client.AiClient;
import com.skillswap.ai.dto.AiRequest;
import com.skillswap.ai.dto.AiResponse;
import com.skillswap.ai.entity.AiInteraction;
import com.skillswap.ai.entity.AiInteractionType;
import com.skillswap.ai.repository.AiInteractionRepository;
import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AiInteractionService {

    private final AiInteractionRepository aiInteractionRepository;

    private final UserRepository userRepository;

    private final AiClient aiClient;

    public AiInteractionService(
            AiInteractionRepository aiInteractionRepository,
            UserRepository userRepository,
            AiClient aiClient
    ) {
        this.aiInteractionRepository =
                aiInteractionRepository;

        this.userRepository =
                userRepository;

        this.aiClient =
                aiClient;
    }

    @Transactional
    public AiResponse process(
            AiRequest request,
            String userEmail
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "AI request is required"
            );
        }

        if (userEmail == null
                || userEmail.isBlank()) {
            throw new BadRequestException(
                    "User email is required"
            );
        }

        request.normalize();

        if (request.getPrompt() == null
                || request.getPrompt().isBlank()) {
            throw new BadRequestException(
                    "AI prompt is required"
            );
        }

        User user =
                userRepository
                        .findByEmail(
                                userEmail.trim()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "User not found"
                                )
                        );

        AiInteraction interaction =
                new AiInteraction(
                        user,
                        AiInteractionType.GENERAL_CHAT,
                        request.getPrompt()
                );

        interaction.setContextType(
                request.getContextType()
        );

        interaction.setContextId(
                request.getContextId()
        );

        interaction.setProvider(
                aiClient.isAvailable()
                        ? "AI_SERVICE"
                        : "LOCAL_FALLBACK"
        );

        interaction.markProcessing();

        interaction =
                aiInteractionRepository.save(
                        interaction
                );

        try {
            String result =
                    aiClient.generate(
                            request.getPrompt()
                    );

            interaction.markCompleted(
                    result
            );

            aiInteractionRepository.save(
                    interaction
            );

            return AiResponse.success(
                    "AI request processed successfully",
                    result
            );

        } catch (Exception exception) {

            interaction.markFailed(
                    exception.getMessage()
            );

            aiInteractionRepository.save(
                    interaction
            );

            return AiResponse.failure(
                    "AI request could not be processed"
            );
        }
    }

    @Transactional(readOnly = true)
    public Optional<AiInteraction> getById(
            Long interactionId,
            String userEmail
    ) {
        if (interactionId == null) {
            throw new BadRequestException(
                    "Interaction ID is required"
            );
        }

        User user =
                getUser(
                        userEmail
                );

        return aiInteractionRepository
                .findByIdAndUser(
                        interactionId,
                        user
                );
    }

    @Transactional(readOnly = true)
    public java.util.List<AiInteraction> getMyInteractions(
            String userEmail
    ) {
        User user =
                getUser(
                        userEmail
                );

        return aiInteractionRepository
                .findByUser(
                        user
                );
    }

    @Transactional(readOnly = true)
    public long count(
            String userEmail
    ) {
        User user =
                getUser(
                        userEmail
                );

        return aiInteractionRepository
                .countByUser(
                        user
                );
    }

    private User getUser(
            String userEmail
    ) {
        if (userEmail == null
                || userEmail.isBlank()) {
            throw new BadRequestException(
                    "User email is required"
            );
        }

        return userRepository
                .findByEmail(
                        userEmail.trim()
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found"
                        )
                );
    }
}
