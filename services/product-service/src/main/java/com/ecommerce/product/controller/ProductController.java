
package com.ecommerce.product.controller;

import com.ecommerce.product.dto.CreateProductRequest;
import com.ecommerce.product.dto.UpdateProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.ProductStatus;
import com.ecommerce.product.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        return productService.createProduct(request);
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable UUID id) {

        return productService.getProductById(id);
    }

    @GetMapping
    public Page<ProductResponse> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        return productService.getProducts(
                name, brand, status, minPrice, maxPrice, pageable
        );
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {

        return productService.updateProduct(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public ProductResponse deactivateProduct(@PathVariable UUID id) {

        return productService.deactivateProduct(id);
    }
}
