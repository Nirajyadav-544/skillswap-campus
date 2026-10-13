package com.skillswap.session.repository;

import com.skillswap.session.entity.LearningSession;
import com.skillswap.session.entity.SessionStatus;
import com.skillswap.session.entity.SessionType;
import com.skillswap.identity.entity.User;
import com.skillswap.exchange.entity.ExchangeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LearningSessionRepository
        extends JpaRepository<LearningSession, Long> {

    List<LearningSession> findByExchangeRequest(
            ExchangeRequest exchangeRequest
    );

    Page<LearningSession> findByExchangeRequest(
            ExchangeRequest exchangeRequest,
            Pageable pageable
    );

    List<LearningSession> findByExchangeRequestId(
            Long exchangeRequestId
    );

    Page<LearningSession> findByExchangeRequestId(
            Long exchangeRequestId,
            Pageable pageable
    );

    Optional<LearningSession> findFirstByExchangeRequestIdOrderByCreatedAtDesc(
            Long exchangeRequestId
    );

    boolean existsByExchangeRequestId(
            Long exchangeRequestId
    );

    List<LearningSession> findByTeacherUser(
            User teacherUser
    );

    Page<LearningSession> findByTeacherUser(
            User teacherUser,
            Pageable pageable
    );

    List<LearningSession> findByTeacherUserId(
            Long teacherUserId
    );

    Page<LearningSession> findByTeacherUserId(
            Long teacherUserId,
            Pageable pageable
    );

    List<LearningSession> findByLearnerUser(
            User learnerUser
    );

    Page<LearningSession> findByLearnerUser(
            User learnerUser,
            Pageable pageable
    );

    List<LearningSession> findByLearnerUserId(
            Long learnerUserId
    );

    Page<LearningSession> findByLearnerUserId(
            Long learnerUserId,
            Pageable pageable
    );

    List<LearningSession> findByStatus(
            SessionStatus status
    );

    Page<LearningSession> findByStatus(
            SessionStatus status,
            Pageable pageable
    );

    List<LearningSession> findBySessionType(
            SessionType sessionType
    );

    Page<LearningSession> findBySessionType(
            SessionType sessionType,
            Pageable pageable
    );

    List<LearningSession> findByTeacherUserAndStatus(
            User teacherUser,
            SessionStatus status
    );

    Page<LearningSession> findByTeacherUserAndStatus(
            User teacherUser,
            SessionStatus status,
            Pageable pageable
    );

    List<LearningSession> findByLearnerUserAndStatus(
            User learnerUser,
            SessionStatus status
    );

    Page<LearningSession> findByLearnerUserAndStatus(
            User learnerUser,
            SessionStatus status,
            Pageable pageable
    );

    List<LearningSession> findByTeacherUserIdAndStatus(
            Long teacherUserId,
            SessionStatus status
    );

    Page<LearningSession> findByTeacherUserIdAndStatus(
            Long teacherUserId,
            SessionStatus status,
            Pageable pageable
    );

    List<LearningSession> findByLearnerUserIdAndStatus(
            Long learnerUserId,
            SessionStatus status
    );

    Page<LearningSession> findByLearnerUserIdAndStatus(
            Long learnerUserId,
            SessionStatus status,
            Pageable pageable
    );

    List<LearningSession> findBySkillNameIgnoreCase(
            String skillName
    );

    Page<LearningSession> findBySkillNameIgnoreCase(
            String skillName,
            Pageable pageable
    );

    List<LearningSession> findByStatusAndScheduledStartAfter(
            SessionStatus status,
            LocalDateTime scheduledStart
    );

    Page<LearningSession> findByStatusAndScheduledStartAfter(
            SessionStatus status,
            LocalDateTime scheduledStart,
            Pageable pageable
    );

    List<LearningSession> findByScheduledStartAfter(
            LocalDateTime scheduledStart
    );

    Page<LearningSession> findByScheduledStartAfter(
            LocalDateTime scheduledStart,
            Pageable pageable
    );

    List<LearningSession> findByScheduledStartBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    Page<LearningSession> findByScheduledStartBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    List<LearningSession> findByScheduledEndBefore(
            LocalDateTime scheduledEnd
    );

    Page<LearningSession> findByScheduledEndBefore(
            LocalDateTime scheduledEnd,
            Pageable pageable
    );

    long countByTeacherUser(
            User teacherUser
    );

    long countByLearnerUser(
            User learnerUser
    );

    long countByStatus(
            SessionStatus status
    );

    long countBySessionType(
            SessionType sessionType
    );

    long countByTeacherUserAndStatus(
            User teacherUser,
            SessionStatus status
    );

    long countByLearnerUserAndStatus(
            User learnerUser,
            SessionStatus status
    );

    void deleteByExchangeRequest(
            ExchangeRequest exchangeRequest
    );

    void deleteByExchangeRequestId(
            Long exchangeRequestId
    );

    @Query("""
            SELECT s
            FROM LearningSession s
            WHERE
                (
                    s.teacherUser.id = :userId
                    OR
                    s.learnerUser.id = :userId
                )
            ORDER BY s.scheduledStart ASC
            """)
    List<LearningSession> findMySessions(
            @Param("userId") Long userId
    );

    @Query("""
            SELECT s
            FROM LearningSession s
            WHERE
                (
                    s.teacherUser.id = :userId
                    OR
                    s.learnerUser.id = :userId
                )
            """)
    Page<LearningSession> findMySessions(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
            SELECT s
            FROM LearningSession s
            WHERE
                (
                    s.teacherUser.id = :userId
                    OR
                    s.learnerUser.id = :userId
                )
                AND s.status = :status
            ORDER BY s.scheduledStart ASC
            """)
    List<LearningSession> findMySessionsByStatus(
            @Param("userId") Long userId,
            @Param("status") SessionStatus status
    );

    @Query("""
            SELECT s
            FROM LearningSession s
            WHERE
                (
                    s.teacherUser.id = :userId
                    OR
                    s.learnerUser.id = :userId
                )
                AND s.status = :status
            """)
    Page<LearningSession> findMySessionsByStatus(
            @Param("userId") Long userId,
            @Param("status") SessionStatus status,
            Pageable pageable
    );

    @Query("""
            SELECT s
            FROM LearningSession s
            WHERE
                (
                    LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR
                    LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR
                    LOWER(s.skillName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            """)
    Page<LearningSession> searchByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
            SELECT s
            FROM LearningSession s
            WHERE
                (
                    s.teacherUser.id = :userId
                    OR
                    s.learnerUser.id = :userId
                )
                AND
                (
                    LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR
                    LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR
                    LOWER(s.skillName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            """)
    Page<LearningSession> searchMySessionsByKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
            SELECT s
            FROM LearningSession s
            WHERE
                (
                    s.teacherUser.id = :userId
                    OR
                    s.learnerUser.id = :userId
                )
                AND s.scheduledStart >= :from
                AND s.scheduledStart <= :to
            ORDER BY s.scheduledStart ASC
            """)
    List<LearningSession> findMySessionsBetween(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
            SELECT s
            FROM LearningSession s
            WHERE
                (
                    s.teacherUser.id = :userId
                    OR
                    s.learnerUser.id = :userId
                )
                AND s.scheduledStart >= :from
                AND s.scheduledStart <= :to
            """)
    Page<LearningSession> findMySessionsBetween(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    @Query("""
            SELECT s
            FROM LearningSession s
            WHERE s.status IN :statuses
            AND s.scheduledEnd < :now
            """)
    List<LearningSession> findSessionsPastEndTime(
            @Param("statuses") List<SessionStatus> statuses,
            @Param("now") LocalDateTime now
    );

    @Query("""
            SELECT s
            FROM LearningSession s
            WHERE s.status IN :statuses
            AND s.scheduledStart <= :now
            AND s.scheduledEnd > :now
            """)
    List<LearningSession> findSessionsCurrentlyRunning(
            @Param("statuses") List<SessionStatus> statuses,
            @Param("now") LocalDateTime now
    );

    @Query("""
            SELECT s
            FROM LearningSession s
            WHERE s.status IN :statuses
            AND s.scheduledStart > :now
            ORDER BY s.scheduledStart ASC
            """)
    List<LearningSession> findUpcomingSessions(
            @Param("statuses") List<SessionStatus> statuses,
            @Param("now") LocalDateTime now
    );

    @Query("""
            SELECT s
            FROM LearningSession s
            WHERE
                (
                    s.teacherUser.id = :userId
                    OR
                    s.learnerUser.id = :userId
                )
                AND s.status IN :statuses
                AND s.scheduledStart > :now
            ORDER BY s.scheduledStart ASC
            """)
    List<LearningSession> findMyUpcomingSessions(
            @Param("userId") Long userId,
            @Param("statuses") List<SessionStatus> statuses,
            @Param("now") LocalDateTime now
    );

    @Query("""
            SELECT s
            FROM LearningSession s
            WHERE
                (
                    s.teacherUser.id = :userId
                    OR
                    s.learnerUser.id = :userId
                )
                AND s.status = :status
                AND s.scheduledStart > :now
            ORDER BY s.scheduledStart ASC
            """)
    List<LearningSession> findMyUpcomingSessionsByStatus(
            @Param("userId") Long userId,
            @Param("status") SessionStatus status,
            @Param("now") LocalDateTime now
    );
}