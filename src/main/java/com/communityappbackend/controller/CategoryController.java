package com.communityappbackend.controller;

import com.communityappbackend.dto.CategoryRequest;
import com.communityappbackend.dto.CategoryResponse;
import com.communityappbackend.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles CRUD operations on item categories.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Fetch all categories.
     */
    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    /**
     * Create a new category.
     */
    @PostMapping
    public CategoryResponse addCategory(@RequestBody CategoryRequest request) {
        return categoryService.addCategory(request);
    }
}
