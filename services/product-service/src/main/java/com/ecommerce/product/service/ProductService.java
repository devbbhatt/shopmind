
package com.ecommerce.product.service;

import com.ecommerce.product.dto.CreateProductRequest;
import com.ecommerce.product.dto.UpdateProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.CategoryStatus;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductStatus;
import com.ecommerce.product.exception.DuplicateSkuException;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.specification.ProductSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.product.entity.Category;
import com.ecommerce.product.repository.CategoryRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            ProductMapper productMapper,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse createProduct(CreateProductRequest request) {

        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException("Product SKU already exists");
        }

        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setSku(request.sku());
        product.setBrand(request.brand());
        product.setPrice(request.price());
        product.setCurrency(request.currency());
        product.setStatus(ProductStatus.ACTIVE);
        product.setCategory(getActiveCategory(request.categoryId()));

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with ID: " + id
                        ));

        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {

        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    public ProductResponse updateProduct(UUID id,
                                         UpdateProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with ID: " + id
                        ));

        boolean skuUsedByAnotherProduct =
                productRepository.findBySku(request.sku())
                        .filter(existing -> !existing.getId().equals(id))
                        .isPresent();

        if (skuUsedByAnotherProduct) {
            throw new DuplicateSkuException("Product SKU already exists");
        }

        product.setName(request.name());
        product.setDescription(request.description());
        product.setSku(request.sku());
        product.setBrand(request.brand());
        product.setPrice(request.price());
        product.setCurrency(request.currency());
        product.setCategory(getActiveCategory(request.categoryId()));

        Product updatedProduct = productRepository.save(product);

        return productMapper.toResponse(updatedProduct);
    }

    public ProductResponse deactivateProduct(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with ID: " + id
                        ));

        product.setStatus(ProductStatus.INACTIVE);

        Product updatedProduct = productRepository.save(product);

        return productMapper.toResponse(updatedProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(
            String name,
            String brand,
            ProductStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        if (minPrice != null && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException(
                    "Minimum price cannot exceed maximum price");
        }

        Specification<Product> specification =
                Specification.where(ProductSpecification.hasName(name))
                        .and(ProductSpecification.hasBrand(brand))
                        .and(ProductSpecification.hasStatus(status))
                        .and(ProductSpecification.minPrice(minPrice))
                        .and(ProductSpecification.maxPrice(maxPrice));

        return productRepository.findAll(specification, pageable)
                .map(productMapper::toResponse);
    }

    private Category getActiveCategory(UUID categoryId) {

        if (categoryId == null) {
            return null;
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Category not found with ID: " + categoryId));

        if (category.getStatus() != CategoryStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Cannot assign product to an inactive category");
        }

        return category;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(
            UUID categoryId,
            Pageable pageable) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new ProductNotFoundException(
                    "Category not found with ID: " + categoryId
            );
        }

        return productRepository.findByCategoryId(categoryId, pageable)
                .map(productMapper::toResponse);
    }
}
