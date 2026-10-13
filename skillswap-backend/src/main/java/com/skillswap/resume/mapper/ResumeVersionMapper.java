package com.skillswap.resume.mapper;

import com.skillswap.resume.dto.ResumeVersionResponse;
import com.skillswap.resume.entity.ResumeVersion;
import org.springframework.stereotype.Component;

@Component
public class ResumeVersionMapper {

    public ResumeVersionResponse toResponse(ResumeVersion version) {

        if (version == null) {
            return null;
        }

        return ResumeVersionResponse.from(version);
    }
}