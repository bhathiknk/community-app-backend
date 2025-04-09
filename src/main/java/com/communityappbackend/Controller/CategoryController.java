package com.communityappbackend.Controller;

import com.communityappbackend.DTO.CategoryRequest;
import com.communityappbackend.DTO.CategoryResponse;
import com.communityappbackend.Service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getAll() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    public CategoryResponse add(@RequestBody CategoryRequest request) {
        return categoryService.addCategory(request);
    }
}
