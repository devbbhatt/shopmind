package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.*;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.entity.InventoryTransaction;
import com.ecommerce.inventory.enums.TransactionType;
import com.ecommerce.inventory.exception.BadRequestException;
import com.ecommerce.inventory.exception.ResourceNotFoundException;
import com.ecommerce.inventory.repository.InventoryRepository;
import com.ecommerce.inventory.repository.InventoryTransactionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;

    @Transactional
    public InventoryResponse createInventory(CreateInventoryRequest request) {

        if (inventoryRepository.existsByProductIdAndVariantId(
                request.getProductId(), request.getVariantId())) {
            throw new BadRequestException(
                    "Inventory already exists for this product variant");
        }

        Inventory inventory = new Inventory();
        inventory.setProductId(request.getProductId());
        inventory.setVariantId(request.getVariantId());
        inventory.setQuantityOnHand(request.getQuantityOnHand());
        inventory.setReservedQuantity(0);
        inventory.setLowStockThreshold(
                request.getLowStockThreshold() == null
                        ? 5 : request.getLowStockThreshold()
        );

        Inventory saved = inventoryRepository.save(inventory);

        if (saved.getQuantityOnHand() > 0) {
            saveTransaction(
                    saved,
                    TransactionType.STOCK_IN,
                    saved.getQuantityOnHand(),
                    null,
                    "Initial stock"
            );
        }

        return toInventoryResponse(saved);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryById(UUID inventoryId) {
        return toInventoryResponse(findInventory(inventoryId));
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductAndVariant(
            UUID productId, UUID variantId) {

        Inventory inventory = inventoryRepository
                .findByProductIdAndVariantId(productId, variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for this product variant"));

        return toInventoryResponse(inventory);
    }

    @Transactional
    public InventoryResponse addStock(
            UUID inventoryId, UpdateStockRequest request) {

        Inventory inventory = findInventoryForUpdate(inventoryId);

        inventory.setQuantityOnHand(
                inventory.getQuantityOnHand() + request.getQuantity()
        );

        Inventory saved = inventoryRepository.save(inventory);

        saveTransaction(
                saved,
                TransactionType.STOCK_IN,
                request.getQuantity(),
                null,
                request.getNotes()
        );

        return toInventoryResponse(saved);
    }

    @Transactional
    public InventoryResponse removeStock(
            UUID inventoryId, UpdateStockRequest request) {

        Inventory inventory = findInventoryForUpdate(inventoryId);

        if (inventory.getAvailableQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient available stock");
        }

        inventory.setQuantityOnHand(
                inventory.getQuantityOnHand() - request.getQuantity()
        );

        Inventory saved = inventoryRepository.save(inventory);

        saveTransaction(
                saved,
                TransactionType.STOCK_OUT,
                request.getQuantity(),
                null,
                request.getNotes()
        );

        return toInventoryResponse(saved);
    }

    @Transactional
    public InventoryResponse reserveStock(
            UUID inventoryId, ReserveStockRequest request) {

        Inventory inventory = findInventoryForUpdate(inventoryId);

        if (inventory.getAvailableQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock to reserve");
        }

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() + request.getQuantity()
        );

        Inventory saved = inventoryRepository.save(inventory);

        saveTransaction(
                saved,
                TransactionType.RESERVE,
                request.getQuantity(),
                request.getOrderId(),
                "Stock reserved for order"
        );

        return toInventoryResponse(saved);
    }

    @Transactional
    public InventoryResponse releaseStock(
            UUID inventoryId, ReserveStockRequest request) {

        Inventory inventory = findInventoryForUpdate(inventoryId);

        if (inventory.getReservedQuantity() < request.getQuantity()) {
            throw new BadRequestException(
                    "Release quantity exceeds reserved stock");
        }

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - request.getQuantity()
        );

        Inventory saved = inventoryRepository.save(inventory);

        saveTransaction(
                saved,
                TransactionType.RELEASE,
                request.getQuantity(),
                request.getOrderId(),
                "Reserved stock released"
        );

        return toInventoryResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getTransactionHistory(
            UUID inventoryId) {

        findInventory(inventoryId);

        return transactionRepository
                .findByInventoryIdOrderByCreatedAtDesc(inventoryId)
                .stream()
                .map(this::toTransactionResponse)
                .toList();
    }

    private Inventory findInventory(UUID inventoryId) {
        return inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found: " + inventoryId));
    }

    private Inventory findInventoryForUpdate(UUID inventoryId) {
        return inventoryRepository.findByIdForUpdate(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found: " + inventoryId));
    }

    private void saveTransaction(
            Inventory inventory,
            TransactionType type,
            Integer quantity,
            UUID referenceId,
            String notes) {

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setInventoryId(inventory.getId());
        transaction.setTransactionType(type);
        transaction.setQuantity(quantity);
        transaction.setReferenceId(referenceId);
        transaction.setNotes(notes);

        transactionRepository.save(transaction);
    }

    private InventoryResponse toInventoryResponse(Inventory inventory) {

        return new InventoryResponse(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getVariantId(),
                inventory.getQuantityOnHand(),
                inventory.getReservedQuantity(),
                inventory.getAvailableQuantity(),
                inventory.getLowStockThreshold(),
                inventory.getAvailableQuantity()
                        <= inventory.getLowStockThreshold(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }

    private InventoryTransactionResponse toTransactionResponse(
            InventoryTransaction transaction) {

        return new InventoryTransactionResponse(
                transaction.getId(),
                transaction.getInventoryId(),
                transaction.getTransactionType(),
                transaction.getQuantity(),
                transaction.getReferenceId(),
                transaction.getNotes(),
                transaction.getCreatedAt()
        );
    }
}