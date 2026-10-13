package com.skillswap.session.dto;

import com.skillswap.session.entity.SessionType;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class UpdateSessionRequest {

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Size(max = 200, message = "Skill name must not exceed 200 characters")
    private String skillName;

    private SessionType sessionType;

    private LocalDateTime scheduledStart;

    private LocalDateTime scheduledEnd;

    @Size(max = 2000, message = "Meeting URL must not exceed 2000 characters")
    private String meetingUrl;

    @Size(max = 1000, message = "Location must not exceed 1000 characters")
    private String location;

    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;

    public UpdateSessionRequest() {
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

            if (title.isBlank()) {
                title = null;
            }
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

    public boolean isEmpty() {

        return title == null
                && description == null
                && skillName == null
                && sessionType == null
                && scheduledStart == null
                && scheduledEnd == null
                && meetingUrl == null
                && location == null
                && notes == null;
    }

    public boolean hasScheduleUpdate() {

        return scheduledStart != null
                || scheduledEnd != null;
    }

    public boolean hasBasicInformationUpdate() {

        return title != null
                || description != null
                || skillName != null
                || sessionType != null;
    }

    public boolean hasMeetingInformationUpdate() {

        return meetingUrl != null
                || location != null;
    }

    public boolean hasNotesUpdate() {

        return notes != null;
    }
}