package com.skillswap.company.mapper;

import com.skillswap.company.dto.CompanyResponse;
import com.skillswap.company.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyResponse toResponse(
            Company company
    ) {
        return CompanyResponse.from(company);
    }
}