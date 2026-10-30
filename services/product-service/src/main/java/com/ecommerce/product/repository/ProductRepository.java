
package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends
        JpaRepository<Product, UUID>,
        JpaSpecificationExecutor<Product> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(
            String name, Pageable pageable);

    Page<Product> findByBrandIgnoreCase(
            String brand, Pageable pageable);

    Page<Product> findByCategoryId(UUID categoryId, Pageable pageable);
}
