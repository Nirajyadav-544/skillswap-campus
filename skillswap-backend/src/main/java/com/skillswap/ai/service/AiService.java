package com.skillswap.ai.service;

import com.skillswap.ai.dto.AiRequest;
import com.skillswap.ai.dto.AiResponse;
import com.skillswap.common.exception.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    /**
     * Common entry point for AI requests.
     *
     * The actual external AI integration will be connected
     * through the AI client layer later.
     */
    public AiResponse process(
            AiRequest request
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "AI request is required"
            );
        }

        request.normalize();

        if (request.getPrompt() == null
                || request.getPrompt().isBlank()) {

            throw new BadRequestException(
                    "AI prompt is required"
            );
        }

        /*
         * The external AI provider is intentionally not called
         * at this stage.
         *
         * This service establishes the application-level AI
         * entry point. The actual provider integration will be
         * handled through AiClient.
         */
        return AiResponse.success(
                "AI request accepted",
                "AI processing service is ready for provider integration."
        );
    }
}