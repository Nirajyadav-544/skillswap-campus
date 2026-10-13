package com.skillswap.resume.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.resume.dto.CreateResumeVersionRequest;
import com.skillswap.resume.dto.ResumeVersionPageResponse;
import com.skillswap.resume.dto.ResumeVersionResponse;
import com.skillswap.resume.entity.Resume;
import com.skillswap.resume.entity.ResumeVersion;
import com.skillswap.resume.repository.ResumeRepository;
import com.skillswap.resume.repository.ResumeVersionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ResumeVersionService {

    private final ResumeVersionRepository resumeVersionRepository;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    public ResumeVersionService(
            ResumeVersionRepository resumeVersionRepository,
            ResumeRepository resumeRepository,
            UserRepository userRepository
    ) {
        this.resumeVersionRepository = resumeVersionRepository;
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
    }

    public ResumeVersionResponse createVersion(
            Long resumeId,
            String email,
            CreateResumeVersionRequest request
    ) {

        User user = getUser(email);
        Resume resume = getResume(resumeId, user);

        int nextVersionNumber = getNextVersionNumber(resume);

        ResumeVersion version = new ResumeVersion(
                resume,
                nextVersionNumber,
                request.getTitle()
        );

        version.setDescription(request.getDescription());
        version.setFileUrl(request.getFileUrl());
        version.setFileName(request.getFileName());
        version.setFileType(request.getFileType());
        version.setExtractedText(request.getExtractedText());
        version.setAiScore(request.getAiScore());
        version.setChangeSummary(request.getChangeSummary());

        version.normalize();

        ResumeVersion saved = resumeVersionRepository.save(version);

        return ResumeVersionResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ResumeVersionResponse getVersion(
            Long resumeId,
            Long versionId,
            String email
    ) {

        User user = getUser(email);
        Resume resume = getResume(resumeId, user);

        ResumeVersion version = resumeVersionRepository
                .findByIdAndResume(versionId, resume)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resume version not found"
                        )
                );

        return ResumeVersionResponse.from(version);
    }

    @Transactional(readOnly = true)
    public List<ResumeVersionResponse> getVersions(
            Long resumeId,
            String email
    ) {

        User user = getUser(email);
        Resume resume = getResume(resumeId, user);

        return resumeVersionRepository
                .findByResume(resume)
                .stream()
                .map(ResumeVersionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResumeVersionPageResponse getVersionsPage(
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
                        "versionNumber"
                )
        );

        Page<ResumeVersionResponse> result =
                resumeVersionRepository
                        .findByResume(resume, pageable)
                        .map(ResumeVersionResponse::from);

        return ResumeVersionPageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public ResumeVersionResponse getVersionByNumber(
            Long resumeId,
            Integer versionNumber,
            String email
    ) {

        User user = getUser(email);
        Resume resume = getResume(resumeId, user);

        if (versionNumber == null || versionNumber < 1) {
            throw new BadRequestException(
                    "Version number must be greater than zero"
            );
        }

        ResumeVersion version = resumeVersionRepository
                .findByResumeAndVersionNumber(
                        resume,
                        versionNumber
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resume version not found"
                        )
                );

        return ResumeVersionResponse.from(version);
    }

    @Transactional(readOnly = true)
    public long countVersions(
            Long resumeId,
            String email
    ) {

        User user = getUser(email);
        Resume resume = getResume(resumeId, user);

        return resumeVersionRepository.countByResume(resume);
    }

    public void deleteVersion(
            Long resumeId,
            Long versionId,
            String email
    ) {

        User user = getUser(email);
        Resume resume = getResume(resumeId, user);

        ResumeVersion version = resumeVersionRepository
                .findByIdAndResume(versionId, resume)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resume version not found"
                        )
                );

        resumeVersionRepository.delete(version);
    }

    private int getNextVersionNumber(Resume resume) {

        long count = resumeVersionRepository.countByResume(resume);

        if (count >= Integer.MAX_VALUE) {
            throw new BadRequestException(
                    "Maximum number of resume versions reached"
            );
        }

        return (int) count + 1;
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
                .findByEmail(email.trim().toLowerCase())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }
}