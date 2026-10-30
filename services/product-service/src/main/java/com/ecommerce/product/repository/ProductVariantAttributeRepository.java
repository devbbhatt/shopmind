
package com.ecommerce.product.repository;

import com.ecommerce.product.entity.ProductVariantAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductVariantAttributeRepository
        extends JpaRepository<ProductVariantAttribute, UUID> {

    List<ProductVariantAttribute> findByVariantId(UUID variantId);

    void deleteByVariantId(UUID variantId);
}
