package com.skillswap.resume.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.resume.dto.ResumeUploadResponse;
import com.skillswap.resume.entity.Resume;
import com.skillswap.resume.entity.ResumeVersion;
import com.skillswap.resume.repository.ResumeRepository;
import com.skillswap.resume.repository.ResumeVersionRepository;
import com.skillswap.resume.util.ResumeFileValidator;
import com.skillswap.resume.util.ResumeTextExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ResumeUploadService {

    private final ResumeRepository resumeRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final UserRepository userRepository;

    public ResumeUploadService(
            ResumeRepository resumeRepository,
            ResumeVersionRepository resumeVersionRepository,
            UserRepository userRepository
    ) {
        this.resumeRepository = resumeRepository;
        this.resumeVersionRepository = resumeVersionRepository;
        this.userRepository = userRepository;
    }

    public ResumeUploadResponse upload(
            Long resumeId,
            String email,
            MultipartFile file
    ) {

        User user = getUser(email);

        Resume resume = resumeRepository
                .findByIdAndUser(resumeId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resume not found"
                        )
                );

        ResumeFileValidator.validate(file);

        String fileName = file.getOriginalFilename();

        String fileType = file.getContentType();

        String extractedText =
                ResumeTextExtractor.extractText(file);

        int nextVersionNumber =
                getNextVersionNumber(resume);

        ResumeVersion version =
                new ResumeVersion(
                        resume,
                        nextVersionNumber,
                        fileName
                );

        version.setFileName(fileName);
        version.setFileType(fileType);
        version.setExtractedText(extractedText);

        /*
         * Actual physical file storage is intentionally not
         * performed here until the project's storage strategy
         * (local filesystem / S3 / other object storage) is fixed.
         */
        version.setFileUrl(null);

        version.normalize();

        ResumeVersion savedVersion =
                resumeVersionRepository.save(version);

        return new ResumeUploadResponse(
                resume.getId(),
                savedVersion.getId(),
                savedVersion.getVersionNumber(),
                savedVersion.getFileName(),
                savedVersion.getFileType(),
                savedVersion.getFileUrl(),
                file.getSize(),
                true,
                savedVersion.hasExtractedText(),
                "Resume file metadata uploaded successfully"
        );
    }

    private int getNextVersionNumber(
            Resume resume
    ) {

        long count =
                resumeVersionRepository.countByResume(resume);

        if (count >= Integer.MAX_VALUE) {
            throw new BadRequestException(
                    "Maximum number of resume versions reached"
            );
        }

        return (int) count + 1;
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