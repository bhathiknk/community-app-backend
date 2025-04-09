package com.communityappbackend.Service;

import com.communityappbackend.DTO.CategoryRequest;
import com.communityappbackend.DTO.CategoryResponse;
import com.communityappbackend.Model.Category;
import com.communityappbackend.Repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
            throw new RuntimeException("Category already exists!");
        }
        Category saved = categoryRepo.save(Category.builder()
                .categoryName(request.getCategoryName())
                .build());
        return CategoryResponse.builder()
                .categoryId(saved.getCategoryId())
                .categoryName(saved.getCategoryName())
                .build();
    }
}
