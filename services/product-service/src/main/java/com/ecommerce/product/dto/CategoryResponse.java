
package com.ecommerce.product.dto;

import com.ecommerce.product.entity.CategoryStatus;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(

        UUID id,
        String name,
        String slug,
        String description,
        UUID parentId,
        String parentName,
        CategoryStatus status,
        Instant createdAt,
        Instant updatedAt

) {
}
