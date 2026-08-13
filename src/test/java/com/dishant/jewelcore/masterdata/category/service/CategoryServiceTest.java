package com.dishant.jewelcore.masterdata.category.service;

import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.masterdata.category.dto.CategoryCreateRequest;
import com.dishant.jewelcore.masterdata.category.dto.CategoryUpdateRequest;
import com.dishant.jewelcore.masterdata.category.entity.Category;
import com.dishant.jewelcore.masterdata.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldCreateCategoryWhenCodeDoesNotExist() {

        CategoryCreateRequest request = new CategoryCreateRequest(
                "Rings",
                "RING"
        );

        when(categoryRepository.findByCode("RING"))
                .thenReturn(Optional.empty());

        Category savedCategory = new Category(
                "Rings",
                "RING"
        );

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(savedCategory);

        var result = categoryService.createCategory(request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Rings");
        assertThat(result.code()).isEqualTo("RING");
        assertThat(result.active()).isTrue();

        verify(categoryRepository).findByCode("RING");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void shouldRejectCategoryWhenCodeAlreadyExists() {

        Category existingCategory = new Category(
                "Rings",
                "RING"
        );

        when(categoryRepository.findByCode("RING"))
                .thenReturn(Optional.of(existingCategory));

        CategoryCreateRequest request = new CategoryCreateRequest(
                "Another Ring Category",
                "RING"
        );

        assertThatThrownBy(() ->
                categoryService.createCategory(request)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category code already exists: RING");

        verify(categoryRepository).findByCode("RING");
        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void shouldGetCategoryById() {

        Category category = new Category(
                "Rings",
                "RING"
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        var result = categoryService.getCategoryById(1L);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Rings");
        assertThat(result.code()).isEqualTo("RING");
        assertThat(result.active()).isTrue();

        verify(categoryRepository).findById(1L);
    }

    @Test
    void shouldRejectGetCategoryWhenCategoryDoesNotExist() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.getCategoryById(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found with id: 1");

        verify(categoryRepository).findById(1L);
    }

    @Test
    void shouldUpdateCategory() {

        Category category = new Category(
                "Rings",
                "RING"
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.findByCode("RING"))
                .thenReturn(Optional.empty());

        when(categoryRepository.save(category))
                .thenReturn(category);

        CategoryUpdateRequest request = new CategoryUpdateRequest(
                "Gold Rings",
                "RING"
        );

        var result = categoryService.updateCategory(1L, request);

        assertThat(result.name()).isEqualTo("Gold Rings");
        assertThat(result.code()).isEqualTo("RING");

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).findByCode("RING");
        verify(categoryRepository).save(category);
    }

    @Test
    void shouldRejectUpdateWhenNewCodeBelongsToAnotherCategory() {

        Category currentCategory = new Category(
                "Rings",
                "RING"
        );

        Category existingCategory =
                org.mockito.Mockito.mock(Category.class);

        when(existingCategory.getId())
                .thenReturn(2L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(currentCategory));

        when(categoryRepository.findByCode("NECK"))
                .thenReturn(Optional.of(existingCategory));

        CategoryUpdateRequest request = new CategoryUpdateRequest(
                "Rings",
                "NECK"
        );

        assertThatThrownBy(() ->
                categoryService.updateCategory(1L, request)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category code already exists: NECK");

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).findByCode("NECK");

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void shouldDeactivateActiveCategory() {

        Category category = new Category(
                "Rings",
                "RING"
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.deactivateCategory(1L);

        assertThat(category.isActive()).isFalse();

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(category);
    }

    @Test
    void shouldRejectDeactivationWhenCategoryDoesNotExist() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.deactivateCategory(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found with id: 1");

        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void shouldRejectDeactivationWhenCategoryIsAlreadyInactive() {

        Category category = new Category(
                "Rings",
                "RING"
        );

        category.deactivate();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        assertThatThrownBy(() ->
                categoryService.deactivateCategory(1L)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Category is already inactive: 1");

        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never())
                .save(any(Category.class));
    }
}