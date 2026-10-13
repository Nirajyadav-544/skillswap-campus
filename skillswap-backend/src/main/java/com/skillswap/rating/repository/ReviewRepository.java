package com.skillswap.rating.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.rating.entity.Review;
import com.skillswap.session.entity.LearningSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByRatingId(Long ratingId);

    boolean existsByRatingId(Long ratingId);

    List<Review> findBySession(LearningSession session);

    Page<Review> findBySession(
            LearningSession session,
            Pageable pageable
    );

    List<Review> findBySessionId(Long sessionId);

    Page<Review> findBySessionId(
            Long sessionId,
            Pageable pageable
    );

    List<Review> findByAuthorUser(User authorUser);

    Page<Review> findByAuthorUser(
            User authorUser,
            Pageable pageable
    );

    List<Review> findByAuthorUserId(Long userId);

    Page<Review> findByAuthorUserId(
            Long userId,
            Pageable pageable
    );

    List<Review> findByTargetUser(User targetUser);

    Page<Review> findByTargetUser(
            User targetUser,
            Pageable pageable
    );

    List<Review> findByTargetUserId(Long userId);

    Page<Review> findByTargetUserId(
            Long userId,
            Pageable pageable
    );

    List<Review> findByTargetUserIdAndVisibleTrue(
            Long userId
    );

    Page<Review> findByTargetUserIdAndVisibleTrue(
            Long userId,
            Pageable pageable
    );

    long countByTargetUser(User targetUser);

    long countByTargetUserId(Long userId);

    long countByAuthorUser(User authorUser);

    long countByAuthorUserId(Long userId);

    long countBySession(LearningSession session);

    long countBySessionId(Long sessionId);

    long countByTargetUserIdAndVisibleTrue(Long userId);

    @Query("""
            SELECT r
            FROM Review r
            WHERE r.targetUser.id = :userId
              AND r.visible = true
              AND r.deletedAt IS NULL
            ORDER BY r.createdAt DESC
            """)
    Page<Review> findVisibleReviewsForUser(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
            SELECT r
            FROM Review r
            WHERE r.authorUser.id = :userId
              AND r.deletedAt IS NULL
            ORDER BY r.createdAt DESC
            """)
    Page<Review> findReviewsWrittenByUser(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
            SELECT r
            FROM Review r
            WHERE r.targetUser.id = :userId
              AND r.visible = true
              AND r.deletedAt IS NULL
              AND LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY r.createdAt DESC
            """)
    Page<Review> searchReviewsForUser(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
            SELECT r
            FROM Review r
            WHERE r.session.id = :sessionId
              AND r.deletedAt IS NULL
            ORDER BY r.createdAt DESC
            """)
    List<Review> findActiveReviewsForSession(
            @Param("sessionId") Long sessionId
    );

    void deleteBySession(LearningSession session);

    void deleteBySessionId(Long sessionId);

    void deleteByAuthorUser(User user);

    void deleteByTargetUser(User user);
}