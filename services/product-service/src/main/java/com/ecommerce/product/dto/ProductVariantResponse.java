
package com.ecommerce.product.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProductVariantResponse(
        UUID id,
        UUID productId,
        String sku,
        BigDecimal price,
        String currency,
        boolean active,
        List<VariantAttributeRequest> attributes,
        Instant createdAt,
        Instant updatedAt
) {
}
