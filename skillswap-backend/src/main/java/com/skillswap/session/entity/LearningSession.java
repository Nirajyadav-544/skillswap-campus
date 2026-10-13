package com.skillswap.session.entity;

import com.skillswap.exchange.entity.ExchangeRequest;
import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents a scheduled learning or collaboration session
 * created from an exchange request.
 */
@Entity
@Table(
        name = "learning_sessions",
        indexes = {

                @Index(
                        name = "idx_session_exchange",
                        columnList = "exchange_request_id"
                ),

                @Index(
                        name = "idx_session_teacher",
                        columnList = "teacher_user_id"
                ),

                @Index(
                        name = "idx_session_learner",
                        columnList = "learner_user_id"
                ),

                @Index(
                        name = "idx_session_status",
                        columnList = "status"
                ),

                @Index(
                        name = "idx_session_type",
                        columnList = "session_type"
                ),

                @Index(
                        name = "idx_session_start",
                        columnList = "scheduled_start"
                ),

                @Index(
                        name = "idx_session_end",
                        columnList = "scheduled_end"
                ),

                @Index(
                        name = "idx_session_created",
                        columnList = "created_at"
                )
        }
)
public class LearningSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Exchange request from which this session originated.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "exchange_request_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_session_exchange_request"
            )
    )
    private ExchangeRequest exchangeRequest;

    /**
     * User acting as teacher, mentor, or session owner.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "teacher_user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_session_teacher_user"
            )
    )
    private User teacherUser;

    /**
     * User acting as learner or recipient.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "learner_user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_session_learner_user"
            )
    )
    private User learnerUser;

    /**
     * Session title.
     */
    @Column(
            name = "title",
            nullable = false,
            length = 200
    )
    private String title;

    /**
     * Detailed session description.
     */
    @Column(
            name = "description",
            length = 4000
    )
    private String description;

    /**
     * Skill being taught/practiced in this session.
     */
    @Column(
            name = "skill_name",
            length = 200
    )
    private String skillName;

    /**
     * Type of session.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "session_type",
            nullable = false,
            length = 40
    )
    private SessionType sessionType = SessionType.ONE_ON_ONE;

    /**
     * Current session lifecycle status.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private SessionStatus status = SessionStatus.SCHEDULED;

    /**
     * Scheduled starting time.
     */
    @Column(
            name = "scheduled_start",
            nullable = false
    )
    private LocalDateTime scheduledStart;

    /**
     * Scheduled ending time.
     */
    @Column(
            name = "scheduled_end",
            nullable = false
    )
    private LocalDateTime scheduledEnd;

    /**
     * Actual time at which the session started.
     */
    @Column(name = "actual_start")
    private LocalDateTime actualStart;

    /**
     * Actual time at which the session ended.
     */
    @Column(name = "actual_end")
    private LocalDateTime actualEnd;

    /**
     * Online meeting URL.
     */
    @Column(
            name = "meeting_url",
            length = 1000
    )
    private String meetingUrl;

    /**
     * Optional physical meeting location.
     */
    @Column(
            name = "location",
            length = 500
    )
    private String location;

    /**
     * Session notes.
     */
    @Column(
            name = "notes",
            length = 5000
    )
    private String notes;

    /**
     * Cancellation/rejection reason.
     */
    @Column(
            name = "status_message",
            length = 2000
    )
    private String statusMessage;

    /**
     * Whether the teacher has joined.
     */
    @Column(
            name = "teacher_joined",
            nullable = false
    )
    private boolean teacherJoined = false;

    /**
     * Whether the learner has joined.
     */
    @Column(
            name = "learner_joined",
            nullable = false
    )
    private boolean learnerJoined = false;

    /**
     * Creation timestamp.
     */
    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    /**
     * Last update timestamp.
     */
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    /**
     * Timestamp when the session was cancelled.
     */
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    /**
     * Timestamp when the session was completed.
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public LearningSession() {
    }

    public LearningSession(
            ExchangeRequest exchangeRequest,
            User teacherUser,
            User learnerUser,
            String title,
            LocalDateTime scheduledStart,
            LocalDateTime scheduledEnd
    ) {
        this.exchangeRequest = exchangeRequest;
        this.teacherUser = teacherUser;
        this.learnerUser = learnerUser;
        this.title = title;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;

        this.sessionType = SessionType.ONE_ON_ONE;
        this.status = SessionStatus.SCHEDULED;

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;

        if (sessionType == null) {
            sessionType = SessionType.ONE_ON_ONE;
        }

        if (status == null) {
            status = SessionStatus.SCHEDULED;
        }

        normalizeValues();
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

        if (sessionType == null) {
            sessionType = SessionType.ONE_ON_ONE;
        }

        if (status == null) {
            status = SessionStatus.SCHEDULED;
        }

        normalizeValues();
    }

    private void normalizeValues() {

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

        if (statusMessage != null) {
            statusMessage = statusMessage.trim();

            if (statusMessage.isBlank()) {
                statusMessage = null;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public ExchangeRequest getExchangeRequest() {
        return exchangeRequest;
    }

    public User getTeacherUser() {
        return teacherUser;
    }

    public User getLearnerUser() {
        return learnerUser;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSkillName() {
        return skillName;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public LocalDateTime getScheduledStart() {
        return scheduledStart;
    }

    public LocalDateTime getScheduledEnd() {
        return scheduledEnd;
    }

    public LocalDateTime getActualStart() {
        return actualStart;
    }

    public LocalDateTime getActualEnd() {
        return actualEnd;
    }

    public String getMeetingUrl() {
        return meetingUrl;
    }

    public String getLocation() {
        return location;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public boolean isTeacherJoined() {
        return teacherJoined;
    }

    public boolean isLearnerJoined() {
        return learnerJoined;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExchangeRequest(
            ExchangeRequest exchangeRequest
    ) {
        this.exchangeRequest = exchangeRequest;
    }

    public void setTeacherUser(User teacherUser) {
        this.teacherUser = teacherUser;
    }

    public void setLearnerUser(User learnerUser) {
        this.learnerUser = learnerUser;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType =
                sessionType != null
                        ? sessionType
                        : SessionType.ONE_ON_ONE;
    }

    public void setStatus(SessionStatus status) {
        this.status =
                status != null
                        ? status
                        : SessionStatus.SCHEDULED;
    }

    public void setScheduledStart(
            LocalDateTime scheduledStart
    ) {
        this.scheduledStart = scheduledStart;
    }

    public void setScheduledEnd(
            LocalDateTime scheduledEnd
    ) {
        this.scheduledEnd = scheduledEnd;
    }

    public void setActualStart(
            LocalDateTime actualStart
    ) {
        this.actualStart = actualStart;
    }

    public void setActualEnd(
            LocalDateTime actualEnd
    ) {
        this.actualEnd = actualEnd;
    }

    public void setMeetingUrl(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setTeacherJoined(
            boolean teacherJoined
    ) {
        this.teacherJoined = teacherJoined;
    }

    public void setLearnerJoined(
            boolean learnerJoined
    ) {
        this.learnerJoined = learnerJoined;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt
    ) {
        this.updatedAt = updatedAt;
    }

    public void setCancelledAt(
            LocalDateTime cancelledAt
    ) {
        this.cancelledAt = cancelledAt;
    }

    public void setCompletedAt(
            LocalDateTime completedAt
    ) {
        this.completedAt = completedAt;
    }

    public boolean isScheduled() {
        return status == SessionStatus.SCHEDULED;
    }

    public boolean isConfirmed() {
        return status == SessionStatus.CONFIRMED;
    }

    public boolean isStarted() {
        return status == SessionStatus.STARTED;
    }

    public boolean isCompleted() {
        return status == SessionStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == SessionStatus.CANCELLED;
    }

    public boolean isRejected() {
        return status == SessionStatus.REJECTED;
    }

    public boolean isExpired() {
        if (status == SessionStatus.EXPIRED) {
            return true;
        }

        return scheduledEnd != null
                && scheduledEnd.isBefore(LocalDateTime.now())
                && !isCompleted()
                && !isCancelled()
                && !isRejected();
    }

    public boolean isArchived() {
        return status == SessionStatus.ARCHIVED;
    }

    public boolean isClosed() {
        return status == SessionStatus.COMPLETED
                || status == SessionStatus.CANCELLED
                || status == SessionStatus.REJECTED
                || status == SessionStatus.EXPIRED
                || status == SessionStatus.NO_SHOW
                || status == SessionStatus.ARCHIVED;
    }

    public boolean isParticipant(Long userId) {

        if (userId == null) {
            return false;
        }

        if (teacherUser != null
                && userId.equals(teacherUser.getId())) {
            return true;
        }

        return learnerUser != null
                && userId.equals(learnerUser.getId());
    }

    public boolean isTeacher(Long userId) {

        return userId != null
                && teacherUser != null
                && userId.equals(teacherUser.getId());
    }

    public boolean isLearner(Long userId) {

        return userId != null
                && learnerUser != null
                && userId.equals(learnerUser.getId());
    }

    public boolean hasMeetingUrl() {
        return meetingUrl != null
                && !meetingUrl.isBlank();
    }

    public boolean hasLocation() {
        return location != null
                && !location.isBlank();
    }

    public boolean hasStartedAt() {
        return actualStart != null;
    }

    public boolean hasEndedAt() {
        return actualEnd != null;
    }

    public boolean hasValidSchedule() {

        if (scheduledStart == null
                || scheduledEnd == null) {
            return false;
        }

        return scheduledEnd.isAfter(scheduledStart);
    }

    public void confirm() {

        if (status != SessionStatus.SCHEDULED) {
            return;
        }

        status = SessionStatus.CONFIRMED;
        updatedAt = LocalDateTime.now();
    }

    public void start() {

        if (status != SessionStatus.CONFIRMED
                && status != SessionStatus.SCHEDULED) {
            return;
        }

        status = SessionStatus.STARTED;

        if (actualStart == null) {
            actualStart = LocalDateTime.now();
        }

        updatedAt = LocalDateTime.now();
    }

    public void complete() {

        status = SessionStatus.COMPLETED;

        LocalDateTime now = LocalDateTime.now();

        if (actualStart == null) {
            actualStart = now;
        }

        if (actualEnd == null) {
            actualEnd = now;
        }

        completedAt = now;
        updatedAt = now;
    }

    public void cancel(String reason) {

        status = SessionStatus.CANCELLED;
        cancelledAt = LocalDateTime.now();

        setStatusMessage(reason);

        updatedAt = LocalDateTime.now();
    }

    public void reject(String reason) {

        status = SessionStatus.REJECTED;

        setStatusMessage(reason);

        updatedAt = LocalDateTime.now();
    }

    public void expire() {

        status = SessionStatus.EXPIRED;

        updatedAt = LocalDateTime.now();
    }

    public void markNoShow(String reason) {

        status = SessionStatus.NO_SHOW;

        setStatusMessage(reason);

        updatedAt = LocalDateTime.now();
    }

    public void archive() {

        status = SessionStatus.ARCHIVED;

        updatedAt = LocalDateTime.now();
    }

    public void markTeacherJoined() {

        teacherJoined = true;
        updatedAt = LocalDateTime.now();
    }

    public void markLearnerJoined() {

        learnerJoined = true;
        updatedAt = LocalDateTime.now();
    }

    public void resetAttendance() {

        teacherJoined = false;
        learnerJoined = false;
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {

        return "LearningSession{" +
                "id=" + id +
                ", exchangeRequestId=" +
                (
                        exchangeRequest != null
                                ? exchangeRequest.getId()
                                : null
                ) +
                ", teacherUserId=" +
                (
                        teacherUser != null
                                ? teacherUser.getId()
                                : null
                ) +
                ", learnerUserId=" +
                (
                        learnerUser != null
                                ? learnerUser.getId()
                                : null
                ) +
                ", title='" + title + '\'' +
                ", skillName='" + skillName + '\'' +
                ", sessionType=" + sessionType +
                ", status=" + status +
                ", scheduledStart=" + scheduledStart +
                ", scheduledEnd=" + scheduledEnd +
                '}';
    }
}
