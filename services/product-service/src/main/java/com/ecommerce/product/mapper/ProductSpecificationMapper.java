
package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.ProductSpecificationResponse;
import com.ecommerce.product.entity.ProductSpecification;

import org.springframework.stereotype.Component;

@Component
public class ProductSpecificationMapper {

    public ProductSpecificationResponse toResponse(
            ProductSpecification specification) {

        return new ProductSpecificationResponse(
                specification.getId(),
                specification.getProduct().getId(),
                specification.getSpecificationKey(),
                specification.getSpecificationValue(),
                specification.getUnit()
        );
    }
}
