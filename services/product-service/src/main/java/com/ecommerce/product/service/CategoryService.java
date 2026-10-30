
package com.ecommerce.product.service;

import com.ecommerce.product.dto.CreateCategoryRequest;
import com.ecommerce.product.dto.UpdateCategoryRequest;
import com.ecommerce.product.dto.CategoryResponse;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.entity.CategoryStatus;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.mapper.CategoryMapper;
import com.ecommerce.product.repository.CategoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public CategoryResponse createCategory(CreateCategoryRequest request) {

        if (categoryRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException(
                    "Category slug already exists");
        }

        Category category = new Category();

        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());

        if (request.parentId() != null) {
            Category parent = getCategoryEntity(request.parentId());

            if (parent.getStatus() != CategoryStatus.ACTIVE) {
                throw new IllegalArgumentException(
                        "Parent category must be active");
            }

            category.setParent(parent);
        }

        category.setStatus(CategoryStatus.ACTIVE);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {

        return categoryMapper.toResponse(getCategoryEntity(id));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getMainCategories() {

        return categoryRepository.findByParentIsNull()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubcategories(UUID parentId) {

        getCategoryEntity(parentId);

        return categoryRepository.findByParentId(parentId)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse updateCategory(
            UUID id,
            UpdateCategoryRequest request) {

        Category category = getCategoryEntity(id);

        categoryRepository.findBySlug(request.slug())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Category slug already exists");
                });

        if (request.parentId() != null) {

            if (request.parentId().equals(id)) {
                throw new IllegalArgumentException(
                        "Category cannot be its own parent");
            }

            Category parent = getCategoryEntity(request.parentId());

            if (parent.getStatus() != CategoryStatus.ACTIVE) {
                throw new IllegalArgumentException(
                        "Parent category must be active");
            }

            if (isDescendant(parent, id)) {
                throw new IllegalArgumentException(
                        "Circular category hierarchy is not allowed");
            }

            category.setParent(parent);

        } else {
            category.setParent(null);
        }

        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

    public CategoryResponse deactivateCategory(UUID id) {

        Category category = getCategoryEntity(id);

        category.setStatus(CategoryStatus.INACTIVE);

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

    private Category getCategoryEntity(UUID id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Category not found with ID: " + id));
    }

    private boolean isDescendant(Category category, UUID ancestorId) {

        Category current = category;

        while (current != null) {

            if (current.getId().equals(ancestorId)) {
                return true;
            }

            current = current.getParent();
        }

        return false;
    }
}
