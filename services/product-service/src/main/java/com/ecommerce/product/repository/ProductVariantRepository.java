
package com.ecommerce.product.repository;

import com.ecommerce.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductVariantRepository
        extends JpaRepository<ProductVariant, UUID> {

    List<ProductVariant> findByProductId(UUID productId);

    boolean existsBySku(String sku);
}
