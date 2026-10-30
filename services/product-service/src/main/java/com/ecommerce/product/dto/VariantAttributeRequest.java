
package com.ecommerce.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VariantAttributeRequest(

        @NotBlank
        @Size(max = 100)
        String attributeKey,

        @NotBlank
        @Size(max = 250)
        String attributeValue

) {
}
