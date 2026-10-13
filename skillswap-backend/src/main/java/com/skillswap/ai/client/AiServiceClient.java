package com.skillswap.ai.client;

import com.skillswap.common.exception.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AiServiceClient implements AiClient {

    private final AiClientConfig config;

    private final RestClient restClient;

    public AiServiceClient(
            AiClientConfig config
    ) {
        this.config = config;

        this.restClient = RestClient.builder()
                .baseUrl(config.getBaseUrl())
                .build();
    }

    @Override
    public String generate(
            String prompt
    ) {
        return generate(
                prompt,
                new LinkedHashMap<>()
        );
    }

    @Override
    public String generate(
            String prompt,
            Map<String, Object> context
    ) {
        if (prompt == null
                || prompt.isBlank()) {
            throw new BadRequestException(
                    "AI prompt is required"
            );
        }

        if (!config.isEnabled()) {
            return fallbackResponse();
        }

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "prompt",
                prompt.trim()
        );

        request.put(
                "context",
                context == null
                        ? new LinkedHashMap<>()
                        : context
        );

        try {
            String response =
                    restClient.post()
                            .uri(config.getGeneratePath())
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .body(request)
                            .retrieve()
                            .body(String.class);

            if (response == null
                    || response.isBlank()) {
                throw new IllegalStateException(
                        "AI service returned an empty response"
                );
            }

            return response;

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to communicate with AI service",
                    exception
            );
        }
    }

    @Override
    public boolean isAvailable() {
        return config.isEnabled();
    }

    private String fallbackResponse() {
        return "AI provider integration is not enabled yet.";
    }
}