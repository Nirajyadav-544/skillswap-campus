package com.skillswap.college.repository;

import com.skillswap.college.entity.College;
import com.skillswap.college.entity.CollegeStatus;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollegeRepository extends JpaRepository<College, Long> {

    List<College> findByOwner(User owner);

    Page<College> findByOwner(
            User owner,
            Pageable pageable
    );

    List<College> findByStatus(CollegeStatus status);

    Page<College> findByStatus(
            CollegeStatus status,
            Pageable pageable
    );

    List<College> findByOwnerAndStatus(
            User owner,
            CollegeStatus status
    );

    Page<College> findByOwnerAndStatus(
            User owner,
            CollegeStatus status,
            Pageable pageable
    );

    Optional<College> findByIdAndOwner(
            Long id,
            User owner
    );

    boolean existsByIdAndOwner(
            Long id,
            User owner
    );

    boolean existsByNameIgnoreCase(String name);

    long countByOwner(User owner);

    long countByStatus(CollegeStatus status);

    long countByOwnerAndStatus(
            User owner,
            CollegeStatus status
    );
}