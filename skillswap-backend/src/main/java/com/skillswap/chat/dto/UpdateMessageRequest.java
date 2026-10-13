package com.skillswap.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateMessageRequest {

    @NotBlank
    @Size(max = 10000)
    private String content;

    public UpdateMessageRequest() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void normalize() {
        if (content != null) {
            content = content.trim();
        }
    }
}