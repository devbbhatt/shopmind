
package com.ecommerce.product.specification;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductStatus;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasName(String name) {
        return (root, query, cb) ->
                name == null || name.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }

    public static Specification<Product> hasBrand(String brand) {
        return (root, query, cb) ->
                brand == null || brand.isBlank()
                        ? null
                        : cb.equal(
                        cb.lower(root.get("brand")),
                        brand.toLowerCase()
                );
    }

    public static Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, cb) ->
                status == null
                        ? null
                        : cb.equal(root.get("status"), status);
    }

    public static Specification<Product> minPrice(BigDecimal minPrice) {
        return (root, query, cb) ->
                minPrice == null
                        ? null
                        : cb.greaterThanOrEqualTo(
                        root.get("price"), minPrice);
    }

    public static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return (root, query, cb) ->
                maxPrice == null
                        ? null
                        : cb.lessThanOrEqualTo(
                        root.get("price"), maxPrice);
    }
}
