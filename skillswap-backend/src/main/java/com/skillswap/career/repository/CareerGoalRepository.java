package com.skillswap.career.repository;

import com.skillswap.career.entity.CareerGoal;
import com.skillswap.career.entity.CareerGoalStatus;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CareerGoalRepository
        extends JpaRepository<CareerGoal, Long> {

    List<CareerGoal> findByUserOrderByCreatedAtDesc(
            User user
    );

    Page<CareerGoal> findByUserOrderByCreatedAtDesc(
            User user,
            Pageable pageable
    );

    List<CareerGoal> findByUserAndStatusOrderByCreatedAtDesc(
            User user,
            CareerGoalStatus status
    );

    Page<CareerGoal> findByUserAndStatusOrderByCreatedAtDesc(
            User user,
            CareerGoalStatus status,
            Pageable pageable
    );

    Optional<CareerGoal> findByIdAndUser(
            Long id,
            User user
    );

    long countByUser(User user);

    long countByUserAndStatus(
            User user,
            CareerGoalStatus status
    );

    boolean existsByIdAndUser(
            Long id,
            User user
    );
}