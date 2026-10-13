package com.skillswap.session.dto;

import com.skillswap.session.entity.LearningSession;

import java.time.LocalDateTime;

public record SessionResponse(

        Long id,

        Long exchangeRequestId,

        Long teacherUserId,

        String teacherEmail,

        String teacherFirstName,

        String teacherLastName,

        Long learnerUserId,

        String learnerEmail,

        String learnerFirstName,

        String learnerLastName,

        String title,

        String description,

        String skillName,

        String sessionType,

        String status,

        LocalDateTime scheduledStart,

        LocalDateTime scheduledEnd,

        LocalDateTime actualStart,

        LocalDateTime actualEnd,

        String meetingUrl,

        String location,

        String notes,

        String statusMessage,

        boolean teacherJoined,

        boolean learnerJoined,

        LocalDateTime createdAt,

        LocalDateTime updatedAt,

        LocalDateTime cancelledAt,

        LocalDateTime completedAt,

        boolean scheduled,

        boolean confirmed,

        boolean started,

        boolean completed,

        boolean cancelled,

        boolean rejected,

        boolean expired,

        boolean closed
) {

    public static SessionResponse from(
            LearningSession session
    ) {

        if (session == null) {
            return null;
        }

        var teacher = session.getTeacherUser();
        var learner = session.getLearnerUser();
        var exchange = session.getExchangeRequest();

        return new SessionResponse(

                session.getId(),

                exchange != null
                        ? exchange.getId()
                        : null,

                teacher != null
                        ? teacher.getId()
                        : null,

                teacher != null
                        ? teacher.getEmail()
                        : null,

                teacher != null
                        ? teacher.getFirstName()
                        : null,

                teacher != null
                        ? teacher.getLastName()
                        : null,

                learner != null
                        ? learner.getId()
                        : null,

                learner != null
                        ? learner.getEmail()
                        : null,

                learner != null
                        ? learner.getFirstName()
                        : null,

                learner != null
                        ? learner.getLastName()
                        : null,

                session.getTitle(),

                session.getDescription(),

                session.getSkillName(),

                session.getSessionType() != null
                        ? session.getSessionType().name()
                        : null,

                session.getStatus() != null
                        ? session.getStatus().name()
                        : null,

                session.getScheduledStart(),

                session.getScheduledEnd(),

                session.getActualStart(),

                session.getActualEnd(),

                session.getMeetingUrl(),

                session.getLocation(),

                session.getNotes(),

                session.getStatusMessage(),

                session.isTeacherJoined(),

                session.isLearnerJoined(),

                session.getCreatedAt(),

                session.getUpdatedAt(),

                session.getCancelledAt(),

                session.getCompletedAt(),

                session.isScheduled(),

                session.isConfirmed(),

                session.isStarted(),

                session.isCompleted(),

                session.isCancelled(),

                session.isRejected(),

                session.isExpired(),

                session.isClosed()
        );
    }

    public String getTeacherFullName() {

        return buildFullName(
                teacherFirstName,
                teacherLastName,
                teacherEmail
        );
    }

    public String getLearnerFullName() {

        return buildFullName(
                learnerFirstName,
                learnerLastName,
                learnerEmail
        );
    }

    public String getSessionLabel() {

        if (title != null && !title.isBlank()) {
            return title;
        }

        if (skillName != null && !skillName.isBlank()) {
            return skillName + " Session";
        }

        return "Learning Session";
    }

    public boolean hasMeetingUrl() {

        return meetingUrl != null
                && !meetingUrl.isBlank();
    }

    public boolean hasLocation() {

        return location != null
                && !location.isBlank();
    }

    public boolean hasDescription() {

        return description != null
                && !description.isBlank();
    }

    public boolean hasNotes() {

        return notes != null
                && !notes.isBlank();
    }

    public boolean isActive() {

        return scheduled
                || confirmed
                || started;
    }

    public boolean canBeStarted() {

        return scheduled || confirmed;
    }

    public boolean canBeCompleted() {

        return started;
    }

    public boolean canBeCancelled() {

        return scheduled || confirmed;
    }

    public boolean isOnline() {

        return hasMeetingUrl();
    }

    public boolean isInPerson() {

        return hasLocation()
                && !hasMeetingUrl();
    }

    public boolean isOneOnOne() {

        return "ONE_ON_ONE".equals(sessionType);
    }

    private static String buildFullName(
            String firstName,
            String lastName,
            String fallback
    ) {

        String first =
                firstName != null
                        ? firstName.trim()
                        : "";

        String last =
                lastName != null
                        ? lastName.trim()
                        : "";

        String fullName =
                (first + " " + last).trim();

        if (!fullName.isBlank()) {
            return fullName;
        }

        return fallback;
    }
}