package com.skillswap.company.mapper;

import com.skillswap.company.dto.CompanyMemberResponse;
import com.skillswap.company.entity.CompanyMember;
import org.springframework.stereotype.Component;

@Component
public class CompanyMemberMapper {

    public CompanyMemberResponse toResponse(CompanyMember member) {
        return CompanyMemberResponse.from(member);
    }
}