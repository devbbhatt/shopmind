package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.*;
import com.ecommerce.inventory.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(
            @Valid @RequestBody CreateInventoryRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.createInventory(request));
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<InventoryResponse> getInventory(
            @PathVariable UUID inventoryId) {

        return ResponseEntity.ok(
                inventoryService.getInventoryById(inventoryId));
    }

    @GetMapping("/product/{productId}/variant/{variantId}")
    public ResponseEntity<InventoryResponse> getByProductAndVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId) {

        return ResponseEntity.ok(
                inventoryService.getInventoryByProductAndVariant(
                        productId, variantId));
    }

    @PostMapping("/{inventoryId}/stock-in")
    public ResponseEntity<InventoryResponse> addStock(
            @PathVariable UUID inventoryId,
            @Valid @RequestBody UpdateStockRequest request) {

        return ResponseEntity.ok(
                inventoryService.addStock(inventoryId, request));
    }

    @PostMapping("/{inventoryId}/stock-out")
    public ResponseEntity<InventoryResponse> removeStock(
            @PathVariable UUID inventoryId,
            @Valid @RequestBody UpdateStockRequest request) {

        return ResponseEntity.ok(
                inventoryService.removeStock(inventoryId, request));
    }

    @PostMapping("/{inventoryId}/reserve")
    public ResponseEntity<InventoryResponse> reserveStock(
            @PathVariable UUID inventoryId,
            @Valid @RequestBody ReserveStockRequest request) {

        return ResponseEntity.ok(
                inventoryService.reserveStock(inventoryId, request));
    }

    @PostMapping("/{inventoryId}/release")
    public ResponseEntity<InventoryResponse> releaseStock(
            @PathVariable UUID inventoryId,
            @Valid @RequestBody ReserveStockRequest request) {

        return ResponseEntity.ok(
                inventoryService.releaseStock(inventoryId, request));
    }

    @GetMapping("/{inventoryId}/transactions")
    public ResponseEntity<List<InventoryTransactionResponse>>
    getTransactionHistory(@PathVariable UUID inventoryId) {

        return ResponseEntity.ok(
                inventoryService.getTransactionHistory(inventoryId));
    }
}