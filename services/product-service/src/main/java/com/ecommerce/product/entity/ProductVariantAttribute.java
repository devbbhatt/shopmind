
package com.ecommerce.product.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
        name = "product_variant_attributes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_variant_attribute_key",
                columnNames = {"variant_id", "attribute_key"}
        )
)
public class ProductVariantAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "attribute_key", nullable = false, length = 100)
    private String attributeKey;

    @Column(name = "attribute_value", nullable = false, length = 250)
    private String attributeValue;

    public ProductVariantAttribute() {
    }

    public UUID getId() {
        return id;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
}
