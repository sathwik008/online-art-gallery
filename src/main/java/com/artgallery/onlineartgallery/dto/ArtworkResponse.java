package com.artgallery.onlineartgallery.dto;

import com.artgallery.onlineartgallery.model.ArtworkStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ArtworkResponse(
        Long id,
        String title,
        String artistName,
        String category,
        String description,
        String imageUrl,
        BigDecimal price,
        boolean featured,
        ArtworkStatus status,
        LocalDateTime createdAt
) {
}
