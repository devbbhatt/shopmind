package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getBrand(),
                product.getPrice(),
                product.getCurrency(),
                product.getStatus(),
                product.getCategory() != null
                        ? product.getCategory().getId()
                        : null,
                product.getCategory() != null
                        ? product.getCategory().getName()
                        : null,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}