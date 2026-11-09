package com.ecommerce.inventory.dto;

import com.ecommerce.inventory.enums.TransactionType;

import java.time.Instant;
import java.util.UUID;

public record InventoryTransactionResponse(
        UUID id,
        UUID inventoryId,
        TransactionType transactionType,
        Integer quantity,
        UUID referenceId,
        String notes,
        Instant createdAt
) {}