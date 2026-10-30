
package com.ecommerce.product.repository;

import com.ecommerce.product.entity.ProductSpecification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductSpecificationRepository
        extends JpaRepository<ProductSpecification, UUID> {

    List<ProductSpecification> findByProductId(UUID productId);

    boolean existsByProductIdAndSpecificationKeyIgnoreCase(
            UUID productId,
            String specificationKey
    );
}
