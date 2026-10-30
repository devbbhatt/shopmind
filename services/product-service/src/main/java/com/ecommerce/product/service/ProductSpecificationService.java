
package com.ecommerce.product.service;

import com.ecommerce.product.dto.CreateSpecificationRequest;
import com.ecommerce.product.dto.UpdateSpecificationRequest;
import com.ecommerce.product.dto.ProductSpecificationResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductSpecification;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.mapper.ProductSpecificationMapper;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.ProductSpecificationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductSpecificationService {

    private final ProductRepository productRepository;
    private final ProductSpecificationRepository specificationRepository;
    private final ProductSpecificationMapper specificationMapper;

    public ProductSpecificationService(
            ProductRepository productRepository,
            ProductSpecificationRepository specificationRepository,
            ProductSpecificationMapper specificationMapper) {

        this.productRepository = productRepository;
        this.specificationRepository = specificationRepository;
        this.specificationMapper = specificationMapper;
    }

    public ProductSpecificationResponse addSpecification(
            UUID productId,
            CreateSpecificationRequest request) {

        Product product = getProduct(productId);

        boolean duplicate = specificationRepository
                .existsByProductIdAndSpecificationKeyIgnoreCase(
                        productId, request.specificationKey());

        if (duplicate) {
            throw new IllegalArgumentException(
                    "Specification key already exists for this product");
        }

        ProductSpecification specification = new ProductSpecification();

        specification.setProduct(product);
        specification.setSpecificationKey(request.specificationKey().trim());
        specification.setSpecificationValue(request.specificationValue().trim());
        specification.setUnit(request.unit());

        ProductSpecification saved =
                specificationRepository.save(specification);

        return specificationMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductSpecificationResponse> getSpecifications(UUID productId) {

        getProduct(productId);

        return specificationRepository.findByProductId(productId)
                .stream()
                .map(specificationMapper::toResponse)
                .toList();
    }

    public ProductSpecificationResponse updateSpecification(
            UUID productId,
            UUID specificationId,
            UpdateSpecificationRequest request) {

        getProduct(productId);

        ProductSpecification specification =
                getSpecification(productId, specificationId);

        boolean duplicate = specificationRepository
                .findByProductId(productId)
                .stream()
                .anyMatch(existing ->
                        !existing.getId().equals(specificationId)
                                && existing.getSpecificationKey()
                                .equalsIgnoreCase(request.specificationKey().trim()));

        if (duplicate) {
            throw new IllegalArgumentException(
                    "Specification key already exists for this product");
        }

        specification.setSpecificationKey(request.specificationKey().trim());
        specification.setSpecificationValue(request.specificationValue().trim());
        specification.setUnit(request.unit());

        return specificationMapper.toResponse(
                specificationRepository.save(specification));
    }

    public void deleteSpecification(UUID productId, UUID specificationId) {

        ProductSpecification specification =
                getSpecification(productId, specificationId);

        specificationRepository.delete(specification);
    }

    private Product getProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with ID: " + productId));
    }

    private ProductSpecification getSpecification(
            UUID productId, UUID specificationId) {

        return specificationRepository.findById(specificationId)
                .filter(spec -> spec.getProduct().getId().equals(productId))
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Specification not found with ID: " + specificationId));
    }
}
