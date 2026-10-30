package com.ecommerce.product.dto;

import com.ecommerce.product.entity.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        String sku,
        String brand,
        BigDecimal price,
        String currency,
        ProductStatus status,
        UUID categoryId,
        String categoryName,
        Instant createdAt,
        Instant updatedAt
) {
}