package com.library.service;

import com.library.dto.CategoryRequest;
import com.library.dto.CategoryResponse;
import com.library.model.Category;
import com.library.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return toResponse(category);
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();

        category.setName(request.getName());

        Category savedCategory = categoryRepository.save(category);

        return toResponse(savedCategory);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }
}