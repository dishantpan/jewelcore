package com.dishant.jewelcore.masterdata.category.service;

import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.masterdata.category.dto.CategoryCreateRequest;
import com.dishant.jewelcore.masterdata.category.dto.CategoryResponse;
import com.dishant.jewelcore.masterdata.category.dto.CategoryUpdateRequest;
import com.dishant.jewelcore.masterdata.category.entity.Category;
import com.dishant.jewelcore.masterdata.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(
            CategoryCreateRequest request) {

        if (categoryRepository.findByCode(request.code()).isPresent()) {
            throw new IllegalArgumentException(
                    "Category code already exists: " + request.code()
            );
        }

        Category category = new Category(
                request.name(),
                request.code()
        );

        Category savedCategory =
                categoryRepository.save(category);

        return CategoryResponse.from(savedCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id
                ));

        return CategoryResponse.from(category);
    }

    public CategoryResponse updateCategory(
            Long id,
            CategoryUpdateRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id
                ));

        categoryRepository.findByCode(request.code())
                .filter(existingCategory ->
                        !Objects.equals(existingCategory.getId(), id))
                .ifPresent(existingCategory -> {
                    throw new IllegalArgumentException(
                            "Category code already exists: "
                                    + request.code()
                    );
                });

        category.updateDetails(
                request.name(),
                request.code()
        );

        Category updatedCategory =
                categoryRepository.save(category);

        return CategoryResponse.from(updatedCategory);
    }

    public void deactivateCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id
                ));

        if (!category.isActive()) {
            throw new IllegalStateException(
                    "Category is already inactive: " + id
            );
        }

        category.deactivate();

        categoryRepository.save(category);
    }
}