package com.skillswap.admin.mapper;

import com.skillswap.admin.dto.AdminUserResponse;
import com.skillswap.identity.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    public AdminUserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return AdminUserResponse.from(user);
    }
}