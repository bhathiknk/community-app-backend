package com.communityappbackend.service.CategoryService;

import com.communityappbackend.dto.CategoryHandleDTOs.CategoryRequest;
import com.communityappbackend.dto.CategoryHandleDTOs.CategoryResponse;
import com.communityappbackend.exception.CategoryAlreadyExistsException;
import com.communityappbackend.model.Category;
import com.communityappbackend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides CRUD operations for categories.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepo;

    public CategoryService(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepo.findAll().stream()
                .map(cat -> CategoryResponse.builder()
                        .categoryId(cat.getCategoryId())
                        .categoryName(cat.getCategoryName())
                        .build())
                .collect(Collectors.toList());
    }

    public CategoryResponse addCategory(CategoryRequest request) {
        if (categoryRepo.existsByCategoryName(request.getCategoryName())) {
            throw new CategoryAlreadyExistsException("Category already exists: " + request.getCategoryName());
        }
        Category saved = categoryRepo.save(
                Category.builder().categoryName(request.getCategoryName()).build()
        );
        return CategoryResponse.builder()
                .categoryId(saved.getCategoryId())
                .categoryName(saved.getCategoryName())
                .build();
    }
}
