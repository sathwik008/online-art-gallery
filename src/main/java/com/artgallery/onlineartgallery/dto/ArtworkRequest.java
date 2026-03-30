package com.artgallery.onlineartgallery.dto;

import com.artgallery.onlineartgallery.model.ArtworkStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ArtworkRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 120, message = "Title must be under 120 characters")
        String title,

        @NotBlank(message = "Artist name is required")
        @Size(max = 120, message = "Artist name must be under 120 characters")
        String artistName,

        @NotBlank(message = "Category is required")
        @Size(max = 60, message = "Category must be under 60 characters")
        String category,

        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Description must be under 500 characters")
        String description,

        @NotBlank(message = "Image URL is required")
        String imageUrl,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
        BigDecimal price,

        boolean featured,

        @NotNull(message = "Status is required")
        ArtworkStatus status
) {
}
