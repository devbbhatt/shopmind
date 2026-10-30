
package com.ecommerce.product.controller;

import com.ecommerce.product.dto.CreateCategoryRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.dto.UpdateCategoryRequest;
import com.ecommerce.product.dto.CategoryResponse;
import com.ecommerce.product.service.CategoryService;
import com.ecommerce.product.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(
            CategoryService categoryService,
            ProductService productService) {

        this.categoryService = categoryService;
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {

        return categoryService.createCategory(request);
    }

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/main")
    public List<CategoryResponse> getMainCategories() {
        return categoryService.getMainCategories();
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable UUID id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/{parentId}/subcategories")
    public List<CategoryResponse> getSubcategories(
            @PathVariable UUID parentId) {

        return categoryService.getSubcategories(parentId);
    }

    @PutMapping("/{id}")
    public CategoryResponse updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request) {

        return categoryService.updateCategory(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public CategoryResponse deactivateCategory(@PathVariable UUID id) {
        return categoryService.deactivateCategory(id);
    }

    @GetMapping("/{id}/products")
    public Page<ProductResponse> getCategoryProducts(
            @PathVariable UUID id,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        return productService.getProductsByCategory(id, pageable);
    }
}
