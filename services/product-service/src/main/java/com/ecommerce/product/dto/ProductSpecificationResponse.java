
package com.ecommerce.product.dto;

import java.util.UUID;

public record ProductSpecificationResponse(

        UUID id,
        UUID productId,
        String specificationKey,
        String specificationValue,
        String unit

) {
}
