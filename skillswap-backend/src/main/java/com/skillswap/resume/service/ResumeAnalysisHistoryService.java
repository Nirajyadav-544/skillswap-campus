package com.skillswap.resume.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.resume.dto.ResumeAnalysisResponse;
import com.skillswap.resume.entity.Resume;
import com.skillswap.resume.entity.ResumeAnalysis;
import com.skillswap.resume.repository.ResumeAnalysisRepository;
import com.skillswap.resume.repository.ResumeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ResumeAnalysisHistoryService {

    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    public ResumeAnalysisHistoryService(
            ResumeAnalysisRepository resumeAnalysisRepository,
            ResumeRepository resumeRepository,
            UserRepository userRepository
    ) {
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ResumeAnalysisResponse> getAnalyses(
            Long resumeId,
            String email
    ) {

        User user = getUser(email);
        Resume resume = getResume(resumeId, user);

        return resumeAnalysisRepository
                .findByResume(resume)
                .stream()
                .map(ResumeAnalysisResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ResumeAnalysisResponse> getAnalysesPage(
            Long resumeId,
            String email,
            int page,
            int size
    ) {

        User user = getUser(email);
        Resume resume = getResume(resumeId, user);

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        return resumeAnalysisRepository
                .findByResume(resume, pageable)
                .map(ResumeAnalysisResponse::from);
    }

    @Transactional(readOnly = true)
    public ResumeAnalysisResponse getAnalysis(
            Long resumeId,
            Long analysisId,
            String email
    ) {

        User user = getUser(email);
        Resume resume = getResume(resumeId, user);

        ResumeAnalysis analysis =
                resumeAnalysisRepository
                        .findByIdAndResume(
                                analysisId,
                                resume
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resume analysis not found"
                                )
                        );

        return ResumeAnalysisResponse.from(analysis);
    }

    @Transactional(readOnly = true)
    public ResumeAnalysisResponse getLatestAnalysis(
            Long resumeId,
            String email
    ) {

        User user = getUser(email);
        Resume resume = getResume(resumeId, user);

        ResumeAnalysis analysis =
                resumeAnalysisRepository
                        .findFirstByResumeOrderByCreatedAtDesc(
                                resume
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No resume analysis found"
                                )
                        );

        return ResumeAnalysisResponse.from(analysis);
    }

    @Transactional(readOnly = true)
    public long countAnalyses(
            Long resumeId,
            String email
    ) {

        User user = getUser(email);
        Resume resume = getResume(resumeId, user);

        return resumeAnalysisRepository.countByResume(resume);
    }

    private Resume getResume(
            Long resumeId,
            User user
    ) {

        if (resumeId == null) {
            throw new BadRequestException(
                    "Resume ID is required"
            );
        }

        return resumeRepository
                .findByIdAndUser(resumeId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resume not found"
                        )
                );
    }

    private User getUser(String email) {

        if (email == null || email.isBlank()) {
            throw new BadRequestException(
                    "Authenticated user email is required"
            );
        }

        return userRepository
                .findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }
}