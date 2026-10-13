package com.skillswap.college.mapper;

import com.skillswap.college.dto.CollegeMemberResponse;
import com.skillswap.college.entity.CollegeMember;
import org.springframework.stereotype.Component;

@Component
public class CollegeMemberMapper {

    public CollegeMemberResponse toResponse(CollegeMember member) {
        if (member == null) {
            return null;
        }

        return CollegeMemberResponse.from(member);
    }
}