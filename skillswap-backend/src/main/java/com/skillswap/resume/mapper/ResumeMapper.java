package com.skillswap.resume.mapper;

import com.skillswap.resume.dto.ResumeResponse;
import com.skillswap.resume.dto.ResumeSummaryResponse;
import com.skillswap.resume.dto.ResumeVersionResponse;
import com.skillswap.resume.entity.Resume;
import com.skillswap.resume.entity.ResumeVersion;
import org.springframework.stereotype.Component;

@Component
public class ResumeMapper {

    public ResumeResponse toResponse(Resume resume) {

        if (resume == null) {
            return null;
        }

        return ResumeResponse.from(resume);
    }

    public ResumeSummaryResponse toSummaryResponse(
            Resume resume
    ) {

        if (resume == null) {
            return null;
        }

        return ResumeSummaryResponse.from(resume);
    }

    public ResumeVersionResponse toVersionResponse(
            ResumeVersion version
    ) {

        if (version == null) {
            return null;
        }

        return ResumeVersionResponse.from(version);
    }
}