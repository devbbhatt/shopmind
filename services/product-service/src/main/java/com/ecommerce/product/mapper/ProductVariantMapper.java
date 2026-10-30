
package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.*;
import com.ecommerce.product.entity.ProductVariant;
import com.ecommerce.product.entity.ProductVariantAttribute;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductVariantMapper {

    public ProductVariantResponse toResponse(
            ProductVariant variant,
            List<ProductVariantAttribute> attributes) {

        List<VariantAttributeRequest> attributeResponses =
                attributes.stream()
                        .map(attribute -> new VariantAttributeRequest(
                                attribute.getAttributeKey(),
                                attribute.getAttributeValue()))
                        .toList();

        return new ProductVariantResponse(
                variant.getId(),
                variant.getProduct().getId(),
                variant.getSku(),
                variant.getPrice(),
                variant.getCurrency(),
                variant.isActive(),
                attributeResponses,
                variant.getCreatedAt(),
                variant.getUpdatedAt()
        );
    }
}
