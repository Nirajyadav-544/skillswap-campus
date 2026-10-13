package com.skillswap.resume.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.resume.dto.ResumeExportResponse;
import com.skillswap.resume.entity.Resume;
import com.skillswap.resume.entity.ResumeVersion;
import com.skillswap.resume.repository.ResumeRepository;
import com.skillswap.resume.repository.ResumeVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ResumeExportService {

    private final ResumeRepository resumeRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final UserRepository userRepository;

    public ResumeExportService(
            ResumeRepository resumeRepository,
            ResumeVersionRepository resumeVersionRepository,
            UserRepository userRepository
    ) {
        this.resumeRepository = resumeRepository;
        this.resumeVersionRepository = resumeVersionRepository;
        this.userRepository = userRepository;
    }

    public ResumeExportResponse exportLatestVersion(
            Long resumeId,
            String email
    ) {

        User user = getUser(email);
        Resume resume = getResume(resumeId, user);

        ResumeVersion version =
                resumeVersionRepository
                        .findByResumeAndVersionNumber(
                                resume,
                                getLatestVersionNumber(resume)
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resume version not found"
                                )
                        );

        return buildResponse(
                resume,
                version
        );
    }

    public ResumeExportResponse exportVersion(
            Long resumeId,
            Long versionId,
            String email
    ) {

        User user = getUser(email);
        Resume resume = getResume(resumeId, user);

        ResumeVersion version =
                resumeVersionRepository
                        .findByIdAndResume(
                                versionId,
                                resume
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resume version not found"
                                )
                        );

        return buildResponse(
                resume,
                version
        );
    }

    private ResumeExportResponse buildResponse(
            Resume resume,
            ResumeVersion version
    ) {

        String fileName = version.getFileName();

        if (fileName == null || fileName.isBlank()) {
            fileName = version.getTitle();
        }

        if (fileName == null || fileName.isBlank()) {
            fileName = "resume";
        }

        return new ResumeExportResponse(
                resume.getId(),
                version.getId(),
                fileName,
                version.getFileType(),
                version.getFileUrl(),
                true,
                "Resume version is ready for export"
        );
    }

    private int getLatestVersionNumber(
            Resume resume
    ) {

        long count =
                resumeVersionRepository.countByResume(resume);

        if (count <= 0) {
            throw new ResourceNotFoundException(
                    "No resume versions found"
            );
        }

        if (count > Integer.MAX_VALUE) {
            throw new BadRequestException(
                    "Invalid resume version count"
            );
        }

        return (int) count;
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
                .findByIdAndUser(
                        resumeId,
                        user
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resume not found"
                        )
                );
    }

    private User getUser(
            String email
    ) {

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