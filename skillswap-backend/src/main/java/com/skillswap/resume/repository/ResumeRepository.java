package com.skillswap.resume.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.resume.entity.Resume;
import com.skillswap.resume.entity.ResumeStatus;
import com.skillswap.resume.entity.ResumeVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByUser(User user);

    Page<Resume> findByUser(
            User user,
            Pageable pageable
    );

    List<Resume> findByUserAndStatus(
            User user,
            ResumeStatus status
    );

    Page<Resume> findByUserAndStatus(
            User user,
            ResumeStatus status,
            Pageable pageable
    );

    List<Resume> findByUserAndVisibility(
            User user,
            ResumeVisibility visibility
    );

    Page<Resume> findByUserAndVisibility(
            User user,
            ResumeVisibility visibility,
            Pageable pageable
    );

    List<Resume> findByUserAndPrimaryResumeTrue(
            User user
    );

    Optional<Resume> findFirstByUserAndPrimaryResumeTrue(
            User user
    );

    Optional<Resume> findByIdAndUser(
            Long id,
            User user
    );

    boolean existsByIdAndUser(
            Long id,
            User user
    );

    long countByUser(User user);

    long countByUserAndStatus(
            User user,
            ResumeStatus status
    );

    long countByUserAndVisibility(
            User user,
            ResumeVisibility visibility
    );

    long countByUserAndPrimaryResumeTrue(
            User user
    );

    void deleteByUser(User user);
}