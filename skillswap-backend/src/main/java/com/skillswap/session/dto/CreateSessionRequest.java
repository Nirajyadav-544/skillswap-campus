package com.skillswap.session.dto;

import com.skillswap.session.entity.SessionType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CreateSessionRequest {

    @NotNull(message = "Exchange request ID is required")
    private Long exchangeRequestId;

    @NotNull(message = "Teacher user ID is required")
    private Long teacherUserId;

    @NotNull(message = "Learner user ID is required")
    private Long learnerUserId;

    @NotBlank(message = "Session title is required")
    @Size(max = 200, message = "Session title cannot exceed 200 characters")
    private String title;

    @Size(
            max = 4000,
            message = "Description cannot exceed 4000 characters"
    )
    private String description;

    @Size(
            max = 200,
            message = "Skill name cannot exceed 200 characters"
    )
    private String skillName;

    @NotNull(message = "Session type is required")
    private SessionType sessionType;

    @NotNull(message = "Scheduled start is required")
    @Future(message = "Scheduled start must be in the future")
    private LocalDateTime scheduledStart;

    @NotNull(message = "Scheduled end is required")
    @Future(message = "Scheduled end must be in the future")
    private LocalDateTime scheduledEnd;

    @Size(
            max = 1000,
            message = "Meeting URL cannot exceed 1000 characters"
    )
    private String meetingUrl;

    @Size(
            max = 500,
            message = "Location cannot exceed 500 characters"
    )
    private String location;

    @Size(
            max = 5000,
            message = "Notes cannot exceed 5000 characters"
    )
    private String notes;

    public CreateSessionRequest() {
    }

    public Long getExchangeRequestId() {
        return exchangeRequestId;
    }

    public void setExchangeRequestId(Long exchangeRequestId) {
        this.exchangeRequestId = exchangeRequestId;
    }

    public Long getTeacherUserId() {
        return teacherUserId;
    }

    public void setTeacherUserId(Long teacherUserId) {
        this.teacherUserId = teacherUserId;
    }

    public Long getLearnerUserId() {
        return learnerUserId;
    }

    public void setLearnerUserId(Long learnerUserId) {
        this.learnerUserId = learnerUserId;
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

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public LocalDateTime getScheduledStart() {
        return scheduledStart;
    }

    public void setScheduledStart(LocalDateTime scheduledStart) {
        this.scheduledStart = scheduledStart;
    }

    public LocalDateTime getScheduledEnd() {
        return scheduledEnd;
    }

    public void setScheduledEnd(LocalDateTime scheduledEnd) {
        this.scheduledEnd = scheduledEnd;
    }

    public String getMeetingUrl() {
        return meetingUrl;
    }

    public void setMeetingUrl(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void normalize() {

        if (title != null) {
            title = title.trim();
        }

        if (description != null) {
            description = description.trim();

            if (description.isBlank()) {
                description = null;
            }
        }

        if (skillName != null) {
            skillName = skillName.trim();

            if (skillName.isBlank()) {
                skillName = null;
            }
        }

        if (meetingUrl != null) {
            meetingUrl = meetingUrl.trim();

            if (meetingUrl.isBlank()) {
                meetingUrl = null;
            }
        }

        if (location != null) {
            location = location.trim();

            if (location.isBlank()) {
                location = null;
            }
        }

        if (notes != null) {
            notes = notes.trim();

            if (notes.isBlank()) {
                notes = null;
            }
        }
    }

    public boolean hasValidSchedule() {

        if (scheduledStart == null || scheduledEnd == null) {
            return false;
        }

        return scheduledEnd.isAfter(scheduledStart);
    }
}