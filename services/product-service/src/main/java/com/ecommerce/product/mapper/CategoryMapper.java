
package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.CategoryResponse;
import com.ecommerce.product.entity.Category;

import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getParent() != null
                        ? category.getParent().getId()
                        : null,
                category.getParent() != null
                        ? category.getParent().getName()
                        : null,
                category.getStatus(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
