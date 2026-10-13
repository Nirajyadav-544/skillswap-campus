package com.skillswap.chat.dto;

import com.skillswap.chat.entity.ConversationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class CreateConversationRequest {

    @NotNull
    private ConversationType type = ConversationType.DIRECT;

    @Size(max = 200)
    private String title;

    @Size(max = 1000)
    private String description;

    @Size(max = 100)
    private String referenceType;

    private Long referenceId;

    @NotNull
    @Size(min = 1, max = 100)
    private List<Long> participantUserIds = new ArrayList<>();

    public CreateConversationRequest() {
    }

    public ConversationType getType() {
        return type;
    }

    public void setType(ConversationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public List<Long> getParticipantUserIds() {
        return participantUserIds;
    }

    public void setParticipantUserIds(List<Long> participantUserIds) {
        this.participantUserIds = participantUserIds;
    }

    public void normalize() {
        if (type == null) {
            type = ConversationType.DIRECT;
        }

        if (title != null) {
            title = title.trim();

            if (title.isEmpty()) {
                title = null;
            }
        }

        if (description != null) {
            description = description.trim();

            if (description.isEmpty()) {
                description = null;
            }
        }

        if (referenceType != null) {
            referenceType = referenceType.trim();

            if (referenceType.isEmpty()) {
                referenceType = null;
            }
        }

        if (participantUserIds == null) {
            participantUserIds = new ArrayList<>();
        }
    }
}