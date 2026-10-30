package com.ecommerce.product.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 2000)
        String description,

        @NotBlank
        @Size(max = 80)
        String sku,

        @Size(max = 100)
        String brand,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @NotBlank
        @Pattern(regexp = "^[A-Z]{3}$")
        String currency,

        UUID categoryId

) {
}