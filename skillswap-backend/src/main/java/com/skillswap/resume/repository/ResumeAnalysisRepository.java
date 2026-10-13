package com.skillswap.resume.repository;

import com.skillswap.resume.entity.Resume;
import com.skillswap.resume.entity.ResumeAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeAnalysisRepository
        extends JpaRepository<ResumeAnalysis, Long> {

    List<ResumeAnalysis> findByResume(
            Resume resume
    );

    Page<ResumeAnalysis> findByResume(
            Resume resume,
            Pageable pageable
    );

    Optional<ResumeAnalysis> findByIdAndResume(
            Long id,
            Resume resume
    );

    Optional<ResumeAnalysis> findFirstByResumeOrderByCreatedAtDesc(
            Resume resume
    );

    long countByResume(
            Resume resume
    );

    void deleteByResume(
            Resume resume
    );
}