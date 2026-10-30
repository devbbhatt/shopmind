
package com.ecommerce.product.controller;

import com.ecommerce.product.dto.CreateProductVariantRequest;
import com.ecommerce.product.dto.UpdateProductVariantRequest;
import com.ecommerce.product.dto.ProductVariantResponse;
import com.ecommerce.product.service.ProductVariantService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/{productId}/variants")
public class ProductVariantController {

    private final ProductVariantService variantService;

    public ProductVariantController(
            ProductVariantService variantService) {
        this.variantService = variantService;
    }

    // Create Product Variant
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductVariantResponse createVariant(
            @PathVariable UUID productId,
            @Valid @RequestBody CreateProductVariantRequest request) {

        return variantService.createVariant(productId, request);
    }

    // Get All Variants of a Product
    @GetMapping
    public List<ProductVariantResponse> getVariants(
            @PathVariable UUID productId) {

        return variantService.getVariants(productId);
    }

    // Update Product Variant
    @PutMapping("/{variantId}")
    public ProductVariantResponse updateVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId,
            @Valid @RequestBody UpdateProductVariantRequest request) {

        return variantService.updateVariant(
                productId,
                variantId,
                request
        );
    }

    // Deactivate Product Variant
    @PatchMapping("/{variantId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId) {

        variantService.deactivateVariant(productId, variantId);
    }
}
