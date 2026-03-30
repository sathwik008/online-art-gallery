package com.artgallery.onlineartgallery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PurchaseRequest(
        @NotBlank(message = "Customer name is required")
        @Size(max = 120, message = "Customer name must be under 120 characters")
        String customerName,

        @NotBlank(message = "Customer email is required")
        @Email(message = "Customer email must be valid")
        @Size(max = 160, message = "Customer email must be under 160 characters")
        String customerEmail
) {
}
