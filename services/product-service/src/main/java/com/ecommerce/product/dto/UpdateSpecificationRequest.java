
package com.ecommerce.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSpecificationRequest(

        @NotBlank(message = "Specification key is required")
        @Size(max = 100)
        String specificationKey,

        @NotBlank(message = "Specification value is required")
        @Size(max = 500)
        String specificationValue,

        @Size(max = 50)
        String unit

) {
}
