package com.skillswap.resume.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.resume.dto.CreateResumeRequest;
import com.skillswap.resume.dto.ResumePageResponse;
import com.skillswap.resume.dto.ResumeResponse;
import com.skillswap.resume.dto.ResumeSummaryResponse;
import com.skillswap.resume.dto.UpdateResumeRequest;
import com.skillswap.resume.entity.Resume;
import com.skillswap.resume.entity.ResumeStatus;
import com.skillswap.resume.repository.ResumeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ResumeService {

    private final ResumeRepository resumeRepository;

    private final UserRepository userRepository;

    public ResumeService(
            ResumeRepository resumeRepository,
            UserRepository userRepository
    ) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
    }

    public ResumeResponse create(
            CreateResumeRequest request,
            String userEmail
    ) {

        if (request == null) {
            throw new BadRequestException(
                    "Resume request is required"
            );
        }

        request.normalize();

        if (request.getTitle() == null ||
                request.getTitle().isBlank()) {

            throw new BadRequestException(
                    "Resume title is required"
            );
        }

        User user = getUser(userEmail);

        if (Boolean.TRUE.equals(request.getPrimaryResume())) {
            removePrimaryResume(user);
        }

        Resume resume = new Resume(
                user,
                request.getTitle()
        );

        resume.setDescription(
                request.getDescription()
        );

        resume.setFileUrl(
                request.getFileUrl()
        );

        resume.setFileName(
                request.getFileName()
        );

        resume.setFileType(
                request.getFileType()
        );

        resume.setExtractedText(
                request.getExtractedText()
        );

        resume.setVisibility(
                request.getVisibility()
        );

        resume.setPrimaryResume(
                request.getPrimaryResume()
        );

        resume.normalize();

        Resume saved = resumeRepository.save(resume);

        return ResumeResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ResumeResponse getById(
            Long resumeId,
            String userEmail
    ) {

        User user = getUser(userEmail);

        Resume resume = getOwnedResume(
                resumeId,
                user
        );

        return ResumeResponse.from(resume);
    }

    @Transactional(readOnly = true)
    public List<ResumeSummaryResponse> getMyResumes(
            String userEmail
    ) {

        User user = getUser(userEmail);

        return resumeRepository
                .findByUser(user)
                .stream()
                .map(ResumeSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResumePageResponse getMyResumesPage(
            String userEmail,
            int page,
            int size
    ) {

        User user = getUser(userEmail);

        Pageable pageable = PageRequest.of(
                normalizePage(page),
                normalizeSize(size)
        );

        Page<ResumeSummaryResponse> result =
                resumeRepository
                        .findByUser(user, pageable)
                        .map(ResumeSummaryResponse::from);

        return ResumePageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public List<ResumeSummaryResponse> getMyResumesByStatus(
            ResumeStatus status,
            String userEmail
    ) {

        if (status == null) {
            throw new BadRequestException(
                    "Resume status is required"
            );
        }

        User user = getUser(userEmail);

        return resumeRepository
                .findByUserAndStatus(user, status)
                .stream()
                .map(ResumeSummaryResponse::from)
                .toList();
    }

    public ResumeResponse update(
            Long resumeId,
            UpdateResumeRequest request,
            String userEmail
    ) {

        if (request == null) {
            throw new BadRequestException(
                    "Resume update request is required"
            );
        }

        request.normalize();

        if (request.isEmpty()) {
            throw new BadRequestException(
                    "At least one resume field must be provided"
            );
        }

        User user = getUser(userEmail);

        Resume resume = getOwnedResume(
                resumeId,
                user
        );

        if (resume.isDeleted()) {
            throw new BadRequestException(
                    "Deleted resumes cannot be updated"
            );
        }

        if (request.getTitle() != null) {
            resume.setTitle(
                    request.getTitle()
            );
        }

        if (request.getDescription() != null) {
            resume.setDescription(
                    request.getDescription()
            );
        }

        if (request.getFileUrl() != null) {
            resume.setFileUrl(
                    request.getFileUrl()
            );
        }

        if (request.getFileName() != null) {
            resume.setFileName(
                    request.getFileName()
            );
        }

        if (request.getFileType() != null) {
            resume.setFileType(
                    request.getFileType()
            );
        }

        if (request.getExtractedText() != null) {
            resume.setExtractedText(
                    request.getExtractedText()
            );
        }

        if (request.getVisibility() != null) {
            resume.setVisibility(
                    request.getVisibility()
            );
        }

        if (request.getPrimaryResume() != null) {

            if (Boolean.TRUE.equals(
                    request.getPrimaryResume()
            )) {
                removePrimaryResume(user);
            }

            resume.setPrimaryResume(
                    request.getPrimaryResume()
            );
        }

        resume.normalize();

        Resume saved = resumeRepository.save(resume);

        return ResumeResponse.from(saved);
    }

    public ResumeResponse activate(
            Long resumeId,
            String userEmail
    ) {

        User user = getUser(userEmail);

        Resume resume = getOwnedResume(
                resumeId,
                user
        );

        if (resume.isDeleted()) {
            throw new BadRequestException(
                    "Deleted resumes cannot be activated"
            );
        }

        resume.activate();

        Resume saved = resumeRepository.save(resume);

        return ResumeResponse.from(saved);
    }

    public ResumeResponse archive(
            Long resumeId,
            String userEmail
    ) {

        User user = getUser(userEmail);

        Resume resume = getOwnedResume(
                resumeId,
                user
        );

        if (resume.isDeleted()) {
            throw new BadRequestException(
                    "Deleted resume cannot be archived"
            );
        }

        resume.archive();

        Resume saved = resumeRepository.save(resume);

        return ResumeResponse.from(saved);
    }

    public ResumeResponse restore(
            Long resumeId,
            String userEmail
    ) {

        User user = getUser(userEmail);

        Resume resume = getOwnedResume(
                resumeId,
                user
        );

        if (!resume.isArchived() &&
                !resume.isDeleted()) {

            throw new BadRequestException(
                    "Only archived or deleted resumes can be restored"
            );
        }

        resume.restore();

        Resume saved = resumeRepository.save(resume);

        return ResumeResponse.from(saved);
    }

    public void delete(
            Long resumeId,
            String userEmail
    ) {

        User user = getUser(userEmail);

        Resume resume = getOwnedResume(
                resumeId,
                user
        );

        resume.delete();

        resumeRepository.save(resume);
    }

    public ResumeResponse makePrimary(
            Long resumeId,
            String userEmail
    ) {

        User user = getUser(userEmail);

        Resume resume = getOwnedResume(
                resumeId,
                user
        );

        if (resume.isDeleted()) {
            throw new BadRequestException(
                    "Deleted resume cannot be made primary"
            );
        }

        removePrimaryResume(user);

        resume.makePrimary();

        Resume saved = resumeRepository.save(resume);

        return ResumeResponse.from(saved);
    }

@Transactional(readOnly = true)
public ResumeResponse getPrimaryResume(
        String userEmail
) {

    User user = getUser(userEmail);

    Resume resume = resumeRepository
            .findFirstByUserAndPrimaryResumeTrue(user)
            .orElseThrow(() ->
                    new BadRequestException(
                            "Primary resume not found"
                    )
            );

    return ResumeResponse.from(resume);
}

    @Transactional(readOnly = true)
    public long countMyResumes(
            String userEmail
    ) {

        User user = getUser(userEmail);

        return resumeRepository.countByUser(user);
    }

    private User getUser(
            String userEmail
    ) {

        if (userEmail == null ||
                userEmail.isBlank()) {

            throw new BadRequestException(
                    "User email is required"
            );
        }

        return userRepository
                .findByEmail(userEmail.trim())
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found"
                        )
                );
    }

    private Resume getOwnedResume(
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
                        new BadRequestException(
                                "Resume not found"
                        )
                );
    }

    private void removePrimaryResume(
            User user
    ) {

        List<Resume> primaryResumes =
                resumeRepository
                        .findByUserAndPrimaryResumeTrue(user);

        if (primaryResumes.isEmpty()) {
            return;
        }

        for (Resume resume : primaryResumes) {
            resume.removePrimary();
        }

        resumeRepository.saveAll(primaryResumes);
    }

    private int normalizePage(int page) {

        return Math.max(page, 0);
    }

    private int normalizeSize(int size) {

        if (size < 1) {
            return 20;
        }

        return Math.min(size, 100);
    }
}