
package com.ecommerce.product.service;

import com.ecommerce.product.dto.*;
import com.ecommerce.product.entity.*;
import com.ecommerce.product.exception.*;
import com.ecommerce.product.mapper.ProductVariantMapper;
import com.ecommerce.product.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductVariantService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantAttributeRepository attributeRepository;
    private final ProductVariantMapper variantMapper;

    public ProductVariantService(
            ProductRepository productRepository,
            ProductVariantRepository variantRepository,
            ProductVariantAttributeRepository attributeRepository,
            ProductVariantMapper variantMapper) {

        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.attributeRepository = attributeRepository;
        this.variantMapper = variantMapper;
    }

    public ProductVariantResponse createVariant(
            UUID productId,
            CreateProductVariantRequest request) {

        Product product = getProduct(productId);

        if (variantRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException("Variant SKU already exists");
        }

        validateAttributes(request.attributes());

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(request.sku().trim());
        variant.setPrice(request.price());
        variant.setCurrency(request.currency());

        ProductVariant saved = variantRepository.save(variant);

        saveAttributes(saved, request.attributes());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductVariantResponse> getVariants(UUID productId) {

        getProduct(productId);

        return variantRepository.findByProductId(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductVariantResponse updateVariant(
            UUID productId,
            UUID variantId,
            UpdateProductVariantRequest request) {

        ProductVariant variant = getVariant(productId, variantId);

        // Check duplicate SKU, excluding current variant
        variantRepository.findAll().stream()
                .filter(existing -> existing.getSku()
                        .equalsIgnoreCase(request.sku().trim()))
                .filter(existing -> !existing.getId().equals(variantId))
                .findFirst()
                .ifPresent(existing -> {
                    throw new DuplicateSkuException(
                            "Variant SKU already exists");
                });

        // Validate attributes
        validateAttributes(request.attributes());

        // Update variant details
        variant.setSku(request.sku().trim());
        variant.setPrice(request.price());
        variant.setCurrency(request.currency());

        // Delete old attributes
        attributeRepository.deleteByVariantId(variantId);

        // IMPORTANT: Execute deletes before inserting new attributes
        attributeRepository.flush();

        // Save updated attributes
        saveAttributes(variant, request.attributes());

        // Save variant
        ProductVariant updatedVariant = variantRepository.save(variant);

        return toResponse(updatedVariant);
    }

    public void deactivateVariant(UUID productId, UUID variantId) {

        ProductVariant variant = getVariant(productId, variantId);
        variant.setActive(false);
        variantRepository.save(variant);
    }

    private void saveAttributes(
            ProductVariant variant,
            List<VariantAttributeRequest> attributes) {

        for (VariantAttributeRequest request : attributes) {

            ProductVariantAttribute attribute =
                    new ProductVariantAttribute();

            attribute.setVariant(variant);
            attribute.setAttributeKey(request.attributeKey().trim());
            attribute.setAttributeValue(request.attributeValue().trim());

            attributeRepository.save(attribute);
        }
    }

    private void validateAttributes(
            List<VariantAttributeRequest> attributes) {

        long distinctKeys = attributes.stream()
                .map(a -> a.attributeKey().trim().toLowerCase())
                .distinct()
                .count();

        if (distinctKeys != attributes.size()) {
            throw new IllegalArgumentException(
                    "Duplicate variant attribute keys are not allowed");
        }
    }

    private ProductVariantResponse toResponse(ProductVariant variant) {
        return variantMapper.toResponse(
                variant,
                attributeRepository.findByVariantId(variant.getId()));
    }

    private Product getProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with ID: " + productId));
    }

    private ProductVariant getVariant(UUID productId, UUID variantId) {
        getProduct(productId);

        return variantRepository.findById(variantId)
                .filter(v -> v.getProduct().getId().equals(productId))
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Variant not found with ID: " + variantId));
    }
}
