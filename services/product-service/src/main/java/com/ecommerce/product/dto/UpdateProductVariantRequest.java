
package com.ecommerce.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductVariantRequest(

        @NotBlank
        @Size(max = 80)
        String sku,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @NotBlank
        @Pattern(regexp = "^[A-Z]{3}$")
        String currency,

        @NotEmpty
        List<@Valid VariantAttributeRequest> attributes

) {
}
