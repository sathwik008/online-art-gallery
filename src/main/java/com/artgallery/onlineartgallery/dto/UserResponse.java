package com.artgallery.onlineartgallery.dto;

import com.artgallery.onlineartgallery.model.UserRole;

public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role
) {
}
