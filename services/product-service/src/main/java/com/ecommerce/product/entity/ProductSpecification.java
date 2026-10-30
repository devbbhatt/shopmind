
package com.ecommerce.product.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "product_specifications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_specification_key",
                        columnNames = {"product_id", "specification_key"}
                )
        }
)
public class ProductSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "specification_key", nullable = false, length = 100)
    private String specificationKey;

    @Column(name = "specification_value", nullable = false, length = 500)
    private String specificationValue;

    @Column(length = 50)
    private String unit;

    public ProductSpecification() {
    }

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getSpecificationKey() {
        return specificationKey;
    }

    public void setSpecificationKey(String specificationKey) {
        this.specificationKey = specificationKey;
    }

    public String getSpecificationValue() {
        return specificationValue;
    }

    public void setSpecificationValue(String specificationValue) {
        this.specificationValue = specificationValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
