package com.skillswap.college.mapper;

import com.skillswap.college.dto.CollegeResponse;
import com.skillswap.college.entity.College;
import org.springframework.stereotype.Component;

@Component
public class CollegeMapper {

    public CollegeResponse toResponse(College college) {
        return CollegeResponse.from(college);
    }
}