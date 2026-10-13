package com.skillswap.ai.client;

import java.util.Map;

public interface AiClient {

    String generate(
            String prompt
    );

    String generate(
            String prompt,
            Map<String, Object> context
    );

    boolean isAvailable();
}