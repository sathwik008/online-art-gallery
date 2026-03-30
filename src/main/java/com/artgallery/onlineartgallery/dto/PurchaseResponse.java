package com.artgallery.onlineartgallery.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseResponse(
        Long orderId,
        Long artworkId,
        String artworkTitle,
        String customerName,
        String customerEmail,
        BigDecimal purchasePrice,
        LocalDateTime purchasedAt,
        String status
) {
}
