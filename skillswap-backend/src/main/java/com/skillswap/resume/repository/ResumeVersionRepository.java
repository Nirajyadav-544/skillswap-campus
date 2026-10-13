package com.skillswap.resume.repository;

import com.skillswap.resume.entity.Resume;
import com.skillswap.resume.entity.ResumeVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeVersionRepository
        extends JpaRepository<ResumeVersion, Long> {

    List<ResumeVersion> findByResume(
            Resume resume
    );

    Page<ResumeVersion> findByResume(
            Resume resume,
            Pageable pageable
    );

    Optional<ResumeVersion> findByIdAndResume(
            Long id,
            Resume resume
    );

    Optional<ResumeVersion> findByResumeAndVersionNumber(
            Resume resume,
            Integer versionNumber
    );

    boolean existsByResumeAndVersionNumber(
            Resume resume,
            Integer versionNumber
    );

    long countByResume(
            Resume resume
    );

    void deleteByResume(
            Resume resume
    );
}