
package com.ecommerce.product.controller;

import com.ecommerce.product.dto.CreateSpecificationRequest;
import com.ecommerce.product.dto.UpdateSpecificationRequest;
import com.ecommerce.product.dto.ProductSpecificationResponse;
import com.ecommerce.product.service.ProductSpecificationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/{productId}/specifications")
public class ProductSpecificationController {

    private final ProductSpecificationService specificationService;

    public ProductSpecificationController(
            ProductSpecificationService specificationService) {
        this.specificationService = specificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductSpecificationResponse addSpecification(
            @PathVariable UUID productId,
            @Valid @RequestBody CreateSpecificationRequest request) {

        return specificationService.addSpecification(productId, request);
    }

    @GetMapping
    public List<ProductSpecificationResponse> getSpecifications(
            @PathVariable UUID productId) {

        return specificationService.getSpecifications(productId);
    }

    @PutMapping("/{specificationId}")
    public ProductSpecificationResponse updateSpecification(
            @PathVariable UUID productId,
            @PathVariable UUID specificationId,
            @Valid @RequestBody UpdateSpecificationRequest request) {

        return specificationService.updateSpecification(
                productId, specificationId, request);
    }

    @DeleteMapping("/{specificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpecification(
            @PathVariable UUID productId,
            @PathVariable UUID specificationId) {

        specificationService.deleteSpecification(productId, specificationId);
    }
}
