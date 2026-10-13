package com.skillswap.activity.repository;

import com.skillswap.activity.entity.Activity;
import com.skillswap.activity.entity.ActivityType;
import com.skillswap.identity.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository
        extends JpaRepository<Activity, Long> {

    List<Activity> findByUserOrderByCreatedAtDesc(
            User user
    );

    Page<Activity> findByUserOrderByCreatedAtDesc(
            User user,
            Pageable pageable
    );

    List<Activity> findByUserAndTypeOrderByCreatedAtDesc(
            User user,
            ActivityType type
    );

    long countByUser(User user);

    void deleteByUser(User user);
}