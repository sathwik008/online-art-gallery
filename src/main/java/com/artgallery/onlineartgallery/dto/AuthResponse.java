package com.artgallery.onlineartgallery.dto;

public record AuthResponse(
        String message,
        UserResponse user
) {
}
