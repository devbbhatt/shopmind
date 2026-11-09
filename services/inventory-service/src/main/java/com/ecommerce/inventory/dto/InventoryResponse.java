package com.ecommerce.inventory.dto;

import java.time.Instant;
import java.util.UUID;

public record InventoryResponse(
        UUID id,
        UUID productId,
        UUID variantId,
        Integer quantityOnHand,
        Integer reservedQuantity,
        Integer availableQuantity,
        Integer lowStockThreshold,
        boolean lowStock,
        Instant createdAt,
        Instant updatedAt
) {}