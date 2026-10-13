package com.skillswap.rating.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.rating.entity.Rating;
import com.skillswap.rating.entity.RatingCategory;
import com.skillswap.rating.entity.RatingStatus;
import com.skillswap.session.entity.LearningSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findBySession(LearningSession session);

    Page<Rating> findBySession(LearningSession session, Pageable pageable);

    List<Rating> findBySessionId(Long sessionId);

    Page<Rating> findBySessionId(Long sessionId, Pageable pageable);

    List<Rating> findByRaterUser(User raterUser);

    Page<Rating> findByRaterUser(User raterUser, Pageable pageable);

    List<Rating> findByRaterUserId(Long userId);

    Page<Rating> findByRaterUserId(Long userId, Pageable pageable);

    List<Rating> findByRatedUser(User ratedUser);

    Page<Rating> findByRatedUser(User ratedUser, Pageable pageable);

    List<Rating> findByRatedUserId(Long userId);

    Page<Rating> findByRatedUserId(Long userId, Pageable pageable);

    List<Rating> findByRatedUserAndStatus(
            User ratedUser,
            RatingStatus status
    );

    Page<Rating> findByRatedUserAndStatus(
            User ratedUser,
            RatingStatus status,
            Pageable pageable
    );

    List<Rating> findByRatedUserIdAndStatus(
            Long userId,
            RatingStatus status
    );

    Page<Rating> findByRatedUserIdAndStatus(
            Long userId,
            RatingStatus status,
            Pageable pageable
    );

    List<Rating> findByCategory(RatingCategory category);

    List<Rating> findByStatus(RatingStatus status);

    Page<Rating> findByStatus(
            RatingStatus status,
            Pageable pageable
    );

    Optional<Rating> findBySessionIdAndRaterUserIdAndRatedUserIdAndCategory(
            Long sessionId,
            Long raterUserId,
            Long ratedUserId,
            RatingCategory category
    );

    boolean existsBySessionIdAndRaterUserIdAndRatedUserIdAndCategory(
            Long sessionId,
            Long raterUserId,
            Long ratedUserId,
            RatingCategory category
    );

    boolean existsBySessionIdAndRaterUserIdAndRatedUserId(
            Long sessionId,
            Long raterUserId,
            Long ratedUserId
    );

    long countByRatedUser(User ratedUser);

    long countByRatedUserId(Long userId);

    long countByRaterUser(User raterUser);

    long countBySession(LearningSession session);

    long countBySessionId(Long sessionId);

    long countByRatedUserAndStatus(
            User ratedUser,
            RatingStatus status
    );

    long countByRatedUserIdAndStatus(
            Long userId,
            RatingStatus status
    );

    long countByStatus(RatingStatus status);

    long countByCategory(RatingCategory category);

    @Query("""
            SELECT COALESCE(AVG(r.score), 0)
            FROM Rating r
            WHERE r.ratedUser.id = :userId
              AND r.status = :status
            """)
    Double calculateAverageScore(
            @Param("userId") Long userId,
            @Param("status") RatingStatus status
    );

    @Query("""
            SELECT COALESCE(AVG(r.score), 0)
            FROM Rating r
            WHERE r.ratedUser.id = :userId
              AND r.category = :category
              AND r.status = :status
            """)
    Double calculateAverageScoreByCategory(
            @Param("userId") Long userId,
            @Param("category") RatingCategory category,
            @Param("status") RatingStatus status
    );

    @Query("""
            SELECT COUNT(r)
            FROM Rating r
            WHERE r.ratedUser.id = :userId
              AND r.status = :status
              AND r.score = :score
            """)
    long countByUserAndScore(
            @Param("userId") Long userId,
            @Param("status") RatingStatus status,
            @Param("score") Integer score
    );

    @Query("""
            SELECT r
            FROM Rating r
            WHERE r.ratedUser.id = :userId
              AND r.status = :status
            ORDER BY r.createdAt DESC
            """)
    Page<Rating> findVisibleRatingsForUser(
            @Param("userId") Long userId,
            @Param("status") RatingStatus status,
            Pageable pageable
    );

    @Query("""
            SELECT r
            FROM Rating r
            WHERE r.raterUser.id = :userId
            ORDER BY r.createdAt DESC
            """)
    Page<Rating> findRatingsGivenByUser(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
            SELECT r
            FROM Rating r
            WHERE r.session.id = :sessionId
            ORDER BY r.createdAt DESC
            """)
    Page<Rating> findRatingsForSession(
            @Param("sessionId") Long sessionId,
            Pageable pageable
    );

    @Query("""
            SELECT r
            FROM Rating r
            WHERE r.ratedUser.id = :userId
              AND r.status = :status
              AND r.score >= :minimumScore
            ORDER BY r.score DESC, r.createdAt DESC
            """)
    Page<Rating> findHighRatingsForUser(
            @Param("userId") Long userId,
            @Param("status") RatingStatus status,
            @Param("minimumScore") Integer minimumScore,
            Pageable pageable
    );

    @Query("""
            SELECT r
            FROM Rating r
            WHERE r.ratedUser.id = :userId
              AND r.status = :status
              AND (
                    LOWER(COALESCE(r.comment, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(r.category, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            ORDER BY r.createdAt DESC
            """)
    Page<Rating> searchUserRatings(
            @Param("userId") Long userId,
            @Param("status") RatingStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
            SELECT COALESCE(SUM(r.score), 0)
            FROM Rating r
            WHERE r.ratedUser.id = :userId
              AND r.status = :status
            """)
    Long sumScoresForUser(
            @Param("userId") Long userId,
            @Param("status") RatingStatus status
    );

    void deleteBySession(LearningSession session);

    void deleteBySessionId(Long sessionId);

    void deleteByRaterUser(User user);

    void deleteByRatedUser(User user);
}